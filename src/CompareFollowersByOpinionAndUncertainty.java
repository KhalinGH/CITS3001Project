import java.util.Comparator;

public class CompareFollowersByOpinionAndUncertainty implements Comparator<Node> {
    // This sort treats
    //     nodes that are more favourable to red
    // as being less than
    //     nodes that are more favourable to blue
    @Override
    public int compare(Node n1, Node n2) {
        if (n1.opinion && !n2.opinion)
            return 1;
        else if (!n1.opinion && n2.opinion)
            return -1;
        else if (n1.opinion && n2.opinion)
            return Double.compare(n2.uncertainty, n1.uncertainty);
        else // !n1.opinion && !n2.opinion
            return Double.compare(n1.uncertainty, n2.uncertainty);
    }
}