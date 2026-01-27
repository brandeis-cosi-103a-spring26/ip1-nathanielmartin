package edu.brandeis.cosi103a.ip2;

import java.util.ArrayList;

public class AutomationGame {
    private ArrayList<Card> supply;

    public AutomationGame() {
        // Set up starting supply; player decks are accounted for separately
        supply = new ArrayList<>();
        for (int i = 0; i < 46; i++) {
            supply.add(new Card(0, 1, true, "Bitcoin"));
            if (i < 40) {
                supply.add(new Card(3, 2, true, "Ethereum"));
            }
            if (i < 30) {
                supply.add(new Card(6, 3, true, "Dogecoin"));
            }
            if (i < 8) {
                supply.add(new Card(2, 1, false, "Method"));
                supply.add(new Card(5, 3, false, "Module"));
                supply.add(new Card(8, 6, false, "Framework"));
            }
        }
    }

    public Card buyCard(String name) {
        // Find and remove the named card from the supply
        for (int i = 0; i < supply.size(); i++) {
            if (supply.get(i).getName().equals(name)) {
                return supply.remove(i);
            }
        }
        // If the card is not found, return null
        return null;
    }

    public boolean checkGameEnd() {
        // Check if the game has ended by seeing if "Framework" cards are depleted
        for (Card card : supply) {
            if (card.getName().equals("Framework")) {
                return false;
            }
        }
        return true;
    }
}
