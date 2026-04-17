package com.ankama;

import java.util.concurrent.CopyOnWriteArrayList;

public class WaitingRoom {

    private static final WaitingRoom waitingRoom = new WaitingRoom();

    private static CopyOnWriteArrayList<ClientServerHandler> waitingRoomClients = new CopyOnWriteArrayList<>();

    public static WaitingRoom getInstance() {
        return waitingRoom;
    }

    public Boolean addToWaitingRoom(ClientServerHandler client) {
        return waitingRoomClients.add(client);
    }

    public CopyOnWriteArrayList<ClientServerHandler> getClients() {
        return waitingRoomClients;
    }

    public void removeFromWaitingRoom(ClientServerHandler client) {
        waitingRoomClients.remove(client);
    }
}
