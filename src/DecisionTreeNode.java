import java.util.ArrayList;

public class DecisionTreeNode {
    ArrayList<DecisionTreeNode> children;
    int numPiecesOfLearningData;
    double averagePotencyFromLearningData;

    public DecisionTreeNode() {
        children = new ArrayList<DecisionTreeNode>();
        numPiecesOfLearningData = 0;
        averagePotencyFromLearningData = 3.0;
    }
}