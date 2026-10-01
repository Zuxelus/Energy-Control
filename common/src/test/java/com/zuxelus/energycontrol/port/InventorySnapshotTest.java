// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port;
import com.zuxelus.energycontrol.port.inventory.InventorySnapshot;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class InventorySnapshotTest {
 @Test void totalIncludesSlotsBeyondSixPreview(){var c=new InventorySnapshot.Collector("Chest",false,true,27);for(int i=0;i<27;i++)c.add("Stone",i+1,false);var s=c.finish(false);assertEquals(new BigDecimal("378"),s.items());assertEquals(27,s.usedSlots());assertEquals(6,s.preview().size());assertFalse(s.truncated());}
 @Test void countsBeyondLongRetainExactTotal(){var c=new InventorySnapshot.Collector("Storage",false,false,-1);c.add("A",Long.MAX_VALUE,false);c.add("B",Long.MAX_VALUE,false);assertEquals(new BigDecimal("18446744073709551614"),c.finish(false).items());}
 @Test void emptyAndNegativeViewsNeverInflateCounts(){var c=new InventorySnapshot.Collector("Storage",true,true,3);c.add("",99,true);c.add("Invalid",-1,false);c.add("Iron",7,false);var s=c.finish(false);assertEquals(BigDecimal.valueOf(7),s.items());assertEquals(1,s.usedSlots());assertEquals("Empty",s.preview().getFirst());assertEquals(3333,s.rows(0,true,true).getFirst().fill());}
 @Test void boundedScanExplicitlyReportsPartialResult(){var c=new InventorySnapshot.Collector("Large",false,true,5000);for(int i=0;i<4096;i++)assertTrue(c.add("Stone",1,false));assertFalse(c.add("Stone",1,false));var s=c.finish(true);assertTrue(s.truncated());assertEquals(4096,s.scannedSlots());assertTrue(s.rows(31,true,false).getLast().text().startsWith("Partial inventory"));}
 @Test void fieldsCanHideEveryDetailWithoutHidingTruncation(){var c=new InventorySnapshot.Collector("Chest",false,true,1);c.add("Stone",1,false);assertTrue(c.finish(false).rows(0,true,false).isEmpty());var data=new net.minecraft.nbt.CompoundTag();assertEquals(31,InventorySnapshot.fields(data));data.putInt("inventoryFields",0);assertEquals(0,InventorySnapshot.fields(data));data.putInt("inventoryFields",255);assertEquals(31,InventorySnapshot.fields(data));}
}
