package com.zuxelus.energycontrol.port;
import com.zuxelus.energycontrol.port.menu.PortableInventory;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
class PortableInventoryTest {
 @BeforeAll static void boot(){SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @Test void changedSlotPersistsAndCopiedPortableHasIndependentContents(){
  var parent=new ItemStack(Items.PAPER);var inventory=new PortableInventory(parent);
  inventory.setItem(0,new ItemStack(Items.DIAMOND));
  assertEquals(Items.DIAMOND,new PortableInventory(parent).getItem(0).getItem());
  var copy=parent.copy();new PortableInventory(copy).setItem(0,new ItemStack(Items.EMERALD));
  assertEquals(Items.DIAMOND,new PortableInventory(parent).getItem(0).getItem());
  assertEquals(Items.EMERALD,new PortableInventory(copy).getItem(0).getItem());
 }
 @Test void removingCardPersistsEmptySlot(){
  var parent=new ItemStack(Items.PAPER);var inventory=new PortableInventory(parent);
  inventory.setItem(0,new ItemStack(Items.DIAMOND));assertFalse(new PortableInventory(parent).getItem(0).isEmpty());inventory.removeItem(0,1);
  assertTrue(new PortableInventory(parent).getItem(0).isEmpty());
 }
}
