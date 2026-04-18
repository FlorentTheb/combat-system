package com.ankama;

public class RobotPlayer implements IPlayer {

    private int healthPoints;

    public RobotPlayer(int baseHealthPoints) {
        this.healthPoints = baseHealthPoints;
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
        return "AI";
    }

    public String makeAIChoice() {
        return (Math.random() <= .5) ? "1" : "2";
    }

}
