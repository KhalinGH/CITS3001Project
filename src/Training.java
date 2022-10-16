import java.util.ArrayList;
import java.util.Arrays;

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
                if (game.bluePlayer.energy >= BlueAgent.energyLostForEachPotency.get(message_potency)) {
                    GameState newGame = new GameState(game, true);
                    newGame.bluePlayer.doMove(newGame, message_potency);
                    double thisEval = run(newGame, false, depthRemaining - 1).get(1);
                    if (thisEval > bestEval) {
                        bestEval = thisEval;
                        bestPotency = message_potency;
                    }
                }
            }
        }
        else {
            bestEval = INF;
            num_potencies = RedAgent.uncertaintyForEachPotency.size() - 1;
            for (int message_potency = 1; message_potency <= num_potencies; message_potency++) {
                GameState newGame = new GameState(game, true);
                newGame.redPlayer.doMove(newGame, message_potency);
                double thisEval = run(newGame, false, depthRemaining - 1).get(1);
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
        System.out.println("Training agents...");
        System.out.println("This should be done within about 7 seconds.");
        System.out.println();
        GameState x = gameForWhichWeAreTraining;
        int numNodes = x.ids_that_have_a_node.size();
        int numGames = 2000 / numNodes; // For each single game, much learning data will be acquired
        if (numGames > 1000)
            numGames = 1000;
        int depth = 3;
        for (int i = 0; i < numGames; i++) {
            GameState game = new GameState(x, false);
            App.addProportionOfEdges(game, Math.random());
            while (true) {
                int bestMoveBlue = (int)Math.round(run(game, true, depth).get(0));
                addToBlueLearningData(game, bestMoveBlue, x.bluePlayer);
                game.bluePlayer.doMove(game, bestMoveBlue);

                if (game.bluePlayer.energy < BlueAgent.energyLostForEachPotency.get(1) &&
                    game.redPlayer.greenFollowers.size() == 0)
                    break;

                int bestMoveRed = (int)Math.round(run(game, false, depth).get(0));
                addToRedLearningData(game, bestMoveRed, x.redPlayer);
                game.redPlayer.doMove(game, bestMoveRed);

                if (game.bluePlayer.energy < BlueAgent.energyLostForEachPotency.get(1) &&
                    game.redPlayer.greenFollowers.size() == 0)
                    break;
                
                game.simulateGreenInteractions();
            }
        }
    }
}