import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {
    ArrayList<GreenTeamMember> nodes;
    int num_grey_good;
    int num_grey_bad;
    ArrayList<ArrayList<Integer>> edges;
    Map<Integer, String> node_colour;

    public void make_graph() {
        
    }

    public GameState() {
        nodes = new ArrayList<GreenTeamMember>();
        num_grey_good = 0;
        num_grey_bad = 0;
        edges = new ArrayList<ArrayList<Integer>>();
        node_colour = new HashMap<Integer, String>();
    }
}