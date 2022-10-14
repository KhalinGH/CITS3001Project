import java.util.ArrayList;


public class GameState {
    GreenTeamMember[] nodes;
    ArrayList<Integer> ids_that_have_a_node;
    int num_grey_good;
    int num_grey_bad;
    ArrayList<ArrayList<GreenTeamMember>> edges;

    public void make_graph() {
        
    }

    public void display() {
        Set<Module> modules = ModuleLayer.boot().modules();
        Set<Requires> requires = module.getDescriptor().requires();
        var options = {};
        var visNodes = new vis.Dataset(options);
        var visEdges = new vis.Dataset(options);

        for(int i=0; i< nodes.length(); i++){

            visNodes.Dataset.add(id: 1, label: nodeID.toString());

        }

        for(int i=0; i  < edges.size(); i++){
            for(int j=0; j<edges.get(i).size(); i++){
                visEdges.add(from: edges.get(i).get(0), to: edges.get(i).get(1));
            }
        }
        
        

        
    }

    public GameState(int highest_node_id) {
        nodes = new GreenTeamMember[highest_node_id + 1];
        ids_that_have_a_node = new ArrayList<Integer>();
        num_grey_good = 0;
        num_grey_bad = 0;
        edges = new ArrayList<ArrayList<GreenTeamMember>>();
    }
}