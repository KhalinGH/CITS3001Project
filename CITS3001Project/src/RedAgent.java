import java.util.ArrayList;
import java.util.Arrays;

public class RedAgent {
    ArrayList<String> greenFollowers;
    static ArrayList<Double> proportionFollowersLostFromMessages = new ArrayList<Double>(Arrays.asList(0.03, 0.06, 0.09, 0.12, 0.15, 0.18, 0.21, 0.24, 0.27, 0.30));

    public RedAgent() {
       greenFollowers = new ArrayList<String>();
    }

    public int makeMove(GameState game) {
        // TODO: Learning strategy
        game.display()

        return 0;
    }


}