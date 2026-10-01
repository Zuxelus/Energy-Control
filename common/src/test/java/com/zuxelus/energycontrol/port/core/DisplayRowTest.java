// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class DisplayRowTest {
 @Test void hugeExactAmountsProduceFractionWithoutLongOverflow(){assertEquals(3750,DisplayRow.bar("Stored",new BigDecimal("3000000000000000000000000000000"),new BigDecimal("8000000000000000000000000000000")).fill());}
 @Test void emptyOverfilledAndNegativeReadingsAreBounded(){assertEquals(0,DisplayRow.bar("",BigDecimal.TEN,BigDecimal.ZERO).fill());assertEquals(10000,DisplayRow.bar("",BigDecimal.TEN,BigDecimal.ONE).fill());assertEquals(0,DisplayRow.bar("",BigDecimal.ONE.negate(),BigDecimal.TEN).fill());}
 @Test void displayTextCannotBecomeABar(){assertFalse(DisplayRow.text("Energy: 90% [bar:9000]").isBar());assertEquals(-1,DisplayRow.load(new net.minecraft.nbt.CompoundTag()).fill());}
 @Test void savedFractionAndUnicodeRemainSeparate(){var row=new DisplayRow("水 / Water",1234);assertEquals(row,DisplayRow.load(row.save()));assertEquals(256,new DisplayRow("x".repeat(300),99999).text().length());assertEquals(10000,new DisplayRow("",99999).fill());}
}
