import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BlueAgent {
    int energy;
    boolean isDone;
    static ArrayList<Double> uncertaintyForEachPotency = new ArrayList<Double>(Arrays.asList(-999.0, 0.8, 0.6, 0.4, 0.2, 0.0, -0.2, -0.4, -0.6, -0.8, -1.0));
    static ArrayList<Integer> energyLostForEachPotency = new ArrayList<Integer>(Arrays.asList(-999, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40));
    Map<GameState, Integer> learningData;
    DecisionTreeNode decisionTree;

    public BlueAgent() {
        energy = 100;
        isDone = false;
        learningData = new HashMap<GameState, Integer>();
        decisionTree = new DecisionTreeNode();
    }

    // Make a copy of this blue agent
    public BlueAgent(BlueAgent x) {
        this.energy = x.energy;
        this.isDone = x.isDone;
        this.learningData = x.learningData; // Shallow copy, because this doesn't change for duplicated agents doing training games
        this.decisionTree = x.decisionTree; // Shallow copy, because this doesn't change for duplicated agents doing training games
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
        Node greyNode = new Node(is_good, -1);
        for (int id : game.ids_that_have_a_node)
            Node.interact_one_way(greyNode, game.nodes[id]);
    }

    // A message_potency of -1 indicates that a grey agent is being released
    // A message_potency of 0 indicates that this blue agent is done
    public void doMove(GameState game, int message_potency) {
        if (message_potency == -1) {
            releaseGreyAgent(game);
            return;
        }
        if (message_potency == 0) {
            isDone = true;
            return;
        }
        loseEnergy(message_potency);
        Node blueNode = new Node(uncertaintyForEachPotency.get(message_potency), true, -1);
        for (int id : game.ids_that_have_a_node)
            Node.interact_one_way(blueNode, game.nodes[id]);
    }

    public void makeHumanMove(GameState game, Scanner scanner) {
        System.out.println("*** Blue agent's turn ***");
        System.out.println("ADMINISTRATOR: Enter '!v' to display visualisation of current network. Brighter colours indicate more certainty of opinion");
        System.out.println("ADMINISTRATOR: Enter '!t' to list all green nodes' opinions and uncertainties");
        String input = new String();
        if (isDone) {
            game.printStats();
            System.out.println("Press enter to pass your turn.");
            input = scanner.nextLine();
            System.out.println();
            if (input.toLowerCase().compareTo("!v") == 0)
                game.display();
            else if (input.toLowerCase().compareTo("!t") == 0)
                game.listGreenData();
            return;
        }

        int num_potencies = uncertaintyForEachPotency.size() - 1;
        int message_potency;
        if (energy < energyLostForEachPotency.get(1)) {
            while (true) {
                game.printStats();
                System.out.println("You have " + energy + " energy remaining.");
                if (game.num_grey_good + game.num_grey_bad == 1)
                    System.out.println("There is " + (game.num_grey_good + game.num_grey_bad) + " grey agent remaining.");
                else
                    System.out.println("There are " + (game.num_grey_good + game.num_grey_bad) + " grey agents remaining.");
                System.out.println("You do not have enough energy remaining to send a message. Enter 'g' to release a grey agent, or 'p' to pass the remainder of your turns (if you pass, the game will end when the red agent runs out of followers).");
                input = scanner.nextLine();
                System.out.println();
                if (input.toLowerCase().compareTo("g") == 0) {
                    message_potency = -1;
                    if (game.num_grey_good + game.num_grey_bad >= 1)
                        break;
                    System.out.println("There are no more grey agents to be released.");
                }
                else if (input.toLowerCase().compareTo("p") == 0) {
                    message_potency = 0;
                    break;
                }
                else if (input.toLowerCase().compareTo("!v") == 0)
                    game.display();
                else if (input.toLowerCase().compareTo("!t") == 0)
                    game.listGreenData();
                else
                    System.out.println("Invalid input.");
            }
        }
        else {
            while (true) {
                game.printStats();
                System.out.println("You have " + energy + " energy remaining.");
                if (game.num_grey_good + game.num_grey_bad == 1)
                    System.out.println("There is " + (game.num_grey_good + game.num_grey_bad) + " grey agent remaining.");
                else
                    System.out.println("There are " + (game.num_grey_good + game.num_grey_bad) + " grey agents remaining.");
                System.out.println("Enter your choice of message potency from 1 to " + num_potencies + ", or enter 'g' to release a grey agent.");
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
                    else if (input.toLowerCase().compareTo("!v") == 0)
                        game.display();
                    else if (input.toLowerCase().compareTo("!t") == 0)
                        game.listGreenData();
                    else
                        System.out.println("Invalid input.");
                }
            }
        }
        doMove(game, message_potency);
        game.printStats();
        System.out.println();
    }



    double priorGreyProb = 0.5;
    int numGreysObservedGood = 0;
    int numGreysObservedBad = 0;
    int numGreysObservedUnknown = 0;

    public void makeAIMove(GameState game) {
        System.out.println("*** Blue agent's turn ***");
        // TODO: Learning strategy
        if (game.getNumGreys() >= 1) {
            boolean releasingGrey = false;
            
            ArrayList<Integer> opinionCounts = game.getOpinionCounts();
            int goodCount = opinionCounts.get(0);
            int badCount = opinionCounts.get(1);
            double propGoodGreens = (double)goodCount / (goodCount + badCount);
            
            if (energy < energyLostForEachPotency.get(1) && goodCount < badCount)
                releasingGrey = true;
            else {
                double probGettingGreensIfGreyReleased;
                if (numGreysObservedGood == 0 && numGreysObservedBad == 0)
                    probGettingGreensIfGreyReleased = 0.5;
                else if (numGreysObservedGood == 0) {
                    int assumeGood = 1;
                    int assumeBad = numGreysObservedBad * numGreysObservedBad;
                    probGettingGreensIfGreyReleased = (double)assumeGood / (assumeGood + assumeBad);
                }
                else if (numGreysObservedBad == 0) {
                    int assumeBad = 1;
                    int assumeGood = numGreysObservedGood * numGreysObservedGood;
                    probGettingGreensIfGreyReleased = (double)assumeGood / (assumeGood + assumeBad);
                }
                else
                    probGettingGreensIfGreyReleased = (double)numGreysObservedGood / (numGreysObservedGood + numGreysObservedBad);

                double bayesianProbGrey = priorGreyProb * probGettingGreensIfGreyReleased / propGoodGreens;
                if (Math.random() < bayesianProbGrey)
                    releasingGrey = true;
            }
            if (releasingGrey) {
                doMove(game, -1);
                ArrayList<Integer> newOpinionCounts = game.getOpinionCounts();
                int newGoodCount = newOpinionCounts.get(0);
                if (newGoodCount > goodCount)
                    numGreysObservedGood++;
                else if (newGoodCount < goodCount)
                    numGreysObservedBad++;
                else
                    numGreysObservedUnknown++;
                return;
            }
        }

        
    }
}