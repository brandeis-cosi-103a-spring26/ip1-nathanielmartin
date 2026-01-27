package edu.brandeis.cosi103a.ip2;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class GameTest {
    private AutomationGame game;

    @Before
    public void setUp() {
        game = new AutomationGame();
    }

    @Test
    public void testBuyCardBitcoinInSupply() {
        // Test buying a Bitcoin (there are 46 in the initial supply)
        Card card = game.buyCard("Bitcoin");
        
        // Verify a card was returned
        assertNotNull(card);
        
        // Verify it's the correct card
        assertEquals("Bitcoin", card.getName());
        assertEquals(0, card.getCost());
        assertEquals(1, card.getValue());
        assertTrue(card.isCurrency());
        
        // Verify supply decreased - count remaining Bitcoins
        int bitcoinCount = 0;
        for (int i = 0; i < 50; i++) {
            Card c = game.buyCard("Bitcoin");
            if (c != null) {
                bitcoinCount++;
            }
        }
        assertEquals(45, bitcoinCount); // 46 - 1 already bought = 45
    }

    @Test
    public void testBuyCardEthereumInSupply() {
        // Test buying Ethereum (there are 40 in the initial supply)
        Card card = game.buyCard("Ethereum");
        
        assertNotNull(card);
        assertEquals("Ethereum", card.getName());
        assertEquals(3, card.getCost());
        assertEquals(2, card.getValue());
        assertTrue(card.isCurrency());
    }

    @Test
    public void testBuyCardDogecoinInSupply() {
        // Test buying Dogecoin (there are 30 in the initial supply)
        Card card = game.buyCard("Dogecoin");
        
        assertNotNull(card);
        assertEquals("Dogecoin", card.getName());
        assertEquals(6, card.getCost());
        assertEquals(3, card.getValue());
        assertTrue(card.isCurrency());
    }

    @Test
    public void testBuyCardMethodInSupply() {
        // Test buying Method (there are 8 in the initial supply)
        Card card = game.buyCard("Method");
        
        assertNotNull(card);
        assertEquals("Method", card.getName());
        assertEquals(2, card.getCost());
        assertEquals(1, card.getValue());
        assertFalse(card.isCurrency());
    }

    @Test
    public void testBuyCardModuleInSupply() {
        // Test buying Module (there are 8 in the initial supply)
        Card card = game.buyCard("Module");
        
        assertNotNull(card);
        assertEquals("Module", card.getName());
        assertEquals(5, card.getCost());
        assertEquals(3, card.getValue());
        assertFalse(card.isCurrency());
    }

    @Test
    public void testBuyCardFrameworkInSupply() {
        // Test buying Framework (there are 8 in the initial supply)
        Card card = game.buyCard("Framework");
        
        assertNotNull(card);
        assertEquals("Framework", card.getName());
        assertEquals(8, card.getCost());
        assertEquals(6, card.getValue());
        assertFalse(card.isCurrency());
    }

    @Test
    public void testBuyCardNotInSupply() {
        // Test buying a card that doesn't exist
        Card card = game.buyCard("NonexistentCard");
        
        // Should return null
        assertNull(card);
    }

    @Test
    public void testBuyCardWrongCaseName() {
        // Test buying a card with wrong case (should not match)
        Card card = game.buyCard("bitcoin"); // lowercase instead of "Bitcoin"
        
        // Should return null because name matching is case-sensitive
        assertNull(card);
    }

    @Test
    public void testBuyCardAfterDepletion() {
        // Exhaust all Framework cards (8 total)
        for (int i = 0; i < 8; i++) {
            Card card = game.buyCard("Framework");
            assertNotNull("Should buy Framework #" + (i + 1), card);
        }
        
        // Try to buy another Framework - should fail
        Card card = game.buyCard("Framework");
        assertNull(card);
    }

    @Test
    public void testBuyCardMultiplePurchases() {
        // Buy multiple cards and verify each purchase
        Card card1 = game.buyCard("Bitcoin");
        Card card2 = game.buyCard("Ethereum");
        Card card3 = game.buyCard("Method");
        
        assertNotNull(card1);
        assertNotNull(card2);
        assertNotNull(card3);
        
        assertEquals("Bitcoin", card1.getName());
        assertEquals("Ethereum", card2.getName());
        assertEquals("Method", card3.getName());
    }

    @Test
    public void testBuyCardSupplyDecreases() {
        // Count initial Methods
        int methodCountInitial = 0;
        AutomationGame testGame = new AutomationGame();
        for (int i = 0; i < 10; i++) {
            if (testGame.buyCard("Method") != null) {
                methodCountInitial++;
            }
        }
        assertEquals(8, methodCountInitial);
        
        // Buy one Method from the main game
        Card card = game.buyCard("Method");
        assertNotNull(card);
        
        // Count remaining Methods in main game
        int methodCountAfter = 0;
        for (int i = 0; i < 10; i++) {
            if (game.buyCard("Method") != null) {
                methodCountAfter++;
            }
        }
        
        // Should be one less
        assertEquals(7, methodCountAfter);
    }

    @Test
    public void testBuyCardEmptyStringName() {
        // Test buying with empty string
        Card card = game.buyCard("");
        
        assertNull(card);
    }

    @Test
    public void testBuyCardPartialName() {
        // Test buying with partial name (should not match)
        Card card = game.buyCard("Bit"); // partial match of "Bitcoin"
        
        assertNull(card);
    }

    @Test
    public void testCheckGameEndGameNotEnded() {
        // At the start, there are 8 Framework cards in supply
        // Game should not have ended
        assertFalse(game.checkGameEnd());
    }

    @Test
    public void testCheckGameEndAfterBuyingSomeFrameworks() {
        // Buy some but not all Framework cards
        game.buyCard("Framework");
        game.buyCard("Framework");
        game.buyCard("Framework");
        
        // Game should still not have ended (5 Frameworks remaining)
        assertFalse(game.checkGameEnd());
    }

    @Test
    public void testCheckGameEndGameHasEnded() {
        // Buy all 8 Framework cards to deplete supply
        for (int i = 0; i < 8; i++) {
            Card card = game.buyCard("Framework");
            assertNotNull("Should successfully buy Framework #" + (i + 1), card);
        }
        
        // Game should have ended now
        assertTrue(game.checkGameEnd());
    }

    @Test
    public void testCheckGameEndAfterBuyingAllButOneFramework() {
        // Buy 7 of 8 Framework cards
        for (int i = 0; i < 7; i++) {
            game.buyCard("Framework");
        }
        
        // Game should not have ended (1 Framework remaining)
        assertFalse(game.checkGameEnd());
        
        // Buy the last Framework
        game.buyCard("Framework");
        
        // Now game should have ended
        assertTrue(game.checkGameEnd());
    }

    @Test
    public void testCheckGameEndOtherCardsRemaining() {
        // Deplete only Framework cards, leave other cards
        for (int i = 0; i < 8; i++) {
            game.buyCard("Framework");
        }
        
        // Even though other cards remain in supply, game should have ended
        assertTrue(game.checkGameEnd());
        
        // Verify other cards are still available
        assertNotNull(game.buyCard("Bitcoin"));
        assertNotNull(game.buyCard("Ethereum"));
        assertNotNull(game.buyCard("Method"));
    }

    @Test
    public void testCheckGameEndMultipleCalls() {
        // Test that checkGameEnd can be called multiple times without side effects
        
        // Initially game hasn't ended
        assertFalse(game.checkGameEnd());
        assertFalse(game.checkGameEnd());
        assertFalse(game.checkGameEnd());
        
        // Deplete Frameworks
        for (int i = 0; i < 8; i++) {
            game.buyCard("Framework");
        }
        
        // Game has ended - multiple calls should still return true
        assertTrue(game.checkGameEnd());
        assertTrue(game.checkGameEnd());
        assertTrue(game.checkGameEnd());
    }

    @Test
    public void testCheckGameEndEmptySupply() {
        // Edge case: deplete entire supply
        // Buy all cards of each type
        for (int i = 0; i < 46; i++) {
            game.buyCard("Bitcoin");
        }
        for (int i = 0; i < 40; i++) {
            game.buyCard("Ethereum");
        }
        for (int i = 0; i < 30; i++) {
            game.buyCard("Dogecoin");
        }
        for (int i = 0; i < 8; i++) {
            game.buyCard("Method");
            game.buyCard("Module");
            game.buyCard("Framework");
        }
        
        // Game should have ended (no Frameworks left)
        assertTrue(game.checkGameEnd());
    }
}
