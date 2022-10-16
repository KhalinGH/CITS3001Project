import java.util.ArrayList;
import java.util.Arrays;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class GameState {
    Node[] nodes;
    ArrayList<Integer> ids_that_have_a_node;
    int num_grey_good;
    int num_grey_bad;
    ArrayList<ArrayList<Integer>> edges;
    RedAgent redPlayer;
    BlueAgent bluePlayer;
    
    public void display() {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("");
        int graphstreamId = 0;
        for (int id : ids_that_have_a_node) {
            graph.addNode(Integer.toString(id));
            org.graphstream.graph.Node n = graph.getNode(graphstreamId++);
            double colourStrength = 50 + (1 - nodes[id].uncertainty) / 2 * 205;
            if (nodes[id].opinion)
                n.setAttribute("ui.style", "fill-color: rgb(0,0," + (int)colourStrength + "); size: 20px;");
            else
                n.setAttribute("ui.style", "fill-color: rgb(" + (int)colourStrength + ",0,0); size: 20px;");
        }
        for (int i=0; i<edges.size(); i++) {
            ArrayList<Integer> edge = edges.get(i);
            graph.addEdge(Integer.toString(i), Integer.toString(edge.get(0)), Integer.toString(edge.get(1)));
        }
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        System.out.println("Visualisation opened in another window. Close visualisation window when you have finished viewing it.\n");
    }

    public void listGreenData() {
        System.out.println("Opinion\t Uncertainty");
        for (int id : ids_that_have_a_node)
            System.out.println(nodes[id].opinion + "\t " + nodes[id].uncertainty);
        System.out.println();
    }

    public GameState(int highest_node_id) {
        nodes = new Node[highest_node_id + 1];
        ids_that_have_a_node = new ArrayList<Integer>();
        num_grey_good = 0;
        num_grey_bad = 0;
        edges = new ArrayList<ArrayList<Integer>>();
        redPlayer = new RedAgent();
        bluePlayer = new BlueAgent();
    }

    public GameState(GameState game) {
        this.nodes = new Node[game.nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            if (game.nodes[i] == null)
                this.nodes[i] = null;
            else
                this.nodes[i] = new Node(game.nodes[i]);
        }
        this.ids_that_have_a_node = game.ids_that_have_a_node; // Shallow copy, because this doesn't change throughout the game
        this.num_grey_good = game.num_grey_good;
        this.num_grey_bad = game.num_grey_bad;
        this.edges = game.edges; // Shallow copy, because this doesn't change throughout the game
        this.redPlayer = new RedAgent(game.redPlayer);
        this.bluePlayer = new BlueAgent(game.bluePlayer);
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

    public int getNumGreys() {
        return num_grey_good + num_grey_bad;
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
        for (ArrayList<Integer> edge : edges) {
            Node.interact_two_way(nodes[edge.get(0)], nodes[edge.get(1)]);
        }
    }
}