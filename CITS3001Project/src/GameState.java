package CITS3001Project;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameState {
    ArrayList<GreenTeamMember> graph = new ArrayList<GreenTeamMember>();

    public void make_graph() {
        
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        
        String input = new String();
        while (true) {
            System.out.println("Enter 'g' to generate a graph from input parameters.");
            System.out.println("Enter 'f' to use a graph specified in an input file.");
            input = scanner.nextLine().toLowerCase();
            if (input.compareTo("g") == 0 || input.compareTo("f") == 0)
                break;
            System.out.println("Invalid input.");
        }
        
        if (input.compareTo("g") == 0) {
            
        }
        else {assert(input.compareTo("f") == 0);
            File inputFile;
            while (true) {
                System.out.println("Enter name of input file.");
                input = scanner.nextLine();
                try {
                    inputFile = new File(input);
                    break;
                }
                catch (FileNotFoundException e) {

                }
            }
            Scanner myFileReader = new Scanner(inputFile);
            while (myFileReader.hasNextLine()) {
                String data = myFileReader.nextLine();

            }
            myFileReader.close();
        }
        

        scanner.close();
    }
}