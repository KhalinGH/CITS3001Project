import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        String input = new String();
        GameState game;

        while (true) {
            System.out.println("Enter 'p' to generate the node attributes from input parameters.");
            System.out.println("Enter 'f' to use the node attributes specified in an input file.");
            input = scanner.nextLine().toLowerCase();
            System.out.println();
            if (input.compareTo("p") == 0 || input.compareTo("f") == 0)
                break;
            System.out.println("Invalid input.");
        }

        int highest_node_id = -1;
        if (input.compareTo("p") == 0) {
            // TODO
            game = new GameState(highest_node_id);
            // TODO
        }
        else {assert(input.compareTo("f") == 0);
            File inputFile;
            Scanner myFileReader;
            while (true) {
                System.out.println("Enter name of input file specifying node attributes.\nFile format:\nid,team\nid,team\nid,team\n...");
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
            
            ArrayList<Integer> temp0 = new ArrayList<Integer>();
            ArrayList<String> temp1 = new ArrayList<String>();
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
                temp0.add(n);
                temp1.add(colour);
                if (n > highest_node_id)
                    highest_node_id = n;
            }

            game = new GameState(highest_node_id);
            assert(temp0.size() == temp1.size());
            for (int i = 0; i < temp0.size(); i++)
                game.node_colour[temp0.get(i)] = temp1.get(i);
            myFileReader.close();
        }



        while (true) {
            System.out.println("Enter 'p' to generate the graph from input parameters.");
            System.out.println("Enter 'f' to use the graph specified in an input file.");
            input = scanner.nextLine().toLowerCase();
            System.out.println();
            if (input.compareTo("p") == 0 || input.compareTo("f") == 0)
                break;
            System.out.println("Invalid input.");
        }
        
        if (input.compareTo("p") == 0) {
            // TODO
        }
        else {assert(input.compareTo("f") == 0);
            File inputFile;
            Scanner myFileReader;
            while (true) {
                System.out.println("Enter name of input file specifying graph.\nFile format:\nn1,n2\nn1,n2\nn1,n2\n...");
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
                if (n1 > highest_node_id) {
                    System.out.println("Node " + n1 + " specified in file does not exist.");
                    System.exit(-1);
                }
                if (n2 > highest_node_id) {
                    System.out.println("Node " + n2 + " specified in file does not exist.");
                    System.exit(-1);
                }
                if (game.node_colour[n1] != "green") {
                    System.out.println("Node " + n1 + " specified in file is not a member of the green team.");
                    System.exit(-1);
                }
                if (game.node_colour[n2] != "green") {
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
        

        scanner.close();
    }
}