public class Node {
    double uncertainty;
    boolean opinion;
    int id;

    // Node with a random uncertainty within a range and a known opinion
    public Node(double minimum_uncertainty, double maximum_uncertainty, boolean opinion, int id) {
        this.uncertainty = minimum_uncertainty + (maximum_uncertainty - minimum_uncertainty) * Math.random();
        this.opinion = opinion;
        this.id = id;
    }

    // Node with a known uncertainty and a known opinion
    public Node(double uncertainty, boolean opinion, int id) {
        this.uncertainty = uncertainty;
        this.opinion = opinion;
        this.id = id;
    }

    // Grey node
    public Node(boolean isGood, int id) {
        this.uncertainty = 0;
        this.opinion = isGood;
        this.id = id;
    }

    // Duplicate a node
    public Node(Node n) {
        assert(n != null);
        this.uncertainty = n.uncertainty;
        this.opinion = n.opinion;
        this.id = n.id;
    }

    public static void changeParametersOfB(Node a, Node b) {
        // TODO: Change b.opinion and/or b.uncertainty based on a.opinion and/or a.uncertainty
    }

    public static void interact_one_way(Node a, Node b) {
        if (a.uncertainty < b.uncertainty) {
            changeParametersOfB(a, b);
        }
    }
    
    public static void interact_two_way(Node a, Node b) {
        if (a.uncertainty < b.uncertainty) {
            changeParametersOfB(a, b);
        }
        else if (b.uncertainty < a.uncertainty) {
            changeParametersOfB(b, a);
        }
    }
}