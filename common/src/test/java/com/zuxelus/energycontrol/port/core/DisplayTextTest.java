// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class DisplayTextTest {
    @Test void preservesTenLinesAndRemovesOnlyTrailingBlanks() {
        assertEquals(List.of("a","","b"), DisplayText.lines("a\r\n\r\nb\n\n"));
        assertEquals(10,DisplayText.lines("x\n".repeat(12)).size());
    }
    @Test void portsUpstreamFormattingEscapes() {
        assertEquals(List.of("\u00a7aGreen @ mail"),DisplayText.lines("@aGreen @@ mail"));
    }
    @Test void limitsMalformedImportedCardData() {
        assertEquals(128,DisplayText.lines("a".repeat(200)).getFirst().length());
        assertEquals(List.of(),DisplayText.lines("\n\n"));
    }
}
