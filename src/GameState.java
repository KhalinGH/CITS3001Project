import java.util.ArrayList;
import java.util.Arrays;

public class GameState {
    Node[] nodes;
    ArrayList<Integer> ids_that_have_a_node;
    int num_grey_good;
    int num_grey_bad;
    ArrayList<ArrayList<Node>> edges;
    boolean game_over;
    RedAgent redPlayer;
    BlueAgent bluePlayer;

    public void display() {
        // TODO
    }

    public GameState(int highest_node_id) {
        nodes = new Node[highest_node_id + 1];
        ids_that_have_a_node = new ArrayList<Integer>();
        num_grey_good = 0;
        num_grey_bad = 0;
        edges = new ArrayList<ArrayList<Node>>();
        game_over = false;
        redPlayer = new RedAgent();
        bluePlayer = new BlueAgent();
    }

    public ArrayList<Integer> getOpinionCounts() {
        int goodCount = 0, badCount = 0;
        for (int id : ids_that_have_a_node) {
            Node n = nodes[id];
            if (n.opinion)
                goodCount++;
            else
                badCount++;
        }
        return new ArrayList<Integer>(Arrays.asList(goodCount, badCount));
    }
    
    public void printStats() {
        ArrayList<Integer> opinionCounts = getOpinionCounts();
        int goodCount = opinionCounts.get(0);
        int badCount = opinionCounts.get(1);
        System.out.printf("%d member" + (goodCount == 1 ? "" : "s") + " of the green team DO want to vote.\n", goodCount);
        System.out.printf("%d member" + (badCount == 1 ? "" : "s") + " of the green team DO NOT want to vote.\n", badCount);
    }

    public String getFinalResult() {
        assert(bluePlayer.isDone && redPlayer.isDone);
        ArrayList<Integer> opinionCounts = getOpinionCounts();
        int goodCount = opinionCounts.get(0);
        int badCount = opinionCounts.get(1);
        if (goodCount > badCount)
            return "The blue player wins!";
        if (badCount > goodCount)
            return "The red player wins!";
        return "The game is a draw!";
    }

    public void simulateGreenInteractions() {
        for (ArrayList<Node> edge : edges) {
            Node.interact_two_way(edge.get(0), edge.get(1));
        }
    }
}