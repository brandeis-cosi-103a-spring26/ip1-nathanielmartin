package edu.brandeis.cosi103a.ip2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Player {
    private Stack<Card> deck;
    private Stack<Card> discards;
    private ArrayList<Card> hand;
    private AutomationGame game;
    private String name;

    public Player(AutomationGame game, String name) {
        this.game = game;
        this.name = name;
        deck = new Stack<>();
        // Set up starting deck containing 7 Bitcoins and 3 Methods
        for (int i = 0; i < 7; i++) {
            deck.push(new Card(0, 1, true, "Bitcoin"));
        }
        for (int i = 0; i < 3; i++) {
            deck.push(new Card(2, 1, false, "Method"));
        }
        discards = new Stack<>();
        hand = new ArrayList<>();
        shuffle();
        draw();
    }

    public String getName() {
        return name;
    }

    public void playTurn() {
        System.out.println(name + "'s turn:");
        int totalValue = 0;
        System.out.println("  Hand:");
        // Show hand and calculate total currency value
        for (Card card : hand) {
            System.out.println("    " + card.getName());
            if (card.isCurrency()) {
                totalValue += card.getValue();
            }
        }
        System.out.println("  Total currency value: " + totalValue);
        buy(totalValue);
        discards.addAll(hand);
        hand.clear();
        draw();
    }

    public void buy(int value) {
        // Buy the most expensive card possible
        // (this strategy is probably suboptimal but good enough for prototype)
        Card card = null;
        if (value >= 8) {
            card = game.buyCard("Framework");
        }
        if (card == null && value >= 6) {
            card = game.buyCard("Dogecoin");
        }
        if (card == null && value >= 5) {
            card = game.buyCard("Module");
        }
        if (card == null && value >= 3) {
            card = game.buyCard("Ethereum");
        }
        if (card == null && value >= 2) {
            card = game.buyCard("Method");
        }
        if (card == null && value >= 0) {
            card = game.buyCard("Bitcoin");
        }
        if (card != null) {
            discards.push(card);
            System.out.println("  Bought: " + card.getName());
        }
    }

    public void draw() {
        // Draw 5 cards, shuffling discards into deck if needed
        for (int i = 0; i < 5; i++) {
            if (deck.isEmpty()) {
                while (!discards.isEmpty()) {
                    deck.push(discards.pop());
                }
                shuffle();
            }
            if (!deck.isEmpty()) {
                hand.add(deck.pop());
            }
        }
    }

    public void shuffle() {
        System.out.println(name + " is shuffling their deck.");
        ArrayList<Card> tempList = new ArrayList<>(deck);
        Collections.shuffle(tempList);
        deck.clear();
        for (Card card : tempList) {
            deck.push(card);
        }
    }

    public int getFinalScore() {
        int score = 0;
        // Combine all cards to calculate score
        Stack<Card> allCards = new Stack<>();
        allCards.addAll(deck);
        allCards.addAll(discards);
        allCards.addAll(hand);
        // Calculate score from non-currency cards
        for (Card card : allCards) {
            if (!card.isCurrency()) {
                score += card.getValue();
            }
        }
        return score;
    }
}
