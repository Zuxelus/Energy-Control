// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.menu;
import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.card.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class CardHolderMenu extends AbstractContainerMenu {
 public final CardHolderInventory contents;private final ItemStack parent;private final Player owner;private final InteractionHand hand;private final int locked;
 public static CardHolderMenu fromNetwork(int id,Inventory inv,FriendlyByteBuf b){return new CardHolderMenu(id,inv,b.readEnum(InteractionHand.class));}
 public CardHolderMenu(int id,Inventory inv,InteractionHand hand){super(EnergyControlPort.HOLDER_MENU.get(),id);this.hand=hand;owner=inv.player;parent=owner.getItemInHand(hand);locked=hand==InteractionHand.MAIN_HAND?inv.selected:40;contents=new CardHolderInventory(parent);
  for(int r=0;r<6;r++)for(int c=0;c<9;c++)addSlot(new Slot(contents,r*9+c,8+c*18,18+r*18){@Override public boolean mayPlace(ItemStack s){return s.getItem() instanceof CardItem;}@Override public int getMaxStackSize(){return 1;}});
  for(int r=0;r<3;r++)for(int c=0;c<9;c++)playerSlot(inv,c+r*9+9,8+c*18,140+r*18);for(int c=0;c<9;c++)playerSlot(inv,c,8+c*18,198);
 }
 private void playerSlot(Inventory inv,int i,int x,int y){addSlot(new Slot(inv,i,x,y){@Override public boolean mayPickup(Player p){return i!=locked;}@Override public boolean mayPlace(ItemStack s){return i!=locked;}});}
 @Override public boolean stillValid(Player p){return p==owner&&p.getItemInHand(hand)==parent&&parent.getItem() instanceof CardHolderItem;}
 @Override public void clicked(int slot,int button,ClickType type,Player p){if(type==ClickType.SWAP&&button==locked)return;super.clicked(slot,button,type,p);}
 @Override public void broadcastChanges(){super.broadcastChanges();if(!owner.level().isClientSide)owner.getInventory().setChanged();}
 @Override public ItemStack quickMoveStack(Player p,int index){if(index<0||index>=slots.size()||!slots.get(index).mayPickup(p))return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<54){if(!moveItemStackTo(stack,54,slots.size(),true))return ItemStack.EMPTY;}else if(!(stack.getItem() instanceof CardItem)||!moveItemStackTo(stack,0,54,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(p,stack);contents.setChanged();return copy;}
}
