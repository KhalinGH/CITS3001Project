import java.util.ArrayList;

public class GameState {
    GreenTeamMember[] nodes;
    ArrayList<Integer> ids_that_have_a_node;
    int num_grey_good;
    int num_grey_bad;
    ArrayList<ArrayList<GreenTeamMember>> edges;

    public void make_graph() {
        
    }

    public GameState(int highest_node_id) {
        nodes = new GreenTeamMember[highest_node_id + 1];
        ids_that_have_a_node = new ArrayList<Integer>();
        num_grey_good = 0;
        num_grey_bad = 0;
        edges = new ArrayList<ArrayList<GreenTeamMember>>();
    }
}