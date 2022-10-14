public class Node {
    double uncertainty;
    boolean opinion;

    // Node with a random uncertainty from a range and a random opinion
    public Node(double minimum_uncertainty, double maximum_uncertainty) {
        // TODO
    }

    // Node with a known uncertainty and a known opinion
    public Node(double uncertainty, boolean opinion) {
        this.uncertainty = uncertainty;
    }

    // Grey node
    public Node(boolean isGood) {
        this.uncertainty = 0;
        this.opinion = isGood;
    }
    
    public static void interact(Node a, Node b) {
        if (a.uncertainty < b.uncertainty) {
            // TODO: Alter b's uncertainty and opinion
        }
        else {
            // TODO: Alter a's uncertainty and opinion
        }
    }
}