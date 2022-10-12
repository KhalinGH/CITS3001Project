import java.util.ArrayList;

public class GameState {
    ArrayList<GreenTeamMember> graph;
    ArrayList<ArrayList<Integer>> edges;

    public void make_graph() {
        
    }

    public GameState() {
        graph = new ArrayList<GreenTeamMember>();
        edges = new ArrayList<ArrayList<Integer>>();
    }
}