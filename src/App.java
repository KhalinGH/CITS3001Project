import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class App {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Double> uncertainties = get_min_and_max_uncertainties(scanner);
        double minimum_uncertainty = uncertainties.get(0);
        double maximum_uncertainty = uncertainties.get(1);
        
        // Set up the game
        GameState game = get_node_ids_and_teams(scanner, minimum_uncertainty, maximum_uncertainty);
        getGraph(scanner, game);
        giveRedFollowers(game);
        
        ArrayList<Boolean> players = getPlayers(scanner);
        boolean bluePlayerIsHuman = players.get(0);
        boolean redPlayerIsHuman = players.get(1);

        if (!bluePlayerIsHuman || !redPlayerIsHuman) {
            System.out.println("Training agents and building probabilistic decision trees...");
            System.out.println("This should be done within about 10 seconds.");
            System.out.println();
            Training.trainOnGames(game);
            Training.makeProbabilisticDecisionTrees(game, bluePlayerIsHuman, redPlayerIsHuman);
        }

        game.printStats();

        while (true) {
            if (bluePlayerIsHuman)
                game.bluePlayer.makeHumanMove(game, scanner);
            else
                game.bluePlayer.makeAIMove(game);

            if (game.bluePlayer.isDone && game.redPlayer.isDone)
                break;

            if (redPlayerIsHuman)
                game.redPlayer.makeHumanMove(game, scanner);
            else
                game.redPlayer.makeAIMove(game);
            
            if (game.bluePlayer.isDone && game.redPlayer.isDone)
                break;
            
            game.simulateGreenInteractions();
        }

        System.out.println("*** GAME OVER ***");
        game.printStats();
        System.out.println(game.getFinalResult());
        System.out.println();

        scanner.close();
    }

    public static ArrayList<Double> get_min_and_max_uncertainties(Scanner scanner) {
        String input = new String();
        double minimum_uncertainty, maximum_uncertainty;

        while (true) {
            System.out.println("Enter the minimum initial uncertainty of each green team member (give a value from -1 to 1)");
            input = scanner.nextLine();
            System.out.println();
            try {
                minimum_uncertainty = Double.parseDouble(input);
                if (-1 <= minimum_uncertainty && minimum_uncertainty <= 1)
                    break;
                System.out.println("Invalid input.");
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        while (true) {
            System.out.println("Enter the maximum initial uncertainty of each green team member (give a value from -1 to 1)");
            input = scanner.nextLine();
            System.out.println();
            try {
                maximum_uncertainty = Double.parseDouble(input);
                if (-1 <= maximum_uncertainty && maximum_uncertainty <= 1)
                    break;
                System.out.println("Invalid input.");
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        return new ArrayList<Double>(Arrays.asList(minimum_uncertainty, maximum_uncertainty));
    }

    public static ArrayList<Boolean> getWhoWantsToVote(Scanner scanner, int numNodes) {
        String input = new String();
        double proportionThatWantToVote;
        while (true) {
            System.out.println("Enter the percentage of green team members that initially want to vote (from 0 to 100).");
            input = scanner.nextLine();
            System.out.println();
            // If the input ends with "%" or " %", then remove that ending from the input
            if (!input.isEmpty() && input.charAt(input.length() - 1) == '%') {
                input = input.substring(0, input.length() - 1);
                if (!input.isEmpty() && input.charAt(input.length() - 1) == ' ')
                    input = input.substring(0, input.length() - 1);
            }
            try {
                double percentageThatWantToVote = Double.parseDouble(input);
                proportionThatWantToVote = percentageThatWantToVote / 100;
                if (0 <= proportionThatWantToVote && proportionThatWantToVote <= 1)
                    break;
                System.out.println("Invalid input.");
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        int numberWhoWantToVote = (int)Math.round(proportionThatWantToVote * numNodes);
        ArrayList<Boolean> whoWantsToVote = new ArrayList<Boolean>();
        for (int i = 0; i < numberWhoWantToVote; i++)
            whoWantsToVote.add(true);
        for (int i = 0; i < numNodes - numberWhoWantToVote; i++)
            whoWantsToVote.add(false);
        Collections.shuffle(whoWantsToVote);
        assert(whoWantsToVote.size() == numNodes);
        return whoWantsToVote;
    }

    public static GameState get_node_ids_and_teams(Scanner scanner, double minimum_uncertainty, double maximum_uncertainty) {
        String input = new String();
        GameState game;

        // Get node ids and teams
        while (true) {
            System.out.println("Enter 'p' to generate the node ids and teams from input parameters.");
            System.out.println("Enter 'f' to use the node ids and teams specified in an input file.");
            input = scanner.nextLine().toLowerCase();
            System.out.println();
            if (input.compareTo("p") == 0 || input.compareTo("f") == 0)
                break;
            System.out.println("Invalid input.");
        }

        // Use parameters to get node ids and teams (must set game.nodes, game.ids_that_have_a_node, game.num_grey_good, game.num_grey_bad)
        if (input.compareTo("p") == 0) {
            int num_green_agents;
            while (true) {
                System.out.println("Enter the number of green team members (maximum 1000).");
                input = scanner.nextLine();
                System.out.println();
                try {
                    num_green_agents = Integer.parseInt(input);
                    if (num_green_agents >= 0)
                        break;
                    System.out.println("Invalid input.");
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
            game = new GameState(num_green_agents);
            ArrayList<Boolean> wantsToVote = getWhoWantsToVote(scanner, num_green_agents);
            for (int i = 1; i <= num_green_agents; i++) {
                game.nodes[i] = new Node(minimum_uncertainty, maximum_uncertainty, wantsToVote.get(i-1), i);
                game.ids_that_have_a_node.add(i);
            }

            while (true) {
                System.out.println("Enter the number of good grey agents.");
                input = scanner.nextLine();
                System.out.println();
                try {
                    game.num_grey_good = Integer.parseInt(input);
                    if (game.num_grey_good >= 0)
                        break;
                    System.out.println("Invalid input.");
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
            while (true) {
                System.out.println("Enter the number of bad grey agents (i.e. spies).");
                input = scanner.nextLine();
                System.out.println();
                try {
                    game.num_grey_bad = Integer.parseInt(input);
                    if (game.num_grey_bad >= 0)
                        break;
                    System.out.println("Invalid input.");
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
        }
        // Use input file to get node ids and teams (must set game.nodes, game.ids_that_have_a_node, game.num_grey_good, game.num_grey_bad)
        else {assert(input.compareTo("f") == 0);
            File inputFile;
            Scanner myFileReader;
            while (true) {
                System.out.println("Enter the name of the input file specifying the node ids and teams.\nFile format:\nid,team\nid,team\nid,team\n...");
                input = scanner.nextLine();
                System.out.println();
                inputFile = new File(input);
                try {
                    myFileReader = new Scanner(inputFile);
                    break;
                }
                catch (FileNotFoundException e) {
                    System.out.println("File cannot be found or cannot be opened.");
                }
            }
            
            int highest_node_id = -1;
            int numGreenNodes = 0;
            ArrayList<Integer> temp_ids = new ArrayList<Integer>();
            ArrayList<String> temp_colours = new ArrayList<String>();
            while (myFileReader.hasNextLine()) {
                String[] data = myFileReader.nextLine().split(",");
                if (data.length != 2)
                    continue;
                int n;
                try {
                    n = Integer.parseInt(data[0]);
                }
                catch (NumberFormatException e) {
                    continue;
                }
                String colour = data[1].toLowerCase();
                temp_ids.add(n);
                temp_colours.add(colour);
                if (n > highest_node_id)
                    highest_node_id = n;
                if (colour.compareTo("green") == 0)
                    numGreenNodes++;
            }
            myFileReader.close();

            game = new GameState(highest_node_id);
            ArrayList<Boolean> wantsToVote = getWhoWantsToVote(scanner, numGreenNodes);
            int wantsToVoteIterator = 0;
            assert(temp_ids.size() == temp_colours.size());
            for (int i = 0; i < temp_ids.size(); i++) {
                int n = temp_ids.get(i);
                String colour = temp_colours.get(i);
                if (colour.compareTo("green") == 0) {
                    game.nodes[n] = new Node(minimum_uncertainty, maximum_uncertainty, wantsToVote.get(wantsToVoteIterator++), n);
                    game.ids_that_have_a_node.add(n);
                }
                else if (colour.compareTo("grey-good") == 0)
                    game.num_grey_good++;
                else if (colour.compareTo("grey-bad") == 0)
                    game.num_grey_bad++;
                else if (colour.compareTo("red") != 0 && colour.compareTo("blue") != 0) {
                    System.out.println("Team '" + colour + "' not recognised");
                    System.exit(-1);
                }
            }
            assert(wantsToVoteIterator == numGreenNodes);
        }
        return game;
    }

    public static void addProportionOfEdges(GameState game, double prob_edge) {
        int num_nodes = game.ids_that_have_a_node.size();
        ArrayList<ArrayList<Integer>> possible_edges = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < game.ids_that_have_a_node.size(); i++)
            for (int j = i + 1; j < game.ids_that_have_a_node.size(); j++) {
                int id1 = game.ids_that_have_a_node.get(i);
                int id2 = game.ids_that_have_a_node.get(j);
                ArrayList<Integer> edge = new ArrayList<Integer>();
                edge.add(id1);
                edge.add(id2);
                possible_edges.add(edge);
            }
        assert(possible_edges.size() == num_nodes * (num_nodes - 1) / 2);
        int num_edges = (int)Math.round(prob_edge * possible_edges.size());

        Collections.shuffle(possible_edges);
        for (int i = 0; i < num_edges; i++) {
            ArrayList<Integer> chosen_possible_edge = possible_edges.get(i);
            int n1 = chosen_possible_edge.get(0);
            int n2 = chosen_possible_edge.get(1);

            ArrayList<Integer> edge = new ArrayList<Integer>();
            edge.add(n1);
            edge.add(n2);
            game.edges.add(edge);
        }
    }

    public static void getGraph(Scanner scanner, GameState game) {
        String input = new String();

        // Get graph
        while (true) {
            System.out.println("Enter 'p' to generate the graph from input parameters.");
            System.out.println("Enter 'f' to use the graph specified in an input file.");
            input = scanner.nextLine().toLowerCase();
            System.out.println();
            if (input.compareTo("p") == 0 || input.compareTo("f") == 0)
                break;
            System.out.println("Invalid input.");
        }
        
        // Use parameters to get graph (must set game.edges)
        if (input.compareTo("p") == 0) {
            double prob_edge;
            while (true) {
                System.out.println("Enter the percentage of possible edges that should be a real edge (from 0 to 100).");
                input = scanner.nextLine();
                System.out.println();
                // If the input ends with "%" or " %", then remove that ending from the input
                if (!input.isEmpty() && input.charAt(input.length() - 1) == '%') {
                    input = input.substring(0, input.length() - 1);
                    if (!input.isEmpty() && input.charAt(input.length() - 1) == ' ')
                        input = input.substring(0, input.length() - 1);
                }
                try {
                    double percentageRealEdges = Double.parseDouble(input);
                    prob_edge = percentageRealEdges / 100;
                    if (0 <= prob_edge && prob_edge <= 1)
                        break;
                    System.out.println("Invalid input.");
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
            addProportionOfEdges(game, prob_edge);
        }
        // Use input file to get graph (must set game.edges)
        else {assert(input.compareTo("f") == 0);
            File inputFile;
            Scanner myFileReader;
            while (true) {
                System.out.println("Enter the name of the input file specifying the graph.\nFile format:\nn1,n2\nn1,n2\nn1,n2\n...");
                input = scanner.nextLine();
                System.out.println();
                inputFile = new File(input);
                try {
                    myFileReader = new Scanner(inputFile);
                    break;
                }
                catch (FileNotFoundException e) {
                    System.out.println("File cannot be found or cannot be opened.");
                }
            }

            while (myFileReader.hasNextLine()) {
                String[] data = myFileReader.nextLine().split(",");
                if (data.length != 2)
                    continue;
                int n1, n2;
                try {
                    n1 = Integer.parseInt(data[0]);
                    n2 = Integer.parseInt(data[1]);
                }
                catch (NumberFormatException e) {
                    continue;  
                }
                if (n1 >= game.nodes.length) {
                    System.out.println("Node " + n1 + " specified in file does not exist.");
                    System.exit(-1);
                }
                if (n2 >= game.nodes.length) {
                    System.out.println("Node " + n2 + " specified in file does not exist.");
                    System.exit(-1);
                }
                if (game.nodes[n1] == null) {
                    System.out.println("Node " + n1 + " specified in file is not a member of the green team.");
                    System.exit(-1);
                }
                if (game.nodes[n2] == null) {
                    System.out.println("Node " + n2 + " specified in file is not a member of the green team.");
                    System.exit(-1);
                }
                ArrayList<Integer> edge = new ArrayList<Integer>();
                edge.add(n1);
                edge.add(n2);
                game.edges.add(edge);
            }
            myFileReader.close();
        }
    }

    public static void giveRedFollowers(GameState game) {
        // Give red followers (must set game.redPlayer.greenFollowers)
        for (int id : game.ids_that_have_a_node) {
            Node n = game.nodes[id];
            game.redPlayer.greenFollowers.add(n);
        }
    }

    public static ArrayList<Boolean> getPlayers(Scanner scanner) {
        String inputBlue = new String();
        String inputRed = new String();

        // Decide whether the blue player should be a human or an AI
        while (true) {
            System.out.println("Enter 'h' to make the blue player a human.");
            System.out.println("Enter 'a' to make the blue player an AI.");
            inputBlue = scanner.nextLine().toLowerCase();
            System.out.println();
            if (inputBlue.compareTo("h") == 0 || inputBlue.compareTo("a") == 0)
                break;
            System.out.println("Invalid input.");
        }

        // Decide whether the red player should be a human or an AI
        while (true) {
            System.out.println("Enter 'h' to make the red player a human.");
            System.out.println("Enter 'a' to make the red player an AI.");
            inputRed = scanner.nextLine().toLowerCase();
            System.out.println();
            if (inputRed.compareTo("h") == 0 || inputRed.compareTo("a") == 0)
                break;
            System.out.println("Invalid input.");
        }
        
        ArrayList<Boolean> players = new ArrayList<Boolean>();
        players.add(inputBlue.compareTo("h") == 0);
        players.add(inputRed.compareTo("h") == 0);
        return players;
    }
}