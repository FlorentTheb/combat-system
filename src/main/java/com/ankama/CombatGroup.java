package com.ankama;

public class CombatGroup implements Runnable {

    ClientServerHandler playerHandler1;
    ClientServerHandler playerHandler2;

    public CombatGroup(ClientServerHandler handler1, ClientServerHandler handler2) {
        this.playerHandler1 = handler1;
        this.playerHandler2 = handler2;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
}
