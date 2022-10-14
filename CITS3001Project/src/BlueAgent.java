import java.util.ArrayList;
import java.util.Arrays;

public class BlueAgent {
    int energy;
    static ArrayList<Integer> energyLostFromMessages = new ArrayList<Integer>(Arrays.asList(-1, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30));

    public BlueAgent() {
        energy = 100;
    }

    public void loseEnergy(int message_potency) {
        energy -= energyLostFromMessages.get(message_potency);
    }

    public void releaseGreyAgent(GameState game) {
        double proportionOfGreysThatAreGood = (double)game.num_grey_good / (game.num_grey_good + game.num_grey_bad);
        boolean is_good = Math.random() < proportionOfGreysThatAreGood;
        Node greyNode = new Node(is_good);
        for (int id : game.ids_that_have_a_node)
            Node.interact(greyNode, game.nodes[id]);
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