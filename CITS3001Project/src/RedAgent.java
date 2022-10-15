import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class RedAgent {
    ArrayList<Node> greenFollowers;
    static ArrayList<Double> uncertaintyForEachPotency = new ArrayList<Double>(Arrays.asList(-1.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0));
    static ArrayList<Double> proportionFollowersLostForEachPotency = new ArrayList<Double>(Arrays.asList(-1.0, 0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50));

    public RedAgent() {
        greenFollowers = new ArrayList<Node>();
    }
    
    public void loseFollowers(int message_potency) {
        double proportionLost = proportionFollowersLostForEachPotency.get(message_potency);
        int numFollowersLost = (int)Math.round(proportionLost * greenFollowers.size());
        if (numFollowersLost == 0 && greenFollowers.size() != 0) // TODO: Is this a good system?
            numFollowersLost = 1;
        Collections.shuffle(greenFollowers);
        for (int i = 0; i < numFollowersLost; i++)
            greenFollowers.remove(greenFollowers.size() - 1);
    }

    public void doMove(GameState game, int message_potency) {
        loseFollowers(message_potency);
        Node redNode = new Node(uncertaintyForEachPotency.get(message_potency), false);
        for (int id : game.ids_that_have_a_node)
            Node.interact_one_way(redNode, game.nodes[id]);
    }

    public void makeHumanMove(GameState game, Scanner scanner) {
        System.out.println("*** Red agent's turn ***");
        game.display();

        String input = new String();
        int num_potencies = uncertaintyForEachPotency.size() - 1;
        int message_potency;
        while (true) {
            game.printStats();
            System.out.println("You, the red agent, have " + greenFollowers.size() + " followers remaining.");
            System.out.println("The blue agent has " + game.bluePlayer.energy + " energy remaining.");
            if (game.num_grey_good + game.num_grey_bad == 1)
                System.out.println("There is " + (game.num_grey_good + game.num_grey_bad) + " grey agent remaining.");
            else
                System.out.println("There are " + (game.num_grey_good + game.num_grey_bad) + " grey agents remaining.");
            System.out.println("Enter your choice of message potency from 1 to " + num_potencies + ".");
            System.out.print("Potency:                     \t");
            for (int i = 1; i <= num_potencies; i++)
                System.out.print(i + "\t");
            System.out.println();
            System.out.print("Uncertainty:                 \t");
            for (int i = 1; i <= num_potencies; i++)
                System.out.print(uncertaintyForEachPotency.get(i) + "\t");
            System.out.println();
            System.out.print("Proportion of followers lost:\t");
            for (int i = 1; i <= num_potencies; i++)
                System.out.print(proportionFollowersLostForEachPotency.get(i) + "\t");
            System.out.println();
            input = scanner.nextLine();
            System.out.println();
            try {
                message_potency = Integer.parseInt(input);
                if (1 <= message_potency && message_potency <= num_potencies)
                    break;
                System.out.println("Invalid input.");
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        doMove(game, message_potency);
    }

    public void makeAIMove(GameState game) {
        // TODO: Learning strategy
    }


}