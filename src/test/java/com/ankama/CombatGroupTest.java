package com.ankama;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CombatGroupTest {

    private CombatGroup group1;
    private CombatGroup group2;

    @BeforeEach
    public void initGroup() {
        ClientServerHandler handler1 = Mockito.mock(ClientServerHandler.class);
        Mockito.when(handler1.getClientPseudo()).thenReturn("PlayerOne");
        Mockito.when(handler1.getPlayer()).thenReturn(new HumanPlayer(handler1, 50));

        ClientServerHandler handler2 = Mockito.mock(ClientServerHandler.class);
        Mockito.when(handler2.getClientPseudo()).thenReturn("PlayerTwo");
        Mockito.when(handler2.getPlayer()).thenReturn(new HumanPlayer(handler2, 50));

        ClientServerHandler handler3 = Mockito.mock(ClientServerHandler.class);
        Mockito.when(handler3.getClientPseudo()).thenReturn("PlayerThree");
        Mockito.when(handler3.getPlayer()).thenReturn(new HumanPlayer(handler3, 50));
        group1 = new CombatGroup(handler1, handler2);
        group2 = new CombatGroup(handler3);
    }

    @Test
    public void testGetPlayersOpponents() {
        IPlayer player1 = group1.getPlayers().get(0);
        IPlayer player2 = group1.getPlayers().get(1);
        assertEquals(player2, group1.getOpponent(player1));
        assertEquals(player1, group1.getOpponent(player2));
    }

    @Test
    public void testTypeOfPlayers() {
        IPlayer player1 = group1.getPlayers().get(0);
        IPlayer player2 = group1.getPlayers().get(1);
        IPlayer player3 = group2.getPlayers().get(0);
        IPlayer player4 = group2.getPlayers().get(1);
        assertTrue(player1 instanceof HumanPlayer);
        assertTrue(player2 instanceof HumanPlayer);
        assertTrue(player3 instanceof HumanPlayer);
        assertTrue(player4 instanceof RobotPlayer);
    }

    @Test
    public void testAttackEventSuccess() {
        IPlayer player1 = group1.getPlayers().get(0);
        IPlayer player2 = group1.getPlayers().get(1);
        group1.tryAttack(20, 1, player1, player2);
        assertEquals(50, player1.getHealthPoints());
        assertEquals(30, player2.getHealthPoints());
    }

    @Test
    public void testAttackEventFail() {
        IPlayer player1 = group1.getPlayers().get(0);
        IPlayer player2 = group1.getPlayers().get(1);
        group1.tryAttack(20, 0, player1, player2);
        assertEquals(50, player1.getHealthPoints());
        assertEquals(50, player2.getHealthPoints());
    }

    @Test
    public void testAttackKillsTargetAndEndGame() {
        IPlayer player1 = group1.getPlayers().get(0);
        IPlayer player2 = group1.getPlayers().get(1);
        group1.tryAttack(50, 1, player1, player2);
        assertEquals(0, player2.getHealthPoints());
        assertTrue(group1.getIsGameOver());
    }

    @Test
    public void testSwitchingTurnsAfterAttack() {
        IPlayer player1 = group1.getPlayers().get(0);
        IPlayer player2 = group1.getPlayers().get(1);

        int turnIndex = group1.getTurnIndex();
        IPlayer playerPlaying = group1.getPlayers().get(turnIndex);

        group1.tryAttack(20, 1, player1, player2);
        turnIndex = group1.getTurnIndex();
        IPlayer newPlayerPlaying = group1.getPlayers().get(turnIndex);
        assertNotEquals(playerPlaying, newPlayerPlaying);
    }
}
