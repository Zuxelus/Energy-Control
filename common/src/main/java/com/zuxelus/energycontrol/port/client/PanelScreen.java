// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.client;

import com.zuxelus.energycontrol.port.card.CardItem;
import com.zuxelus.energycontrol.port.menu.PanelMenu;
import com.zuxelus.energycontrol.port.network.PanelEditPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class PanelScreen extends AbstractContainerScreen<PanelMenu> {
    private EditBox text;
    private int selectedSlot;
    public PanelScreen(PanelMenu menu, Inventory inventory, Component title) {
        super(menu,inventory,title); imageWidth=176; imageHeight=194; inventoryLabelY=100;
    }
    @Override protected void init() {
        super.init();
        text = new EditBox(font,leftPos+8,topPos+51,160,18,Component.translatable("gui.energycontrol.text"));
        text.setMaxLength(512); addRenderableWidget(text); loadText();
        addRenderableWidget(Button.builder(Component.translatable("gui.energycontrol.save"),b ->
                NetworkManager.sendToServer(new PanelEditPayload(menu.containerId,selectedSlot,text.getValue())))
                .bounds(leftPos+8,topPos+73,42,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.energycontrol.slot"),b -> {
            selectedSlot=(selectedSlot+1)%menu.cardSlots; loadText();
        }).bounds(leftPos+52,topPos+73,36,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.energycontrol.color"),b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0))
                .bounds(leftPos+90,topPos+73,38,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.energycontrol.power"),b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId,1))
                .bounds(leftPos+130,topPos+73,38,20).build());
    }
    private void loadText() { text.setValue(CardItem.data(menu.getSlot(selectedSlot).getItem()).getString("text")); }
    @Override protected void renderBg(GuiGraphics g, float tick, int mouseX, int mouseY) {
        g.fill(leftPos,topPos,leftPos+imageWidth,topPos+imageHeight,0xffc6c6c6);
        g.fill(leftPos+6,topPos+22,leftPos+170,topPos+48,0xff282c30);
        for(var slot : menu.slots) {
            g.fill(leftPos+slot.x-1,topPos+slot.y-1,leftPos+slot.x+17,topPos+slot.y+17,0xff555555);
            g.fill(leftPos+slot.x,topPos+slot.y,leftPos+slot.x+16,topPos+slot.y+16,0xff8b8b8b);
        }
        int x=leftPos+16+selectedSlot*18;
        g.renderOutline(x-2,topPos+27,20,20,0xffffff55);
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float tick) {
        super.render(g,mouseX,mouseY,tick); renderTooltip(g,mouseX,mouseY);
    }
    @Override public boolean keyPressed(int key,int scan,int modifiers) {
        if(text.isFocused() && key != 256) return text.keyPressed(key,scan,modifiers) || text.canConsumeInput();
        return super.keyPressed(key,scan,modifiers);
    }
}
