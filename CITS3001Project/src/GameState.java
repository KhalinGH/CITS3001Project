import java.util.ArrayList;

public class GameState {
    ArrayList<GreenTeamMember> nodes;
    int num_grey_good;
    int num_grey_bad;
    ArrayList<ArrayList<Integer>> edges;
    String[] node_colour;

    public void make_graph() {
        
    }

    public GameState(int highest_node_id) {
        nodes = new ArrayList<GreenTeamMember>();
        num_grey_good = 0;
        num_grey_bad = 0;
        edges = new ArrayList<ArrayList<Integer>>();
        node_colour = new String[highest_node_id + 1];
    }
}