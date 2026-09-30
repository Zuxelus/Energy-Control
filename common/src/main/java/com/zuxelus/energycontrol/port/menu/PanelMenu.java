// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.menu;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.block.PanelBlockEntity;
import com.zuxelus.energycontrol.port.card.CardItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class PanelMenu extends AbstractContainerMenu {
    public final PanelBlockEntity panel;
    public final int cardSlots;
    public static PanelMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf buffer) {
        var be = inventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (!(be instanceof PanelBlockEntity panel)) throw new IllegalStateException("Panel menu missing its block entity");
        return new PanelMenu(id,inventory,panel);
    }
    public PanelMenu(int id, Inventory inventory, PanelBlockEntity panel) {
        super(EnergyControlPort.PANEL_MENU.get(),id);
        this.panel = panel; cardSlots = panel.getContainerSize();
        for(int i=0;i<cardSlots;i++) addSlot(new Slot(panel,i,16+i*18,29) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof CardItem; }
            @Override public int getMaxStackSize() { return 1; }
        });
        for(int row=0;row<3;row++) for(int col=0;col<9;col++) addSlot(new Slot(inventory,col+row*9+9,8+col*18,112+row*18));
        for(int col=0;col<9;col++) addSlot(new Slot(inventory,col,8+col*18,170));
    }
    @Override public boolean stillValid(Player player) { return !panel.isRemoved() && panel.stillValid(player); }
    @Override public boolean clickMenuButton(Player player, int id) {
        if (!stillValid(player) || player.isSpectator()) return false;
        if(id==0) panel.cycleColor(); else if(id==1) panel.togglePower(); else return false;
        return true;
    }
    public void editText(Player player, int slot, String text) {
        if(player.containerMenu != this || player.isSpectator() || !stillValid(player)) return;
        panel.setText(slot,text); broadcastChanges();
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if(index<0 || index>=slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index); if(!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), copy = stack.copy();
        if(index<cardSlots) { if(!moveItemStackTo(stack,cardSlots,slots.size(),true)) return ItemStack.EMPTY; }
        else if(!(stack.getItem() instanceof CardItem) || !moveItemStackTo(stack,0,cardSlots,false)) return ItemStack.EMPTY;
        if(stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player,stack); return copy;
    }
}
