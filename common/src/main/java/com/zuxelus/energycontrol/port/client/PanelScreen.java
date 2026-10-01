// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.client;
import com.zuxelus.energycontrol.port.card.CardItem;
import com.zuxelus.energycontrol.port.menu.PanelMenu;
import com.zuxelus.energycontrol.port.network.PanelEditPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class PanelScreen extends AbstractContainerScreen<PanelMenu> {
    private MultiLineEditBox text;
    private int selectedSlot;
    private String loadedText="";
    private boolean dirty,loading;
    private Button slotButton,powerButton,scaleButton,alignButton,fontButton,rateButton;
    public PanelScreen(PanelMenu menu,Inventory inventory,Component title) {
        super(menu,inventory,title);imageWidth=330;imageHeight=242;inventoryLabelY=139;
    }
    private Button button(String label,int x,int y,int w,Runnable action) {
        return addRenderableWidget(Button.builder(Component.literal(label),b->action.run()).bounds(leftPos+x,topPos+y,w,20).build());
    }
    @Override protected void init() {
        super.init();
        text=new MultiLineEditBox(font,leftPos+8,topPos+51,160,45,Component.literal("Ten lines; @a color, @@ literal @"),Component.translatable("gui.energycontrol.text"));
        text.setCharacterLimit(512);text.setValueListener(v->{if(!loading)dirty=true;});addRenderableWidget(text);loadText();
        button("Save text",180,25,140,()->{NetworkManager.sendToServer(new PanelEditPayload(menu.containerId,selectedSlot,text.getValue()));dirty=false;loadedText=text.getValue();});
        slotButton=button("Card",180,48,140,()->{selectedSlot=(selectedSlot+1)%menu.cardSlots;loadText();});
        button("Text color",180,71,68,()->send(0));button("Background",252,71,68,()->send(6));
        powerButton=button("Power",180,94,140,()->send(1));scaleButton=button("Scale",180,117,140,()->send(2));
        alignButton=button("Align",180,140,140,()->send(3));fontButton=button("Font",180,163,140,()->send(4));rateButton=button("Refresh",180,186,140,()->send(5));
        button("Labels",180,209,44,()->send(100+selectedSlot*2));button("Percent",228,209,44,()->send(101+selectedSlot*2));button("Each",276,209,44,()->send(200+selectedSlot));
    }
    private void send(int id){minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id);}
    private String cardText(){var stack=menu.getSlot(selectedSlot).getItem();return CardItem.data(stack).getString(stack.getItem() instanceof CardItem c && c.kind()==CardItem.Kind.TEXT?"text":"title");}
    private void loadText(){loading=true;loadedText=cardText();text.setValue(loadedText);loading=false;dirty=false;}
    @Override protected void containerTick() {
        super.containerTick();
        // Initial slot data can arrive after opening the menu. Preserve unsaved typing.
        String server=cardText();
        if(!dirty && !server.equals(loadedText))loadText();
        var p=menu.panel;
        slotButton.setMessage(Component.literal("Card "+(selectedSlot+1)+" / "+menu.cardSlots));
        powerButton.setMessage(Component.literal("Power: "+new String[]{"Redstone","Inverted","Always on","Off"}[p.powerMode()]));
        scaleButton.setMessage(Component.literal("Scale: "+p.scalePercent()+"%"));
        alignButton.setMessage(Component.literal("Align: "+new String[]{"Left","Center","Right"}[p.alignment()]));
        fontButton.setMessage(Component.literal("Font: "+(p.uniformFont()?"Uniform":"Default")));
        rateButton.setMessage(Component.literal("Refresh: "+p.refreshTicks()+" ticks"));
    }
    @Override protected void renderBg(GuiGraphics g,float tick,int mx,int my) {
        g.fill(leftPos,topPos,leftPos+imageWidth,topPos+imageHeight,0xffc6c6c6);g.fill(leftPos+6,topPos+22,leftPos+170,topPos+48,0xff282c30);
        for(var slot:menu.slots){g.fill(leftPos+slot.x-1,topPos+slot.y-1,leftPos+slot.x+17,topPos+slot.y+17,0xff555555);g.fill(leftPos+slot.x,topPos+slot.y,leftPos+slot.x+16,topPos+slot.y+16,0xff8b8b8b);}
        g.drawString(font,"Range",leftPos+16,topPos+111,0xff333333,false);g.drawString(font,"Capacity",leftPos+70,topPos+111,0xff333333,false);g.drawString(font,"Precision",leftPos+124,topPos+111,0xff333333,false);
        g.renderOutline(leftPos+14+selectedSlot*18,topPos+27,20,20,0xffffff55);
    }
    @Override public void render(GuiGraphics g,int mx,int my,float tick){super.render(g,mx,my,tick);renderTooltip(g,mx,my);}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(text.isFocused()&&key!=256){text.keyPressed(key,scan,modifiers);return true;}return super.keyPressed(key,scan,modifiers);}
}
