// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.menu;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import com.zuxelus.energycontrol.port.card.CardItem;
public final class CardHolderInventory extends SimpleContainer {
 private final ItemStack parent;private boolean loading=true;
 public CardHolderInventory(ItemStack parent){super(54);this.parent=parent;var contents=NonNullList.withSize(54,ItemStack.EMPTY);parent.getOrDefault(DataComponents.CONTAINER,ItemContainerContents.EMPTY).copyInto(contents);for(int i=0;i<54;i++)super.setItem(i,contents.get(i));loading=false;}
 @Override public boolean canPlaceItem(int index,ItemStack stack){return stack.getItem() instanceof CardItem;}
 @Override public int getMaxStackSize(){return 1;}
 @Override public void setChanged(){super.setChanged();if(parent!=null&&!loading)parent.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(java.util.stream.IntStream.range(0,54).mapToObj(this::getItem).toList()));}
}
