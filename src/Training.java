import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Training {
    static double INF = 1e9;

    public static double initEval(GameState game) {
        ArrayList<Integer> opinionCounts = game.getOpinionCounts();
        int goodCount = opinionCounts.get(0);
        int badCount = opinionCounts.get(1);
        double proportionGood = (double)goodCount / (goodCount + badCount);
        double proportionEnergy = (double)game.bluePlayer.energy / 100;
        if (proportionEnergy < 0.1)
            proportionEnergy = 0.1;
        double proportionFollowers = (double)game.redPlayer.greenFollowers.size() / game.ids_that_have_a_node.size();
        if (proportionFollowers < 0.1)
            proportionFollowers = 0.1;

        return proportionGood * (proportionEnergy / proportionFollowers);
    }

    public static ArrayList<Double> run(GameState game, boolean turn, int depthRemaining) {
        if (depthRemaining == 0)
            return new ArrayList<Double>(Arrays.asList(-2.0, initEval(game)));
        int bestPotency = 0;
        double bestEval;
        int num_potencies;
        if (turn) {
            bestEval = -INF;
            num_potencies = BlueAgent.uncertaintyForEachPotency.size() - 1;
            for (int message_potency = 1; message_potency <= num_potencies; message_potency++) {
                if (game.bluePlayer.energy < BlueAgent.energyLostForEachPotency.get(message_potency))
                    break;
                GameState newGame = new GameState(game, true);
                newGame.bluePlayer.doMove(newGame, message_potency);
                double thisEval = run(newGame, false, depthRemaining - 1).get(1);
                if (thisEval > bestEval) {
                    bestEval = thisEval;
                    bestPotency = message_potency;
                }
            }
            if (bestEval == -INF) { // If we did not have enough energy to spread any message
                GameState newGame = new GameState(game, true);
                double thisEval = run(newGame, false, depthRemaining - 1).get(1);
                if (thisEval > bestEval) {
                    bestEval = thisEval;
                    bestPotency = 0;
                }
            }
        }
        else {
            bestEval = INF;
            num_potencies = RedAgent.uncertaintyForEachPotency.size() - 1;
            for (int message_potency = 1; message_potency <= num_potencies; message_potency++) {
                GameState newGame = new GameState(game, true);
                newGame.redPlayer.doMove(newGame, message_potency);
                double thisEval = run(newGame, true, depthRemaining - 1).get(1);
                if (thisEval < bestEval) {
                    bestEval = thisEval;
                    bestPotency = message_potency;
                }
            }
        }
        return new ArrayList<Double>(Arrays.asList((double)bestPotency, bestEval));
    }

    public static void addToBlueLearningData(GameState sampleGameState, int bestMoveBlue, BlueAgent bluePlayer) {
        bluePlayer.learningData.put(sampleGameState, bestMoveBlue);
    }

    public static void addToRedLearningData(GameState sampleGameState, int bestMoveRed, RedAgent redPlayer) {
        redPlayer.learningData.put(sampleGameState, bestMoveRed);
    }

    public static void trainOnGames(GameState gameForWhichWeAreTraining) {
        GameState x = gameForWhichWeAreTraining;
        int numNodes = x.ids_that_have_a_node.size();
        int numGames = 1500 / numNodes; // For each single game, much learning data will be acquired
        if (numGames > 200)
            numGames = 200;
        if (numGames < 7)
            numGames = 7;
        int depth;
        if (numNodes <= 500)
            depth = 4;
        else
            depth = 3;
        for (int i = 0; i < numGames; i++) {
            GameState game = new GameState(x, false);
            App.addProportionOfEdges(game, Math.random());
            while (true) {
                int bestMoveBlue = (int)Math.round(run(game, true, depth).get(0));
                if (bestMoveBlue != 0) {
                    addToBlueLearningData(new GameState(game, true), bestMoveBlue, x.bluePlayer);
                    game.bluePlayer.doMove(game, bestMoveBlue);
                }

                if (game.bluePlayer.energy < BlueAgent.energyLostForEachPotency.get(1) &&
                    game.redPlayer.greenFollowers.size() == 0)
                    break;

                int bestMoveRed = (int)Math.round(run(game, false, depth).get(0));
                if (bestMoveRed != 0) {
                    addToRedLearningData(new GameState(game, true), bestMoveRed, x.redPlayer);
                    game.redPlayer.doMove(game, bestMoveRed);
                }

                if (game.bluePlayer.energy < BlueAgent.energyLostForEachPotency.get(1) &&
                    game.redPlayer.greenFollowers.size() == 0)
                    break;
                
                game.simulateGreenInteractions();
            }
        }
    }
    
    public static int getChildNumber(double prop, int numLeaves) {
        assert(0 <= prop && prop <= 1);
        int child = (int)(prop * numLeaves);
        if (child == numLeaves) // This can only occur when prop is 1
            child--;
        return child;
    }

    public static void fillDecisionTreeFromLearningData(DecisionTreeNode decisionTree, Map<GameState, Integer> learningData) {
        for (int i = 0; i < DecisionTreeNode.numLeavesPropVoting; i++) {
            decisionTree.children.add(new DecisionTreeNode());
            DecisionTreeNode d1 = decisionTree.children.get(i);
            for (int j = 0; j < DecisionTreeNode.numLeavesPropEnergy; j++) {
                d1.children.add(new DecisionTreeNode());
                DecisionTreeNode d2 = d1.children.get(j);
                for (int k = 0; k < DecisionTreeNode.numLeavesPropFollowers; k++)
                    d2.children.add(new DecisionTreeNode());
            }
        }
        for (Map.Entry<GameState, Integer> entry: learningData.entrySet()) {
            DecisionTreeNode n = decisionTree;
            GameState sampleGame = entry.getKey();

            ArrayList<Integer> opinionCounts = sampleGame.getOpinionCounts();
            int goodCount = opinionCounts.get(0);
            int badCount = opinionCounts.get(1);
            double proportionGood = (double)goodCount / (goodCount + badCount);
            int child = getChildNumber(proportionGood, DecisionTreeNode.numLeavesPropVoting);
            n = n.children.get(child);
            
            double proportionEnergy = (double)sampleGame.bluePlayer.energy / 100;
            child = getChildNumber(proportionEnergy, DecisionTreeNode.numLeavesPropEnergy);
            n = n.children.get(child);
            
            double proportionFollowers = (double)sampleGame.redPlayer.greenFollowers.size() / sampleGame.ids_that_have_a_node.size();
            child = getChildNumber(proportionFollowers, DecisionTreeNode.numLeavesPropFollowers);
            n = n.children.get(child);
            
            if (n.numPiecesOfLearningData == 0)
                n.averagePotencyFromLearningData = 0.0;
            double totalPotencyFromLearningData = n.averagePotencyFromLearningData * n.numPiecesOfLearningData;
            totalPotencyFromLearningData += entry.getValue();
            n.numPiecesOfLearningData++;
            n.averagePotencyFromLearningData = totalPotencyFromLearningData / n.numPiecesOfLearningData;
        }
    }

    public static void makeProbabilisticDecisionTrees(GameState game, boolean bluePlayerIsHuman, boolean redPlayerIsHuman) {
        if (!bluePlayerIsHuman)
            fillDecisionTreeFromLearningData(game.bluePlayer.decisionTree, game.bluePlayer.learningData);
        if (!redPlayerIsHuman)
            fillDecisionTreeFromLearningData(game.redPlayer.decisionTree, game.redPlayer.learningData);
    }
}