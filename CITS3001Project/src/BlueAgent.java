import java.util.ArrayList;
import java.util.Arrays;

public class BlueAgent {
    int energy;
    static ArrayList<Integer> energyLostFromMessages = new ArrayList<Integer>(Arrays.asList(3, 6, 9, 12, 15, 18, 21, 24, 27, 30));

    public BlueAgent() {
        energy = 100;
    }



    public int makeHumanMove(GameState game) {
        game.display();

        // TODO
        return 0;
    }

    public int makeAIMove(GameState game) {
        // TODO: Learning strategy
        return 0;
    }


}