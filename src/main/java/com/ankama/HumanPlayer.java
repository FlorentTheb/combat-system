package com.ankama;

public class HumanPlayer implements IPlayer {

    private int healthPoints;
    private ClientServerHandler handler;

    public ClientServerHandler getHandler() {
        return handler;
    }

    public HumanPlayer(ClientServerHandler handler, int baseHealthpoints) {
        this.handler = handler;
        this.healthPoints = baseHealthpoints;
    }

    @Override
    public int getHealthPoints() {
        return healthPoints;
    }

    @Override
    public void hit(int damageAmount) {
        if (healthPoints > 0) {
            healthPoints -= damageAmount;
            if (healthPoints < 0)
                healthPoints = 0;
        }
    }

    @Override
    public String getPseudo() {
        return handler.getClientPseudo();
    }

    public void displayDamaged(String opponentPseudo, int damage) {
        String message = opponentPseudo + " hit you with " + damage + " DMG !\nYou have " + healthPoints
                + " remaining HP.";
        handler.sendMessageToClient(message);
    }

    public void displayHit(String opponentPseudo, int damage, int hpRemaining) {
        String message = "You hit " + opponentPseudo + " with " + damage + " DMG !\n"
                + opponentPseudo + " has " + hpRemaining + " remaining HP.";
        handler.sendMessageToClient(message);
    }

    public void displayDodge(String attackerPseudo) {
        String message = attackerPseudo + " missed ! Lucky you!";
        handler.sendMessageToClient(message);
    }

    public void displayMiss() {
        String message = "You missed ! Better luck next time ...";
        handler.sendMessageToClient(message);
    }

    public void computeWinByForfeit(String loserPseudo) {
        String message = "WIN BY FORFEIT ! " + loserPseudo + " disconnected.";
        handler.sendMessageToClient(message);
    }

    public void computeWin(String loserPseudo) {
        String message = "WIN ! " + loserPseudo + " couldn't handle the pressure ! Well played !";
        handler.sendMessageToClient(message);
    }

    public void computeLoss(String winnerPseudo) {
        String message = "LOST ! " + winnerPseudo + " was better than you this time...";
        handler.sendMessageToClient(message);
    }

    public void displayActionsAvailable() {
        String actionsMsg = "Your turn !\n1 -> Massive Attack (20 DMG) | 50% Success Rate\n2 -> Light Attack (10 DMG)   | 90% Success Rate";
        handler.sendMessageToClient(actionsMsg);
    }
}
