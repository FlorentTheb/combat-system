package com.ankama;

import java.util.concurrent.CopyOnWriteArrayList;

public class WaitingRoom {

    private static final WaitingRoom waitingRoom = new WaitingRoom();

    private static CopyOnWriteArrayList<ClientServerHandler> waitingRoomClients = new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<ClientServerHandler> getWaitingRoomClients() {
        return waitingRoomClients;
    }

    public static WaitingRoom getInstance() {
        return waitingRoom;
    }

    public void addToWaitingRoom(ClientServerHandler client) {
        if (waitingRoomClients.add(client)) {
            if (waitingRoomClients.size() >= 2) {
                CombatGroup combatGroup = new CombatGroup(waitingRoomClients.get(0), waitingRoomClients.get(1));
                waitingRoomClients.get(0).setGroup(combatGroup);
                waitingRoomClients.get(1).setGroup(combatGroup);
                Thread thread = new Thread(combatGroup);
                thread.start();
                waitingRoomClients.remove(0);
                waitingRoomClients.remove(0);
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

    public void removeAllFromWaitingRoom() {
        waitingRoomClients.clear();
    }
}
