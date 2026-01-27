package edu.brandeis.cosi103a.ip2;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
    private AutomationGame game;
    private Player player;

    @Before
    public void setUp() {
        game = new AutomationGame();
        player = new Player(game, "TestPlayer");
    }

    @Test
    public void testBuySuccessfulFirstAttempt() {
        // Test buying Framework (most expensive card, cost 8)
        // Player should successfully buy Framework with value >= 8
        player.buy(8);
        // Verify that a Framework was purchased by checking the game supply
        // The initial supply has 8 Frameworks, after buying one there should be 7
        int frameworkCount = 0;
        AutomationGame newGame = new AutomationGame();
        for (int i = 0; i < 8; i++) {
            if (newGame.buyCard("Framework") != null) {
                frameworkCount++;
            }
        }
        assertEquals(8, frameworkCount);
        
        // Now count in the game where we bought one
        frameworkCount = 0;
        for (int i = 0; i < 8; i++) {
            if (game.buyCard("Framework") != null) {
                frameworkCount++;
            }
        }
        assertEquals(7, frameworkCount);
    }

    @Test
    public void testBuySuccessfulBitcoin() {
        // Test buying Bitcoin (cheapest card, cost 0)
        // This should always succeed as long as supply isn't empty
        player.buy(0);
        // Check that Bitcoin was bought by verifying supply decreased
        int bitcoinCount = 0;
        for (int i = 0; i < 60; i++) {
            if (game.buyCard("Bitcoin") != null) {
                bitcoinCount++;
            }
        }
        // Initial supply has 46 Bitcoins, minus 1 bought = 45 remaining
        assertEquals(45, bitcoinCount);
    }

    @Test
    public void testBuySuccessfulMethod() {
        // Test buying Method (cost 2) with exact value
        player.buy(2);
        // Check that Method was bought
        int methodCount = 0;
        for (int i = 0; i < 10; i++) {
            if (game.buyCard("Method") != null) {
                methodCount++;
            }
        }
        // Initial supply has 8 Methods, minus 1 bought = 7 remaining
        assertEquals(7, methodCount);
    }

    @Test
    public void testBuyUnsuccessfulNotEnoughValue() {
        // First, exhaust all Bitcoins from the supply (the only card with cost 0)
        for (int i = 0; i < 46; i++) {
            game.buyCard("Bitcoin");
        }
        // Now try to buy with value 1 (not enough for Method which costs 2)
        // This should fail because Bitcoin is depleted and value < 2
        player.buy(1);
        
        // Verify no other cards were removed from supply
        // Try buying a Method - should still have 8 available
        int methodCount = 0;
        for (int i = 0; i < 10; i++) {
            if (game.buyCard("Method") != null) {
                methodCount++;
            }
        }
        assertEquals(8, methodCount);
    }

    @Test
    public void testBuyUnsuccessfulCardDepleted() {
        // Exhaust all Framework cards from supply
        for (int i = 0; i < 8; i++) {
            game.buyCard("Framework");
        }
        
        // Try to buy with value 8 - should fall through to next available card
        // With value 8, player should buy Dogecoin instead (cost 6)
        player.buy(8);
        
        // Verify Framework is still depleted (should return null)
        assertNull(game.buyCard("Framework"));
        
        // Verify Dogecoin was bought instead (should have 29 remaining from 30)
        int dogecoinCount = 0;
        for (int i = 0; i < 35; i++) {
            if (game.buyCard("Dogecoin") != null) {
                dogecoinCount++;
            }
        }
        assertEquals(29, dogecoinCount);
    }

    @Test
    public void testBuyUnsuccessfulMultipleCardsDepleted() {
        // Exhaust Framework, Dogecoin, and Module
        for (int i = 0; i < 8; i++) {
            game.buyCard("Framework");
            game.buyCard("Module");
        }
        for (int i = 0; i < 30; i++) {
            game.buyCard("Dogecoin");
        }
        
        // Try to buy with value 8
        // Should fall through to Ethereum (cost 3)
        player.buy(8);
        
        // Verify Ethereum was bought (39 remaining from 40)
        int ethereumCount = 0;
        for (int i = 0; i < 45; i++) {
            if (game.buyCard("Ethereum") != null) {
                ethereumCount++;
            }
        }
        assertEquals(39, ethereumCount);
    }

    @Test
    public void testBuyWithValueBetweenTiers() {
        // Test with value 4 - should buy Ethereum (cost 3, best available)
        player.buy(4);
        
        // Verify Ethereum was bought (39 remaining from 40)
        int ethereumCount = 0;
        for (int i = 0; i < 45; i++) {
            if (game.buyCard("Ethereum") != null) {
                ethereumCount++;
            }
        }
        assertEquals(39, ethereumCount);
    }

    @Test
    public void testDrawWithEnoughCardsInDeck() {
        // Clear the hand first (constructor already draws 5 cards)
        player.getHand().clear();
        
        // Ensure deck has at least 5 cards (constructor starts with 10 cards, 5 already drawn)
        // So deck should have 5 cards remaining
        int deckSizeBefore = player.getDeck().size();
        assertTrue("Deck should have at least 5 cards", deckSizeBefore >= 5);
        
        // Draw 5 cards
        player.draw();
        
        // Verify hand now has 5 cards
        assertEquals(5, player.getHand().size());
        
        // Verify deck size decreased by 5
        assertEquals(deckSizeBefore - 5, player.getDeck().size());
    }

    @Test
    public void testDrawWithEmptyDeck() {
        // Set up: clear hand, empty the deck, put some cards in discards
        player.getHand().clear();
        
        // Move all deck cards to discards
        while (!player.getDeck().isEmpty()) {
            player.getDiscards().push(player.getDeck().pop());
        }
        
        // Verify deck is empty
        assertEquals(0, player.getDeck().size());
        
        // Store discards size (should be 10 total cards from initial deck)
        int discardsSizeBefore = player.getDiscards().size();
        assertTrue("Should have cards in discards", discardsSizeBefore > 0);
        
        // Draw 5 cards - this should trigger shuffle from discards
        player.draw();
        
        // Verify hand has 5 cards
        assertEquals(5, player.getHand().size());
        
        // Verify discards are now empty (all moved to deck during shuffle)
        assertEquals(0, player.getDiscards().size());
        
        // Verify deck has remaining cards (original discards - 5 drawn)
        assertEquals(discardsSizeBefore - 5, player.getDeck().size());
    }

    @Test
    public void testDrawWithShuffleNeededDuringDraw() {
        // Set up: discard initial hand, leave only 2 cards in deck, put 8 in discards
        // Move hand to discards (5 cards from constructor's draw)
        while (!player.getHand().isEmpty()) {
            player.getDiscards().push(player.getHand().remove(0));
        }
        
        // Move cards to discards until deck has exactly 2 cards
        while (player.getDeck().size() > 2) {
            player.getDiscards().push(player.getDeck().pop());
        }
        
        // Verify setup
        assertEquals(2, player.getDeck().size());
        int discardsSizeBefore = player.getDiscards().size();
        assertEquals(8, discardsSizeBefore);
        
        // Draw 5 cards - should draw 2 from deck, shuffle discards, then draw 3 more
        player.draw();
        
        // Verify hand has 5 cards
        assertEquals(5, player.getHand().size());
        
        // Verify discards are empty (shuffled into deck)
        assertEquals(0, player.getDiscards().size());
        
        // Verify deck has remaining cards (8 discards shuffled in - 3 drawn = 5)
        assertEquals(5, player.getDeck().size());
    }

    @Test
    public void testDrawWithInsufficientTotalCards() {
        // Edge case: test when total cards < 5
        player.getHand().clear();
        
        // Remove cards until only 3 total remain
        while (player.getDeck().size() + player.getDiscards().size() > 3) {
            if (!player.getDeck().isEmpty()) {
                player.getDeck().pop();
            } else if (!player.getDiscards().isEmpty()) {
                player.getDiscards().pop();
            }
        }
        
        int totalCardsBefore = player.getDeck().size() + player.getDiscards().size();
        assertEquals(3, totalCardsBefore);
        
        // Draw 5 cards - should only draw 3 (all available)
        player.draw();
        
        // Verify hand has only 3 cards (all available cards)
        assertEquals(3, player.getHand().size());
        
        // Verify deck and discards are empty
        assertEquals(0, player.getDeck().size());
        assertEquals(0, player.getDiscards().size());
    }

    @Test
    public void testGetFinalScoreOnlyStartingCards() {
        // Test with only starting cards (7 Bitcoins and 3 Methods)
        // Bitcoins are currency (don't count), Methods are non-currency with value 1
        // Expected score: 3 * 1 = 3
        int score = player.getFinalScore();
        assertEquals(3, score);
    }

    @Test
    public void testGetFinalScoreCardsInDeckOnly() {
        // Clear hand and discards, add non-currency cards to deck
        player.getHand().clear();
        player.getDiscards().clear();
        player.getDeck().clear();
        
        // Add some non-currency cards to deck
        player.getDeck().push(new Card(5, 3, false, "Module")); // value 3
        player.getDeck().push(new Card(8, 6, false, "Framework")); // value 6
        player.getDeck().push(new Card(2, 1, false, "Method")); // value 1
        
        // Add currency cards (should not count)
        player.getDeck().push(new Card(0, 1, true, "Bitcoin"));
        player.getDeck().push(new Card(3, 2, true, "Ethereum"));
        
        // Expected score: 3 + 6 + 1 = 10
        assertEquals(10, player.getFinalScore());
    }

    @Test
    public void testGetFinalScoreCardsInDiscardsOnly() {
        // Clear everything and add cards only to discards
        player.getHand().clear();
        player.getDiscards().clear();
        player.getDeck().clear();
        
        // Add non-currency cards to discards
        player.getDiscards().push(new Card(5, 3, false, "Module")); // value 3
        player.getDiscards().push(new Card(8, 6, false, "Framework")); // value 6
        
        // Add currency cards (should not count)
        player.getDiscards().push(new Card(0, 1, true, "Bitcoin"));
        
        // Expected score: 3 + 6 = 9
        assertEquals(9, player.getFinalScore());
    }

    @Test
    public void testGetFinalScoreCardsInHandOnly() {
        // Clear everything and add cards only to hand
        player.getHand().clear();
        player.getDiscards().clear();
        player.getDeck().clear();
        
        // Add non-currency cards to hand
        player.getHand().add(new Card(5, 3, false, "Module")); // value 3
        player.getHand().add(new Card(2, 1, false, "Method")); // value 1
        player.getHand().add(new Card(2, 1, false, "Method")); // value 1
        
        // Add currency cards (should not count)
        player.getHand().add(new Card(6, 3, true, "Dogecoin"));
        
        // Expected score: 3 + 1 + 1 = 5
        assertEquals(5, player.getFinalScore());
    }

    @Test
    public void testGetFinalScoreCardsDistributedAcrossAll() {
        // Clear everything first
        player.getHand().clear();
        player.getDiscards().clear();
        player.getDeck().clear();
        
        // Add non-currency cards to deck
        player.getDeck().push(new Card(8, 6, false, "Framework")); // value 6
        player.getDeck().push(new Card(0, 1, true, "Bitcoin")); // currency, doesn't count
        
        // Add non-currency cards to discards
        player.getDiscards().push(new Card(5, 3, false, "Module")); // value 3
        player.getDiscards().push(new Card(2, 1, false, "Method")); // value 1
        player.getDiscards().push(new Card(3, 2, true, "Ethereum")); // currency, doesn't count
        
        // Add non-currency cards to hand
        player.getHand().add(new Card(2, 1, false, "Method")); // value 1
        player.getHand().add(new Card(5, 3, false, "Module")); // value 3
        player.getHand().add(new Card(6, 3, true, "Dogecoin")); // currency, doesn't count
        
        // Expected score: 6 + 3 + 1 + 1 + 3 = 14
        assertEquals(14, player.getFinalScore());
    }

    @Test
    public void testGetFinalScoreOnlyCurrencyCards() {
        // Clear everything and add only currency cards
        player.getHand().clear();
        player.getDiscards().clear();
        player.getDeck().clear();
        
        // Add currency cards to all locations
        player.getDeck().push(new Card(0, 1, true, "Bitcoin"));
        player.getDeck().push(new Card(3, 2, true, "Ethereum"));
        player.getDiscards().push(new Card(6, 3, true, "Dogecoin"));
        player.getHand().add(new Card(0, 1, true, "Bitcoin"));
        
        // Expected score: 0 (no non-currency cards)
        assertEquals(0, player.getFinalScore());
    }

    @Test
    public void testGetFinalScoreEmptyPlayer() {
        // Clear all cards
        player.getHand().clear();
        player.getDiscards().clear();
        player.getDeck().clear();
        
        // Expected score: 0
        assertEquals(0, player.getFinalScore());
    }

    @Test
    public void testGetFinalScoreMultipleHighValueCards() {
        // Clear everything and add multiple high-value cards
        player.getHand().clear();
        player.getDiscards().clear();
        player.getDeck().clear();
        
        // Add multiple Frameworks (highest value non-currency card)
        player.getDeck().push(new Card(8, 6, false, "Framework")); // value 6
        player.getDiscards().push(new Card(8, 6, false, "Framework")); // value 6
        player.getHand().add(new Card(8, 6, false, "Framework")); // value 6
        player.getHand().add(new Card(8, 6, false, "Framework")); // value 6
        
        // Expected score: 6 + 6 + 6 + 6 = 24
        assertEquals(24, player.getFinalScore());
    }

    @Test
    public void testPlayTurnWithOnlyCurrencyCards() {
        // Set up hand with only currency cards (high value)
        player.getHand().clear();
        player.getHand().add(new Card(0, 1, true, "Bitcoin")); // value 1
        player.getHand().add(new Card(3, 2, true, "Ethereum")); // value 2
        player.getHand().add(new Card(6, 3, true, "Dogecoin")); // value 3
        player.getHand().add(new Card(6, 3, true, "Dogecoin")); // value 3
        player.getHand().add(new Card(3, 2, true, "Ethereum")); // value 2
        // Total currency value: 1 + 2 + 3 + 3 + 2 = 11
        
        int discardsSizeBefore = player.getDiscards().size();
        
        // Play turn
        player.playTurn();
        
        // Verify hand was cleared and new cards drawn
        assertEquals(5, player.getHand().size());
        
        // Verify old hand cards moved to discards (5 cards + any bought card)
        assertTrue(player.getDiscards().size() >= discardsSizeBefore + 5);
        
        // Verify a card was purchased (with value 11, should buy Framework)
        // Discards should have 5 old cards + 1 bought card = 6 more than before
        assertEquals(discardsSizeBefore + 6, player.getDiscards().size());
    }

    @Test
    public void testPlayTurnWithOnlyNonCurrencyCards() {
        // Set up hand with only non-currency cards (no buying power)
        player.getHand().clear();
        player.getHand().add(new Card(2, 1, false, "Method"));
        player.getHand().add(new Card(2, 1, false, "Method"));
        player.getHand().add(new Card(5, 3, false, "Module"));
        player.getHand().add(new Card(5, 3, false, "Module"));
        player.getHand().add(new Card(8, 6, false, "Framework"));
        // Total currency value: 0
        
        int discardsSizeBefore = player.getDiscards().size();
        
        // Play turn
        player.playTurn();
        
        // Verify hand was cleared and new cards drawn
        assertEquals(5, player.getHand().size());
        
        // With value 0, should still buy a Bitcoin (cost 0)
        // Discards should have 5 old cards + 1 Bitcoin = 6 more
        assertEquals(discardsSizeBefore + 6, player.getDiscards().size());
    }

    @Test
    public void testPlayTurnWithMixedCards() {
        // Set up hand with mix of currency and non-currency cards
        player.getHand().clear();
        player.getHand().add(new Card(0, 1, true, "Bitcoin")); // value 1
        player.getHand().add(new Card(2, 1, false, "Method")); // non-currency
        player.getHand().add(new Card(3, 2, true, "Ethereum")); // value 2
        player.getHand().add(new Card(5, 3, false, "Module")); // non-currency
        player.getHand().add(new Card(0, 1, true, "Bitcoin")); // value 1
        // Total currency value: 1 + 2 + 1 = 4
        
        int discardsSizeBefore = player.getDiscards().size();
        
        // Play turn
        player.playTurn();
        
        // Verify new hand drawn
        assertEquals(5, player.getHand().size());
        
        // With value 4, should buy Ethereum (cost 3)
        // Discards should have 5 old cards + 1 bought card
        assertEquals(discardsSizeBefore + 6, player.getDiscards().size());
    }

    @Test
    public void testPlayTurnWithHighValueCurrency() {
        // Set up hand with high currency value to buy expensive card
        player.getHand().clear();
        player.getHand().add(new Card(6, 3, true, "Dogecoin")); // value 3
        player.getHand().add(new Card(6, 3, true, "Dogecoin")); // value 3
        player.getHand().add(new Card(3, 2, true, "Ethereum")); // value 2
        player.getHand().add(new Card(0, 1, true, "Bitcoin")); // value 1
        player.getHand().add(new Card(0, 1, true, "Bitcoin")); // value 1
        // Total currency value: 3 + 3 + 2 + 1 + 1 = 10
        
        int frameworkCountBefore = 0;
        AutomationGame testGame = new AutomationGame();
        for (int i = 0; i < 10; i++) {
            if (testGame.buyCard("Framework") != null) {
                frameworkCountBefore++;
            }
        }
        
        // Play turn
        player.playTurn();
        
        // Verify new hand drawn
        assertEquals(5, player.getHand().size());
        
        // With value 10, should buy Framework (cost 8)
        // Check that a Framework was bought from supply
        int frameworkCountAfter = 0;
        for (int i = 0; i < 10; i++) {
            if (game.buyCard("Framework") != null) {
                frameworkCountAfter++;
            }
        }
        assertEquals(frameworkCountBefore - 1, frameworkCountAfter);
    }

    @Test
    public void testPlayTurnWithLowValueCurrency() {
        // Set up hand with low currency value (can only buy cheap cards)
        player.getHand().clear();
        player.getHand().add(new Card(0, 1, true, "Bitcoin")); // value 1
        player.getHand().add(new Card(2, 1, false, "Method")); // non-currency
        player.getHand().add(new Card(2, 1, false, "Method")); // non-currency
        player.getHand().add(new Card(5, 3, false, "Module")); // non-currency
        player.getHand().add(new Card(8, 6, false, "Framework")); // non-currency
        // Total currency value: 1
        
        int discardsSizeBefore = player.getDiscards().size();
        
        // Play turn
        player.playTurn();
        
        // Verify new hand drawn
        assertEquals(5, player.getHand().size());
        
        // With value 1, should buy Bitcoin (cost 0)
        // Discards should have 5 old cards + 1 Bitcoin
        assertEquals(discardsSizeBefore + 6, player.getDiscards().size());
    }

    @Test
    public void testPlayTurnWithEmptyHand() {
        // Set up with empty hand (edge case)
        player.getHand().clear();
        
        int discardsSizeBefore = player.getDiscards().size();
        
        // Play turn
        player.playTurn();
        
        // Verify new hand drawn (5 cards)
        assertEquals(5, player.getHand().size());
        
        // With value 0, should buy Bitcoin
        // Discards should have 0 old cards + 1 Bitcoin = 1 more
        assertEquals(discardsSizeBefore + 1, player.getDiscards().size());
    }

    @Test
    public void testPlayTurnMultipleTurns() {
        // Test playing multiple turns to ensure state is properly maintained
        
        // Play first turn
        player.playTurn();
        assertEquals(5, player.getHand().size());
        
        // Play second turn
        player.playTurn();
        assertEquals(5, player.getHand().size());
        
        // Play third turn
        player.playTurn();
        assertEquals(5, player.getHand().size());
        
        // Verify cards are cycling properly (total cards should increase due to purchases)
        int totalCards = player.getDeck().size() + player.getDiscards().size() + player.getHand().size();
        // Started with 10 cards, bought 3 cards over 3 turns
        assertTrue(totalCards >= 13);
    }

    @Test
    public void testPlayTurnExactValueForCard() {
        // Test with exact currency value to buy a specific tier card
        player.getHand().clear();
        player.getHand().add(new Card(3, 2, true, "Ethereum")); // value 2
        player.getHand().add(new Card(2, 1, false, "Method")); // non-currency
        player.getHand().add(new Card(2, 1, false, "Method")); // non-currency
        player.getHand().add(new Card(2, 1, false, "Method")); // non-currency
        player.getHand().add(new Card(2, 1, false, "Method")); // non-currency
        // Total currency value: 2 (exact cost for Method)
        
        int methodCountBefore = 0;
        AutomationGame testGame = new AutomationGame();
        for (int i = 0; i < 10; i++) {
            if (testGame.buyCard("Method") != null) {
                methodCountBefore++;
            }
        }
        
        // Play turn
        player.playTurn();
        
        // Verify new hand drawn
        assertEquals(5, player.getHand().size());
        
        // With value 2, should buy Method (cost 2)
        int methodCountAfter = 0;
        for (int i = 0; i < 10; i++) {
            if (game.buyCard("Method") != null) {
                methodCountAfter++;
            }
        }
        assertEquals(methodCountBefore - 1, methodCountAfter);
    }
}
