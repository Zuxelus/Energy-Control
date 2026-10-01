// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port;
import com.zuxelus.energycontrol.port.menu.CardHolderInventory;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
class CardHolderInventoryTest {
 @BeforeAll static void boot(){SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @Test void lastSlotPersistsAndCopiedHolderDoesNotAlias(){var parent=new ItemStack(Items.PAPER);var inv=new CardHolderInventory(parent);assertEquals(54,inv.getContainerSize());inv.setItem(53,new ItemStack(Items.DIAMOND));var copy=parent.copy();new CardHolderInventory(copy).removeItem(53,1);assertTrue(new CardHolderInventory(copy).getItem(53).isEmpty());assertEquals(Items.DIAMOND,new CardHolderInventory(parent).getItem(53).getItem());}
 @Test void ordinaryItemsCannotBeInsertedThroughSlotPolicy(){var inv=new CardHolderInventory(new ItemStack(Items.PAPER));assertFalse(inv.canPlaceItem(0,new ItemStack(Items.CHEST)));assertFalse(inv.canPlaceItem(53,new ItemStack(Items.PAPER)));assertEquals(1,inv.getMaxStackSize());}
}
