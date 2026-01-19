package edu.brandeis.cosi103a.ip1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntSupplier;

import org.junit.Test;

/**
 * Unit tests for the dice game (kept here for CI convenience).
 */
public class AppTest {
    @Test
    public void rollDieRange() {
        DiceGame g = new DiceGame();
        Random r = new Random(54321);
        for (int i = 0; i < 100; i++) {
            int v = g.rollDie(r);
            assertTrue("roll in range", v >= 1 && v <= 6);
        }
    }

    @Test
    public void stopImmediately() {
        DiceGame g = new DiceGame();
        Random r = new Random(1);
        IntSupplier stop = () -> 0;
        List<Integer> seen = new ArrayList<>();
        int result = g.playTurn(r, stop, seen::add);
        assertEquals("only initial roll captured", 1, seen.size());
        assertEquals("result equals captured value", (int) seen.get(0), result);
    }

    @Test
    public void alwaysRerollUntilLimit() {
        DiceGame g = new DiceGame();
        Random r = new Random(2);
        IntSupplier alwaysReroll = () -> 1;
        List<Integer> seen = new ArrayList<>();
        int result = g.playTurn(r, alwaysReroll, seen::add);
        assertEquals("rerolls + initial", DiceGame.MAX_REROLLS + 1, seen.size());
        assertEquals("result equals last captured value", (int) seen.get(seen.size() - 1), result);
    }
}

