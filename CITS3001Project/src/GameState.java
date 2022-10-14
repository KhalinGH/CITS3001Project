import java.util.ArrayList;

public class GameState {
    Node[] nodes;
    ArrayList<Integer> ids_that_have_a_node;
    int num_grey_good;
    int num_grey_bad;
    ArrayList<ArrayList<Node>> edges;
    boolean game_over;

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
    }

    public void simulateGreenInteractions() {
        // TODO
    }
}