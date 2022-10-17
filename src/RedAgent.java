import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RedAgent {
    ArrayList<Node> greenFollowers;
    boolean isDone;
    static ArrayList<Double> uncertaintyForEachPotency = new ArrayList<Double>(Arrays.asList(-999.0, 0.25, 0.1, -0.05, -0.2, -0.35, -0.5, -0.65, -0.7, -0.85, -1.0));
    static ArrayList<Double> proportionFollowersLostForEachPotency = new ArrayList<Double>(Arrays.asList(-999.0, 0.04, 0.08, 0.12, 0.16, 0.20, 0.24, 0.28, 0.32, 0.36, 0.40));
    Map<GameState, Integer> learningData;
    DecisionTreeNode decisionTree;

    public RedAgent() {
        greenFollowers = new ArrayList<Node>();
        isDone = false;
        learningData = new HashMap<GameState, Integer>();
        decisionTree = new DecisionTreeNode();
    }

    // Make a copy of this red agent
    public RedAgent(RedAgent x, GameState game) {
        this.greenFollowers = new ArrayList<Node>();
        for (Node e : x.greenFollowers)
            this.greenFollowers.add(game.nodes[e.id]);
        this.isDone = x.isDone;
        this.learningData = x.learningData; // Shallow copy, because this doesn't change for duplicated agents doing training games
        this.decisionTree = x.decisionTree; // Shallow copy, because this doesn't change for duplicated agents doing training games
    }
    
    public void loseFollowers(int message_potency) {
        double proportionLost = proportionFollowersLostForEachPotency.get(message_potency);
        int numFollowersLost = (int)Math.round(proportionLost * greenFollowers.size());
        if (numFollowersLost == 0 && greenFollowers.size() != 0) // TODO: Is this a good system?
            numFollowersLost = 1;
        Collections.sort(greenFollowers, new CompareFollowersByOpinionAndUncertainty());
        for (int i = 0; i < numFollowersLost; i++)
            greenFollowers.remove(greenFollowers.size() - 1);
    }

    // A message_potency of 0 indicates that this red agent is done
    public void doMove(GameState game, int message_potency) {
        if (message_potency == 0) {
            isDone = true;
            return;
        }
        loseFollowers(message_potency);
        for (Node n : greenFollowers) {
            Node redNode = new Node(uncertaintyForEachPotency.get(message_potency), false, -1);
            Node.interact(redNode, n);
        }
    }

    public void makeHumanMove(GameState game, Scanner scanner) {
        System.out.println("*** Red agent's turn ***");
        System.out.println("ADMINISTRATOR: Enter '!v' to display visualisation of current network. Brighter colours indicate more certainty of opinion.");
        System.out.println("ADMINISTRATOR: Enter '!t' to list all green nodes' opinions and uncertainties.");
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
        if (greenFollowers.size() == 0) {
            while (true) {
                game.printStats();
                System.out.println("You have " + greenFollowers.size() + " follower" + (greenFollowers.size() == 1 ? "" : "s") + " remaining.");
                if (game.num_grey_good + game.num_grey_bad == 1)
                    System.out.println("There is " + (game.num_grey_good + game.num_grey_bad) + " grey agent remaining.");
                else
                    System.out.println("There are " + (game.num_grey_good + game.num_grey_bad) + " grey agents remaining.");
                System.out.println("You do not have any followers remaining to whom to send a message. Enter 'p' to pass the remainder of your turns (if you pass, the game will end when the blue agent runs out of energy).");
                input = scanner.nextLine();
                System.out.println();
                if (input.toLowerCase().compareTo("p") == 0) {
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
                System.out.println("You have " + greenFollowers.size() + " follower" + (greenFollowers.size() == 1 ? "" : "s") + " remaining.");
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
                    if (input.toLowerCase().compareTo("!v") == 0)
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
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            
        }
    }

    ArrayList<Double> probabilityDistributionEnergy;
    double lastMovePropGoodGreens = -1.0;
    int numMoves = 0;

    public void makeAIMove(GameState game, boolean bothAI) {
        numMoves++;
        System.out.println("*** Red agent's turn ***");

        ArrayList<Integer> opinionCounts = game.getOpinionCounts();
        int goodCount = opinionCounts.get(0);
        int badCount = opinionCounts.get(1);
        double propGoodGreens = (double)goodCount / (goodCount + badCount);
        
        // Update our estimated probability distribution of blue's energy
        if (lastMovePropGoodGreens == -1.0) { // If this is our first move
            probabilityDistributionEnergy = new ArrayList<Double>();
            for (int i = 0; i < 100; i++)
                probabilityDistributionEnergy.add(0.0);
            probabilityDistributionEnergy.add(1.0);
        }
        else { // If this is NOT our first move
            double propConverted = (propGoodGreens - lastMovePropGoodGreens) / (1 - lastMovePropGoodGreens); // The proportion of people who blue just converted
            ArrayList<Integer> EnergyLossTable = BlueAgent.energyLostForEachPotency;
            double highestPossiblePropEnergyLoss = (double)EnergyLossTable.get(EnergyLossTable.size() - 1) / 100;
            
            // Use the proportion of people who red just converted to estimate the proportion of red's highest possible
            // follower loss that red just chose to use
            double proportionOfHighestPossibleLoss = propConverted / 4 / highestPossiblePropEnergyLoss;
            if (proportionOfHighestPossibleLoss > 0.9)
                proportionOfHighestPossibleLoss = 0.9;
            
            double minEstimateProportionOfHighestPossibleLoss = proportionOfHighestPossibleLoss - 0.1;
            if (minEstimateProportionOfHighestPossibleLoss < 0)
                minEstimateProportionOfHighestPossibleLoss = 0;
            double maxEstimateProportionOfHighestPossibleLoss = proportionOfHighestPossibleLoss + 0.1;
            if (maxEstimateProportionOfHighestPossibleLoss > 1)
                maxEstimateProportionOfHighestPossibleLoss = 1;
            
            
            ArrayList<Double> temp = new ArrayList<Double>();
            for (int i = 0; i <= 100; i++)
                temp.add(0.0);
            for (int i = 0; i <= 100; i++) {
                double priorProbOfBlueHavingThisMuchEnergy = probabilityDistributionEnergy.get(i);
                int minEstimateFollowerLoss = (int)(i * minEstimateProportionOfHighestPossibleLoss);
                int maxEstimateFollowerLoss = (int)(i * maxEstimateProportionOfHighestPossibleLoss);
                int sizeOfEstimateRange = maxEstimateFollowerLoss - minEstimateFollowerLoss + 1;
                for (int j = i - maxEstimateFollowerLoss; j <= i - minEstimateFollowerLoss; j++)
                    if (0 <= j && j < temp.size())
                        temp.set(j, temp.get(j) + priorProbOfBlueHavingThisMuchEnergy / sizeOfEstimateRange);
            }
            probabilityDistributionEnergy = temp;
        }
        lastMovePropGoodGreens = propGoodGreens;

        if (greenFollowers.size() == 0) {
            if (bothAI)
                System.out.println("Red AI is passing the remainder of their turns");
            doMove(game, 0);
            if (bothAI)
                game.printStats();
            System.out.println();
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                
            }
            return;
        }
        
        // Decide on a potency using the probabilitic decision tree
        double result = 0.0;
        for (int i = 0; i < probabilityDistributionEnergy.size(); i++) {
            DecisionTreeNode n = decisionTree;

            int child = Training.getChildNumber(propGoodGreens, DecisionTreeNode.numLeavesPropVoting);
            n = n.children.get(child);
            
            double probabilityOfReachingThisNode = probabilityDistributionEnergy.get(i);
            double proportionEnergy = (double)i / 100;
            child = Training.getChildNumber(proportionEnergy, DecisionTreeNode.numLeavesPropEnergy);
            n = n.children.get(child);
            
            double proportionFollowers = (double)greenFollowers.size() / game.ids_that_have_a_node.size();
            child = Training.getChildNumber(proportionFollowers, DecisionTreeNode.numLeavesPropFollowers);
            n = n.children.get(child);

            result += probabilityOfReachingThisNode * n.averagePotencyFromLearningData;
        }
        
        if (numMoves >= 4) {
            int x = (int)(Math.random() * 3) + 8;
            if (bothAI)
                System.out.println("Red AI is sending a message with potency " + x);
            doMove(game, x);
            if (bothAI)
                game.printStats();
            System.out.println();
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                
            }
            return;
        }
        
        int potency = (int)Math.round(result);
        if (potency == 0)
            potency = 1;
        if (bothAI)
            System.out.println("Red AI is sending a message with potency " + potency);
        doMove(game, potency);
        if (bothAI)
            game.printStats();
        System.out.println();
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            
        }
    }
}