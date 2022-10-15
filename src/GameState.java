import java.util.ArrayList;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.nio.*;
import org.jgrapht.nio.dot.*;
import org.jgrapht.traverse.*;

import java.io.*;
import java.net.*;
import java.util.*;

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
        System.out.println("BYE");
        Graph<Node, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        for(int id : ids_that_have_a_node){
            g.addVertex(node[id]);
    
        }
        for(int i=0; i<edges.size(); i++){
            g.addEdge(edges.get(i).get(0), edges.get(i).get(1));
        }
        JGraphXAdapter<Node, DefaultEdge> graphAdapter = new JGraphXAdapter<Node, DefaultEdge>(g);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
    
        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/graph.png");
        System.out.println("Hi");
        ImageIO.write(image, "PNG", imgFile);

        assertTrue(imgFile.exists());
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
    
    public void printStats() {
        int goodCount = 0, badCount = 0;
        for (int id : ids_that_have_a_node) {
            Node n = nodes[id];
            if (n.opinion)
                goodCount++;
            else
                badCount++;
        }
        System.out.printf("%d member" + (goodCount == 1 ? "" : "s") + " of the green team DO want to vote.\n", goodCount);
        System.out.printf("%d member" + (badCount == 1 ? "" : "s") + " of the green team DO NOT want to vote.\n", badCount);
    }

    public String getFinalResult() {
        assert(bluePlayer.isDone && redPlayer.isDone);
        int goodCount = 0, badCount = 0;
        for (int id : ids_that_have_a_node) {
            Node n = nodes[id];
            if (n.opinion)
                goodCount++;
            else
                badCount++;
        }
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