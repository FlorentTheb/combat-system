package com.ankama;

public interface IPlayer {

    int getHealthPoints();

    void hit(int damageAmount);

    String getPseudo();
}
