package edu.brandeis.cosi103a.ip2;

public class Driver {
    public static void main(String args[]) {
        // Set up game and players
        AutomationGame game = new AutomationGame();
        Player[] players = new Player[2];
        players[0] = new Player(game, "Player 1");
        players[1] = new Player(game, "Player 2");
        // Play until the game ends (all Framework cards are bought)
        while (game.checkGameEnd() == false) {
            for (Player player : players) {
                player.playTurn();
            }
        }
        // Game over, show final scores
        System.out.println("Game over!");
        for (Player player : players) {
            System.out.println(player.getName() + "'s final score: " + player.getFinalScore());
        }
    }
}
