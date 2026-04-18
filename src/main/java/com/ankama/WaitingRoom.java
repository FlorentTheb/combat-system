package com.ankama;

import java.util.concurrent.CopyOnWriteArrayList;

public class WaitingRoom {

    private static final WaitingRoom waitingRoom = new WaitingRoom();

    private static CopyOnWriteArrayList<ClientServerHandler> waitingRoomClients = new CopyOnWriteArrayList<>();

    public static WaitingRoom getInstance() {
        return waitingRoom;
    }

    public void addToWaitingRoom(ClientServerHandler client) {
        if (waitingRoomClients.add(client)) {
            if (waitingRoomClients.size() >= 2) {
                CombatGroup combatGroup = new CombatGroup(waitingRoomClients.get(0), waitingRoomClients.get(1));
                Thread thread = new Thread(combatGroup);
                thread.start();
                waitingRoomClients.subList(0, 1).clear();
            } else {
                client.sendMessageToClient("Waiting for another player to join");
            }
        }
    }

    public CopyOnWriteArrayList<ClientServerHandler> getClients() {
        return waitingRoomClients;
    }

    public void removeFromWaitingRoom(ClientServerHandler client) {
        waitingRoomClients.remove(client);
    }
}
