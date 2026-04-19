package com.ankama;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;

public class WaitingRoomTest {

    @BeforeEach
    public void init() {
        WaitingRoom.getInstance().removeAllFromWaitingRoom();
    }

    @Test
    public void testAddOnePlayerToWaitingRoom() {
        ClientServerHandler handler = Mockito.mock(ClientServerHandler.class);
        Mockito.when(handler.getClientPseudo()).thenReturn("PlayerOne");
        WaitingRoom.getInstance().addToWaitingRoom(handler);

        ClientServerHandler firstHandlerInList = WaitingRoom.getInstance().getWaitingRoomClients().get(0);
        assertEquals(handler, firstHandlerInList);

        int sizeOfRoom = WaitingRoom.getInstance().getWaitingRoomClients().size();
        assertEquals(1, sizeOfRoom);
    }

    @Test
    public void testAddTwoPlayersToWaitingRoom() {
        ClientServerHandler handler1 = Mockito.mock(ClientServerHandler.class);
        HumanPlayer player1 = new HumanPlayer(handler1, 50);
        Mockito.when(handler1.getClientPseudo()).thenReturn("PlayerOne");
        Mockito.when(handler1.getPlayer()).thenReturn(player1);

        ClientServerHandler handler2 = Mockito.mock(ClientServerHandler.class);
        HumanPlayer player2 = new HumanPlayer(handler2, 50);
        Mockito.when(handler2.getClientPseudo()).thenReturn("PlayerTwo");
        Mockito.when(handler2.getPlayer()).thenReturn(player2);

        WaitingRoom.getInstance().addToWaitingRoom(handler1);

        int sizeOfRoom = WaitingRoom.getInstance().getWaitingRoomClients().size();
        assertEquals(1, sizeOfRoom);

        WaitingRoom.getInstance().addToWaitingRoom(handler2);
        sizeOfRoom = WaitingRoom.getInstance().getWaitingRoomClients().size();
        assertEquals(0, sizeOfRoom);
    }

    @Test
    public void testRemoveFromWaitingRoom() {
        ClientServerHandler handler = Mockito.mock(ClientServerHandler.class);
        WaitingRoom.getInstance().addToWaitingRoom(handler);

        int sizeOfRoom = WaitingRoom.getInstance().getWaitingRoomClients().size();
        assertEquals(1, sizeOfRoom);

        WaitingRoom.getInstance().removeFromWaitingRoom(handler);
        sizeOfRoom = WaitingRoom.getInstance().getWaitingRoomClients().size();
        assertEquals(0, sizeOfRoom);
    }

}
