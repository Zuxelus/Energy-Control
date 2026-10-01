// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.menu;
import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.port.core.UpgradePolicy;
import com.zuxelus.energycontrol.port.network.PortableDataPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import java.util.*;
public final class PortableMenu extends AbstractContainerMenu {
 public final PortableInventory contents;private final ItemStack parent;private final Player owner;private final InteractionHand hand;private final int lockedSlot;
 public List<String> lines=List.of();public List<Integer> barFills=List.of();private long lastUpdate=-1;
 public static PortableMenu fromNetwork(int id,Inventory inv,FriendlyByteBuf b){return new PortableMenu(id,inv,b.readEnum(InteractionHand.class));}
 public PortableMenu(int id,Inventory inv,InteractionHand hand){
  super(EnergyControlPort.PORTABLE_MENU.get(),id);owner=inv.player;this.hand=hand;lockedSlot=hand==InteractionHand.MAIN_HAND?inv.selected:40;parent=owner.getItemInHand(hand);contents=new PortableInventory(parent);
  for(int i=0;i<4;i++){final int target=i;addSlot(new Slot(contents,i,12+i*42,27){
   @Override public boolean mayPlace(ItemStack s){return target==0?s.getItem() instanceof CardItem:s.getItem() instanceof UpgradeItem u&&u.kind().ordinal()==target-1;}
   @Override public int getMaxStackSize(){return target==0?1:3;}
  });}
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addPlayerSlot(inv,col+row*9+9,8+col*18,158+row*18);
  for(int col=0;col<9;col++)addPlayerSlot(inv,col,8+col*18,216);
 }
 private void addPlayerSlot(Inventory inv,int index,int x,int y){addSlot(new Slot(inv,index,x,y){@Override public boolean mayPickup(Player p){return index!=lockedSlot;}@Override public boolean mayPlace(ItemStack s){return index!=lockedSlot;}});}
 public int upgrade(UpgradeItem.Kind kind){var s=contents.getItem(1+kind.ordinal());return s.getItem() instanceof UpgradeItem u&&u.kind()==kind?Math.min(3,s.getCount()):0;}
 @Override public boolean stillValid(Player p){return p==owner&&p.getItemInHand(hand)==parent&&parent.getItem() instanceof PortablePanelItem;}
 @Override public void clicked(int slot,int button,ClickType type,Player p){
  if(type==ClickType.SWAP && (button==lockedSlot))return;
  super.clicked(slot,button,type,p);
 }
 @Override public void broadcastChanges(){
  super.broadcastChanges();if(!(owner instanceof ServerPlayer sp)||!stillValid(owner))return;
  long now=owner.level().getGameTime();if(now==lastUpdate||now%5!=0)return;lastUpdate=now;
  var next=CardDisplay.rows(owner.level(),owner.blockPosition(),contents.getItem(0),UpgradePolicy.range(upgrade(UpgradeItem.Kind.RANGE)),UpgradePolicy.targets(upgrade(UpgradeItem.Kind.CAPACITY)),UpgradePolicy.decimals(upgrade(UpgradeItem.Kind.PRECISION))).stream().limit(32).toList();
  // Send periodically, including empty data, to initialize newly opened clients.
  lines=next.stream().map(com.zuxelus.energycontrol.port.core.DisplayRow::text).toList();barFills=next.stream().map(com.zuxelus.energycontrol.port.core.DisplayRow::fill).toList();NetworkManager.sendToPlayer(sp,new PortableDataPayload(containerId,lines,barFills));owner.getInventory().setChanged();
 }
 public void editText(Player p,int slot,String text){if(p.containerMenu!=this||!stillValid(p)||p.isSpectator()||slot!=0)return;CardDisplay.edit(contents.getItem(0),text);contents.setChanged();broadcastChanges();}
 @Override public boolean clickMenuButton(Player p,int id){
  if(!stillValid(p)||p.isSpectator())return false;
  if(id>=110&&id<115){boolean changed=CardItem.toggleInventoryField(contents.getItem(0),id-110);if(changed)contents.setChanged();return changed;}
  if(id<100||id>103)return false;
  var stack=contents.getItem(0);if(!(stack.getItem() instanceof CardItem))return false;
  var data=CardItem.data(stack);String key=id==100?"hideLabels":id==101?"hidePercent":id==102?"showEach":"showBars";data.putBoolean(key,!data.getBoolean(key));CardItem.update(stack,data);contents.setChanged();return true;
 }
 @Override public ItemStack quickMoveStack(Player p,int index){
  if(index<0||index>=slots.size()||!slots.get(index).mayPickup(p))return ItemStack.EMPTY;
  var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();
  if(index<4){if(!moveItemStackTo(stack,4,slots.size(),true))return ItemStack.EMPTY;}
  else if(stack.getItem() instanceof CardItem){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}
  else if(stack.getItem() instanceof UpgradeItem u){int t=1+u.kind().ordinal();if(!moveItemStackTo(stack,t,t+1,false))return ItemStack.EMPTY;}
  else return ItemStack.EMPTY;
  if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(p,stack);contents.setChanged();return copy;
 }
}
