import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RedAgent {
    ArrayList<Node> greenFollowers;
    static ArrayList<Double> proportionFollowersLostFromMessages = new ArrayList<Double>(Arrays.asList(-1.0, 0.03, 0.06, 0.09, 0.12, 0.15, 0.18, 0.21, 0.24, 0.27, 0.30));

    public RedAgent() {
       greenFollowers = new ArrayList<Node>();
    }
    
    public void loseFollowers(int message_potency) {
        double proportionLost = proportionFollowersLostFromMessages.get(message_potency);
        int numFollowersLost = (int)Math.round(proportionLost * greenFollowers.size());
        if (numFollowersLost == 0) // TODO: Is this a good system?
            numFollowersLost = 1;
        Collections.shuffle(greenFollowers);
        for (int i = 0; i < numFollowersLost; i++)
            greenFollowers.remove(greenFollowers.size() - 1);
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