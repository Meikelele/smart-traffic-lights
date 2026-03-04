package pl.mbak.traffic.engine.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TurnsTest {
    @Test
    void shouldComputeRightTurns() {
        assertEquals(Turn.RIGHT, Turns.compute(Road.south, Road.east));
        assertEquals(Turn.RIGHT, Turns.compute(Road.east, Road.north));
        assertEquals(Turn.RIGHT, Turns.compute(Road.north, Road.west));
        assertEquals(Turn.RIGHT, Turns.compute(Road.west, Road.south));
    }

    @Test
    void shouldComputeLeftTurns() {
        assertEquals(Turn.LEFT, Turns.compute(Road.south, Road.west));
        assertEquals(Turn.LEFT, Turns.compute(Road.west, Road.north));
        assertEquals(Turn.LEFT, Turns.compute(Road.north, Road.east));
        assertEquals(Turn.LEFT, Turns.compute(Road.east, Road.south));
    }

    @Test
    void shouldComputeStraightTurns() {
        assertEquals(Turn.STRAIGHT, Turns.compute(Road.north, Road.south));
        assertEquals(Turn.STRAIGHT, Turns.compute(Road.south, Road.north));
        assertEquals(Turn.STRAIGHT, Turns.compute(Road.east, Road.west));
        assertEquals(Turn.STRAIGHT, Turns.compute(Road.west, Road.east));
    }

    @Test
    void shouldRejectInvalidMovement() {
        assertThrows(IllegalArgumentException.class, () -> Turns.compute(Road.north, Road.north));
        assertThrows(IllegalArgumentException.class, () -> Turns.compute(null, Road.north));
        assertThrows(IllegalArgumentException.class, () -> Turns.compute(Road.north, null));
        assertThrows(IllegalArgumentException.class, () -> Turns.compute(null, null));
    }
}
