import java.util.ArrayList;

public class DecisionTreeNode {
    ArrayList<DecisionTreeNode> children;
    int numPiecesOfLearningData;
    double averagePotencyFromLearningData;
    static int numLeavesPropVoting = 4; // DEBUG
    static int numLeavesPropEnergy = 4;
    static int numLeavesPropFollowers = 4;

    public DecisionTreeNode() {
        children = new ArrayList<DecisionTreeNode>();
        numPiecesOfLearningData = 0;
        averagePotencyFromLearningData = Math.random() * 5 + 1;
        if (averagePotencyFromLearningData >= 3)
            averagePotencyFromLearningData += 4;
    }
}