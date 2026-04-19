package com.ankama;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HumanPlayerTest {

    private ClientServerHandler mockHandler;
    private HumanPlayer player;

    @BeforeEach
    public void initMock() {
        mockHandler = Mockito.mock(ClientServerHandler.class);
        player = new HumanPlayer(mockHandler, 50);
    }

    @Test
    public void testDefaultHP() {
        int hp = player.getHealthPoints();
        assertEquals(50, hp);
    }

    @Test
    public void testStrongAttack() {
        player.hit(20);
        assertEquals(30, player.getHealthPoints());
    }

    @Test
    public void testLightAttack() {
        player.hit(10);
        assertEquals(40, player.getHealthPoints());
    }

    @Test
    public void testDamageAllHP() {
        player.hit(50);
        assertEquals(0, player.getHealthPoints());
    }

    @Test
    public void testDamageMoreThanMaxHP() {
        player.hit(80);
        assertEquals(0, player.getHealthPoints());
    }

}
