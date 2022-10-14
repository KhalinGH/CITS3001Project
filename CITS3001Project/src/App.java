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
        
        GameState game = get_node_ids_and_teams(scanner, minimum_uncertainty, maximum_uncertainty);
        getGraph(scanner, game);
        
        ArrayList<Boolean> players = getPlayers(scanner);
        boolean bluePlayerIsHuman = players.get(0);
        boolean redPlayerIsHuman = players.get(1);


        RedAgent redPlayer = new RedAgent();
        BlueAgent bluePlayer = new BlueAgent();

        while (!game.game_over) {
            if (redPlayerIsHuman)
                redPlayer.makeHumanMove(game);
            else
                redPlayer.makeAIMove(game);

            if (bluePlayerIsHuman)
                bluePlayer.makeHumanMove(game);
            else
                bluePlayer.makeAIMove(game);
        }

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
                break;
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
                break;
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        return new ArrayList<Double>(Arrays.asList(minimum_uncertainty, maximum_uncertainty));
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
                System.out.println("Enter the number of green team members.");
                input = scanner.nextLine();
                System.out.println();
                try {
                    num_green_agents = Integer.parseInt(input);
                    break;
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
            game = new GameState(num_green_agents);
            for (int i = 1; i <= num_green_agents; i++) {
                game.nodes[i] = new GreenTeamMember(minimum_uncertainty, maximum_uncertainty);
                game.ids_that_have_a_node.add(i);
            }

            while (true) {
                System.out.println("Enter the number of good grey agents.");
                input = scanner.nextLine();
                System.out.println();
                try {
                    game.num_grey_good = Integer.parseInt(input);
                    break;
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
                    break;
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
            }

            game = new GameState(highest_node_id);
            assert(temp_ids.size() == temp_colours.size());
            for (int i = 0; i < temp_ids.size(); i++) {
                int n = temp_ids.get(i);
                String colour = temp_colours.get(i);
                if (colour == "green") {
                    game.nodes[n] = new GreenTeamMember(minimum_uncertainty, maximum_uncertainty);
                    game.ids_that_have_a_node.add(n);
                }
                else if (colour == "grey-good")
                    game.num_grey_good++;
                else if (colour == "grey-bad")
                    game.num_grey_bad++;
                else if (colour != "red" && colour != "blue") {
                    System.out.println("Team '" + colour + "' not recognised");
                    System.exit(-1);
                }
            }
            myFileReader.close();
        }
        return game;
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
                System.out.println("Enter the proportion of possible edges that should be a real edge (from 0 to 1)."); // TODO: Reword
                input = scanner.nextLine();
                System.out.println();
                try {
                    prob_edge = Double.parseDouble(input);
                    if (0 <= prob_edge && prob_edge <= 1)
                        break;
                    System.out.println("Invalid input.");
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }

            int num_nodes = game.ids_that_have_a_node.size();
            ArrayList<ArrayList<Integer>> possible_edges = new ArrayList<ArrayList<Integer>>();
            for (int i = 0; i < game.ids_that_have_a_node.size(); i++)
                for (int j = i + 1; j < game.ids_that_have_a_node.size(); j++) {
                    ArrayList<Integer> edge = new ArrayList<Integer>();
                    edge.add(i);
                    edge.add(j);
                    possible_edges.add(edge);
                }
            assert(possible_edges.size() == num_nodes * (num_nodes - 1) / 2);
            int num_edges = (int)Math.round(prob_edge * possible_edges.size());

            Collections.shuffle(possible_edges);
            for (int i = 0; i < num_edges; i++) {
                ArrayList<Integer> chosen_possible_edge = possible_edges.get(i);
                int n1 = chosen_possible_edge.get(0);
                int n2 = chosen_possible_edge.get(1);

                ArrayList<GreenTeamMember> edge = new ArrayList<GreenTeamMember>();
                edge.add(game.nodes[n1]);
                edge.add(game.nodes[n2]);
                game.edges.add(edge);
            }
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
                ArrayList<GreenTeamMember> edge = new ArrayList<GreenTeamMember>();
                edge.add(game.nodes[n1]);
                edge.add(game.nodes[n2]);
                game.edges.add(edge);
            }
            myFileReader.close();
        }
    }

    public static ArrayList<Boolean> getPlayers(Scanner scanner) {
        String inputBlue = new String();
        String inputRed = new String();

        // Get players
        while (true) {
            System.out.println("Enter 'h' to make the blue player a human.");
            System.out.println("Enter 'a' to make the blue player an AI.");
            inputBlue = scanner.nextLine().toLowerCase();
            System.out.println();
            if (inputBlue.compareTo("h") == 0 || inputBlue.compareTo("a") == 0)
                break;
            System.out.println("Invalid input.");
        }

        // Get players
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