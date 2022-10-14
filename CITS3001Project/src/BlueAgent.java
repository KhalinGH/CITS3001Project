import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class BlueAgent {
    int energy;
    static ArrayList<Double> uncertaintyForEachPotency = new ArrayList<Double>(Arrays.asList(-1.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0));
    static ArrayList<Integer> energyLostForEachPotency = new ArrayList<Integer>(Arrays.asList(-1, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30));

    public BlueAgent() {
        energy = 100;
    }

    public void loseEnergy(int message_potency) {
        energy -= energyLostForEachPotency.get(message_potency);
    }

    public void releaseGreyAgent(GameState game) {
        double proportionOfGreysThatAreGood = (double)game.num_grey_good / (game.num_grey_good + game.num_grey_bad);
        boolean is_good = Math.random() < proportionOfGreysThatAreGood;
        if (is_good)
            game.num_grey_good--;
        else
            game.num_grey_bad--;
        Node greyNode = new Node(is_good);
        for (int id : game.ids_that_have_a_node)
            Node.interact_one_way(greyNode, game.nodes[id]);
    }

    public void doMove(GameState game, int message_potency) { // A message_potency of -1 indicates that a grey agent is being released
        if (message_potency == -1) {
            releaseGreyAgent(game);
            return;
        }
        loseEnergy(message_potency);
        Node blueNode = new Node(uncertaintyForEachPotency.get(message_potency), true);
        for (int id : game.ids_that_have_a_node)
            Node.interact_one_way(blueNode, game.nodes[id]);
    }

    public void makeHumanMove(GameState game, Scanner scanner) {
        System.out.println("*** Blue agent's turn ***");
        game.display();

        String input = new String();
        int num_potencies = uncertaintyForEachPotency.size() - 1;
        int message_potency;
        while (true) {
            game.printStats();
            System.out.println("You, the blue agent, have " + energy + " energy remaining.");
            System.out.println("The red agent has " + game.redPlayer.greenFollowers.size() + " followers remaining.");
            if (game.num_grey_good + game.num_grey_bad == 1)
                System.out.println("There is " + (game.num_grey_good + game.num_grey_bad) + " grey agent remaining.");
            else
                System.out.println("There are " + (game.num_grey_good + game.num_grey_bad) + " grey agents remaining.");
            System.out.println("Enter your choice of message potency from 1 to " + num_potencies + ", or enter 'g' to release a grey agent");
            System.out.print("Potency:                     \t");
            for (int i = 1; i <= num_potencies; i++)
                System.out.print(i + "\t");
            System.out.println();
            System.out.print("Uncertainty:                 \t");
            for (int i = 1; i <= num_potencies; i++)
                System.out.print(uncertaintyForEachPotency.get(i) + "\t");
            System.out.println();
            System.out.print("Energy lost:                 \t");
            for (int i = 1; i <= num_potencies; i++)
                System.out.print(energyLostForEachPotency.get(i) + "\t");
            System.out.println();
            input = scanner.nextLine();
            System.out.println();
            try {
                message_potency = Integer.parseInt(input);
                if (1 <= message_potency && message_potency <= num_potencies) {
                    if (energy >= energyLostForEachPotency.get(message_potency))
                        break;
                    System.out.println("You do not have enough energy to send that message.");
                }
                else
                    System.out.println("Invalid input.");
            }
            catch (NumberFormatException e) {
                if (input.toLowerCase().compareTo("g") == 0) {
                    message_potency = -1;
                    if (game.num_grey_good + game.num_grey_bad >= 1)
                        break;
                    System.out.println("There are no more grey agents to be released.");
                }
                else
                    System.out.println("Invalid input.");
            }
        }
        doMove(game, message_potency);
    }

    public void makeAIMove(GameState game) {
        // TODO: Learning strategy
    }
}