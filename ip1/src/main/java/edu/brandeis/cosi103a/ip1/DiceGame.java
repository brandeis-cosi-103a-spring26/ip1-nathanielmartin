package edu.brandeis.cosi103a.ip1;

import java.util.Random;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class DiceGame {
    public static final int TURNS_PER_PLAYER = 10;
    public static final int MAX_REROLLS = 2;

    public int rollDie(Random rand) {
        return rand.nextInt(6) + 1;
    }

    /**
     * Play a single turn.
     * @param rand source of randomness
     * @param decisionSupplier returns 1 to reroll, 0 to stop (called when a reroll decision is needed)
     * @param onRoll consumer called for each roll (initial and rerolls) with the roll value
     * @return final die value for the turn
     */
    public int playTurn(Random rand, IntSupplier decisionSupplier, IntConsumer onRoll) {
        int rerollsLeft = MAX_REROLLS;
        int value = rollDie(rand);
        if (onRoll != null) onRoll.accept(value);

        while (rerollsLeft > 0) {
            int decision = decisionSupplier.getAsInt();
            if (decision == 1) {
                value = rollDie(rand);
                rerollsLeft--;
                if (onRoll != null) onRoll.accept(value);
            } else {
                break;
            }
        }

        return value;
    }
}
