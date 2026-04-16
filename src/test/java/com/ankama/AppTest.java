package com.ankama;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AppTest {

    static App app = new App("Jack", "Sparrow", 22);

    @Test
    void testName() {
        assertEquals("Jack", app.getName());
    }

    @Test
    void testSurname() {
        assertEquals("Sparrow", app.getSurname());
    }

    @Test
    void testAge() {
        assertEquals(22, app.getAge());
    }
}
