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
        int bestPotency = -1;
        double bestEval;
        int num_potencies;
        if (turn) {
            bestEval = -INF;
            num_potencies = BlueAgent.uncertaintyForEachPotency.size() - 1;
            for (int message_potency = 1; message_potency <= num_potencies; message_potency++) {
                GameState newGame = new GameState(game);
                newGame.bluePlayer.doMove(newGame, message_potency);
                double thisEval = run(newGame, false, depthRemaining - 1).get(1);
                if (thisEval > bestEval) {
                    bestEval = thisEval;
                    bestPotency = message_potency;
                }
            }
        }
        else {
            bestEval = INF;
            num_potencies = RedAgent.uncertaintyForEachPotency.size() - 1;
            for (int message_potency = 1; message_potency <= num_potencies; message_potency++) {
                GameState newGame = new GameState(game);
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
}