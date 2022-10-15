import java.util.ArrayList;
import java.util.Arrays;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Graph;

public class GameState {
    Node[] nodes;
    ArrayList<Integer> ids_that_have_a_node;
    int num_grey_good;
    int num_grey_bad;
    ArrayList<ArrayList<Integer>> edges;
    boolean game_over;
    RedAgent redPlayer;
    BlueAgent bluePlayer;
    
    public void display() {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("Tutorial 1");
        int graphstreamId = 0;
        for (int id : ids_that_have_a_node) {
            graph.addNode(Integer.toString(id));
            org.graphstream.graph.Node n = graph.getNode(graphstreamId++);
            if (nodes[id].opinion)
                n.setAttribute("ui.style", "fill-color: rgb(0,0,255);");
            else
                n.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
        }
        for (int i=0; i<edges.size(); i++) {
            ArrayList<Integer> edge = edges.get(i);
            graph.addEdge(Integer.toString(i), Integer.toString(edge.get(0)), Integer.toString(edge.get(1)));
        }
        graph.display();
    }

    public GameState(int highest_node_id) {
        nodes = new Node[highest_node_id + 1];
        ids_that_have_a_node = new ArrayList<Integer>();
        num_grey_good = 0;
        num_grey_bad = 0;
        edges = new ArrayList<ArrayList<Integer>>();
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
        for (ArrayList<Integer> edge : edges) {
            Node.interact_two_way(nodes[edge.get(0)], nodes[edge.get(1)]);
        }
    }
}