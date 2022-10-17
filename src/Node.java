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
        this.uncertainty = -0.5;
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
    
    public static void interact(Node a, Node b) {
        if (a.uncertainty >= b.uncertainty) {
            // Switch the variables a and b
            Node temp = a;
            a = b;
            b = temp;
        }
        // So now we know that a.uncertainty < b.uncertainty
        
        if (a.opinion == b.opinion) {
            b.uncertainty -= (b.uncertainty - a.uncertainty) / 2;
        }
        else { // a.opinion != b.opinion
            double probOpinionChange = (b.uncertainty - a.uncertainty);
            if (probOpinionChange > 0.9)
                probOpinionChange = 0.9;
            
            // If b changes their opinion
            if (Math.random() < probOpinionChange) {
                b.opinion = a.opinion;
                b.uncertainty = 1 - (b.uncertainty - a.uncertainty);
            }
            // If b does not change their opinion
            else {
                a.uncertainty += (1 - b.uncertainty) / 2;
                if (a.uncertainty > 1)
                    a.uncertainty = 1;
                b.uncertainty += (1 - a.uncertainty) / 2;
                if (b.uncertainty > 1)
                    b.uncertainty = 1;
            }
        }
    }

    public static void interactGreenGreen(Node a, Node b) {
        if (a.uncertainty >= b.uncertainty) {
            // Switch the variables a and b
            Node temp = a;
            a = b;
            b = temp;
        }
        // So now we know that a.uncertainty < b.uncertainty
        
        if (a.opinion == b.opinion) {
            b.uncertainty -= (b.uncertainty - a.uncertainty) / 40;
        }
        else { // a.opinion != b.opinion
            double probOpinionChange = (b.uncertainty - a.uncertainty) / 20;
            if (probOpinionChange > 0.9)
                probOpinionChange = 0.9;
            
            // If b changes their opinion
            if (Math.random() < probOpinionChange) {
                b.opinion = a.opinion;
                b.uncertainty = 1 - (b.uncertainty - a.uncertainty);
            }
            // If b does not change their opinion
            else {
                a.uncertainty += (1 - b.uncertainty) / 40;
                if (a.uncertainty > 1)
                    a.uncertainty = 1;
                b.uncertainty += (1 - a.uncertainty) / 40;
                if (b.uncertainty > 1)
                    b.uncertainty = 1;
            }
        }

    }
}