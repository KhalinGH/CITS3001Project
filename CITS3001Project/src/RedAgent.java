import java.util.ArrayList;
import java.util.Arrays;

public class RedAgent {
    ArrayList<String> greenFollowers;
    static ArrayList<Double> proportionFollowersLostFromMessages = new ArrayList<Double>(Arrays.asList(0.03, 0.06, 0.09, 0.12, 0.15, 0.18, 0.21, 0.24, 0.27, 0.30));

    public RedAgent() {
       greenFollowers = new ArrayList<String>();
    }

    public int makeHumanMove(GameState game) {
        game.display();

        // TODO
        return 0;
    }

    public int makeAIMove(GameState game) {
        // TODO: Learning strategy
        return 0;
    }


}