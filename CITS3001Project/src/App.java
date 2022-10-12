import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        GameState game = new GameState();
        
        String input = new String();

        while (true) {
            System.out.println("Enter 'g' to generate the node attributes from input parameters.");
            System.out.println("Enter 'f' to use the node attributes specified in an input file.");
            input = scanner.nextLine().toLowerCase();
            System.out.println();
            if (input.compareTo("g") == 0 || input.compareTo("f") == 0)
                break;
            System.out.println("Invalid input.");
        }

        if (input.compareTo("g") == 0) {
            
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
                ArrayList<Integer> edge = new ArrayList<Integer>();
                edge.add(n1);
                edge.add(n2);
                game.edges.add(edge);
            }
            myFileReader.close();
        }



        while (true) {
            System.out.println("Enter 'g' to generate the graph from input parameters.");
            System.out.println("Enter 'f' to use the graph specified in an input file.");
            input = scanner.nextLine().toLowerCase();
            System.out.println();
            if (input.compareTo("g") == 0 || input.compareTo("f") == 0)
                break;
            System.out.println("Invalid input.");
        }
        
        if (input.compareTo("g") == 0) {
            
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