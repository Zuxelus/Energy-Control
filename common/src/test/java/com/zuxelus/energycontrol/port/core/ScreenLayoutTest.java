package com.zuxelus.energycontrol.port.core;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ScreenLayoutTest {
    @Test void extendsOnlyAcrossCompleteRows() {
        // A missing corner must not become a drawable part of the rectangle.
        var parts = Set.of(new ScreenLayout.Cell(1, 0), new ScreenLayout.Cell(2, 0),
                new ScreenLayout.Cell(0, 1), new ScreenLayout.Cell(1, 1));
        assertEquals(new ScreenLayout.Bounds(0, 0, 2, 0),
                ScreenLayout.grow((x, y) -> parts.contains(new ScreenLayout.Cell(x, y))));
    }
    @Test void growsNegativeCoordinatesAndStopsAtTwentyPerDirection() {
        assertEquals(new ScreenLayout.Bounds(-20, -20, 20, 20), ScreenLayout.grow((x,y) -> true));
    }
    @Test void fillsCompleteRectangleAndRebuildsAfterRemoval() {
        assertEquals(new ScreenLayout.Bounds(-1, -1, 1, 1),
                ScreenLayout.grow((x,y) -> Math.abs(x) <= 1 && Math.abs(y) <= 1));
        assertEquals(new ScreenLayout.Bounds(0, -1, 1, 1),
                ScreenLayout.grow((x,y) -> x >= 0 && x <= 1 && Math.abs(y) <= 1));
    }
    @Test void emptyWorldIsSinglePanel() {
        assertEquals(new ScreenLayout.Bounds(0,0,0,0), ScreenLayout.grow((x,y) -> false));
    }
}
