// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.menu;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
public final class PortableInventory extends SimpleContainer {
 private final ItemStack parent;private boolean loading=true;
 public PortableInventory(ItemStack parent){
  super(4);this.parent=parent;var contents=NonNullList.withSize(4,ItemStack.EMPTY);
  parent.getOrDefault(DataComponents.CONTAINER,ItemContainerContents.EMPTY).copyInto(contents);
  for(int i=0;i<4;i++)super.setItem(i,contents.get(i));loading=false;
 }
 @Override public void setChanged(){
  super.setChanged();
  if(parent!=null&&!loading)parent.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(java.util.stream.IntStream.range(0,4).mapToObj(this::getItem).toList()));
 }
}
