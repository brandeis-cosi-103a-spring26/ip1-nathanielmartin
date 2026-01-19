package edu.brandeis.cosi103a.ip1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.IntSupplier;

public class App {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Random rand = new Random();

        DiceGame game = new DiceGame();

        System.out.println("Two-player Dice Game");
        System.out.println("Rules: Each player gets " + DiceGame.TURNS_PER_PLAYER + " turns.");
        System.out.println("On a turn you roll a six-sided die and may reroll up to " + DiceGame.MAX_REROLLS + " times.");
        System.out.println("When you stop, the die value is added to your score. Highest score after all turns wins.");
        System.out.println();

        int[] scores = new int[2];

        for (int turn = 1; turn <= DiceGame.TURNS_PER_PLAYER; turn++) {
            for (int player = 0; player < 2; player++) {
                System.out.println("-- Turn " + turn + " for Player " + (player + 1) + " --");

                IntSupplier cliDecisionSupplier = () -> {
                    while (true) {
                        System.out.print("Enter 'r' to reroll or 's' to stop: ");
                        String line = in.nextLine().trim().toLowerCase();
                        if (line.isEmpty()) continue;
                        char c = line.charAt(0);
                        if (c == 'r') return 1;
                        if (c == 's') return 0;
                        System.out.println("Invalid input. Please enter 'r' or 's'.");
                    }
                };

                List<Integer> seen = new ArrayList<>();
                int finalValue = game.playTurn(rand, cliDecisionSupplier, v -> {
                    if (seen.isEmpty()) {
                        System.out.println("Initial roll: " + v);
                    } else {
                        System.out.println("Reroll result: " + v);
                    }
                    seen.add(v);
                });

                scores[player] += finalValue;
                System.out.println("Player " + (player + 1) + " score: " + scores[player]);
                System.out.println();
            }
        }

        System.out.println("Game over!");
        System.out.println("Player 1 final score: " + scores[0]);
        System.out.println("Player 2 final score: " + scores[1]);
        if (scores[0] > scores[1]) {
            System.out.println("Player 1 wins!");
        } else if (scores[1] > scores[0]) {
            System.out.println("Player 2 wins!");
        } else {
            System.out.println("It's a tie!");
        }

        in.close();
    }
}
