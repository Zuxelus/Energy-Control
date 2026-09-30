package com.zuxelus.energycontrol.port.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnergySnapshotTest {
    @Test void preservesLongEnergyWithoutIntTruncation() {
        var snapshot = new EnergySnapshot(8_000_000_000L, 16_000_000_000L, "E");
        assertEquals(0.5, snapshot.fraction());
        assertEquals(8_000_000_000L, snapshot.stored());
    }
    @Test void zeroCapacityAndOverfullReadingsCannotBreakRenderer() {
        assertEquals(0, new EnergySnapshot(0,0,"FE").fraction());
        assertEquals(1, new EnergySnapshot(120,100,"FE").fraction());
    }
    @Test void rejectsNegativeProviderValues() {
        assertThrows(IllegalArgumentException.class, () -> new EnergySnapshot(-1,10,"FE"));
        assertThrows(IllegalArgumentException.class, () -> new EnergySnapshot(1,-10,"FE"));
    }
}
