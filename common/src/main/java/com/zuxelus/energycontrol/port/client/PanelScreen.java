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
    private boolean layout,fieldsMode;private int group;
    private Button fieldsToggle;private final java.util.List<Button> fieldButtons=new java.util.ArrayList<>();
    private final java.util.List<Button> styleButtons=new java.util.ArrayList<>(),layoutButtons=new java.util.ArrayList<>();
    private Button slopeHButton,slopeVButton,resetSlopeButton;
    private Button thicknessButton,thickerButton,resetCaseButton,resetProjectionButton;
    private Button layoutToggle,nextPageButton,pageSizeButton,autoPageButton,pitchButton,yawButton,depthButton;
    private Button slotButton,powerButton,scaleButton,alignButton,fontButton,rateButton;
    public PanelScreen(PanelMenu menu,Inventory inventory,Component title) {
        super(menu,inventory,title);imageWidth=330;imageHeight=242;inventoryLabelY=139;
    }
    private Button button(String label,int x,int y,int w,Runnable action) {
        var b=addRenderableWidget(Button.builder(Component.literal(label),ignored->action.run()).bounds(leftPos+x,topPos+y,w,20).build());
        if(group==3)fieldButtons.add(b);if(group==1)styleButtons.add(b);if(group==2)layoutButtons.add(b);return b;
    }
    @Override protected void init() {
        super.init();styleButtons.clear();layoutButtons.clear();fieldButtons.clear();group=0;
        text=new MultiLineEditBox(font,leftPos+8,topPos+51,160,45,Component.literal("Ten lines; @a color, @@ literal @"),Component.translatable("gui.energycontrol.text"));
        text.setCharacterLimit(512);text.setValueListener(v->{if(!loading)dirty=true;});addRenderableWidget(text);loadText();
        button("Save text",180,25,94,()->{NetworkManager.sendToServer(new PanelEditPayload(menu.containerId,selectedSlot,text.getValue()));dirty=false;loadedText=text.getValue();});
        fieldsToggle=button("Fields",278,25,42,()->{fieldsMode=!fieldsMode;layout=false;updateGroups();});
        slotButton=button("Card",180,48,94,()->{selectedSlot=(selectedSlot+1)%menu.cardSlots;loadText();});
        layoutToggle=button("Layout",278,48,42,()->{layout=!layout;fieldsMode=false;updateGroups();});group=1;
        button("Text color",180,71,68,()->send(0));button("Background",252,71,68,()->send(6));
        powerButton=button("Power",180,94,140,()->send(1));scaleButton=button("Scale",180,117,140,()->send(2));
        alignButton=button("Align",180,140,140,()->send(3));fontButton=button("Font",180,163,140,()->send(4));rateButton=button("Refresh",180,186,140,()->send(5));
        button("Label",180,209,33,()->send(100+selectedSlot*2));button("Pct",216,209,33,()->send(101+selectedSlot*2));button("Each",252,209,33,()->send(200+selectedSlot));button("Bars",288,209,32,()->send(300+selectedSlot));
        group=2;button("Prev",180,71,68,()->send(8));nextPageButton=button("Next",252,71,68,()->send(7));
        pageSizeButton=button("Lines",180,94,140,()->send(9));autoPageButton=button("Auto",180,117,140,()->send(10));
        pitchButton=button("Pitch",180,140,140,()->send(11));yawButton=button("Yaw",180,163,140,()->send(12));depthButton=button("Depth",180,186,140,()->send(13));
        resetProjectionButton=button("Reset projection",180,209,140,()->send(14));
        thicknessButton=button("Thickness",180,140,140,()->send(15));thickerButton=button("Thicker +1",180,163,68,()->send(17));resetCaseButton=button("Full thickness",252,163,68,()->send(16));
        slopeHButton=button("Slope H",180,186,68,()->send(18));slopeVButton=button("Slope V",252,186,68,()->send(19));resetSlopeButton=button("Reset slopes",180,209,140,()->send(20));
        slopeHButton.active=slopeVButton.active=resetSlopeButton.active=thicknessButton.active=thickerButton.active=resetCaseButton.active=menu.panel.advanced();group=3;
        for(int k=0;k<5;k++){final int field=k;button("Field",180,71+k*28,140,()->send(400+selectedSlot*5+field));}group=0;updateGroups();
    }
    private void updateGroups(){for(var b:styleButtons)b.visible=!layout&&!fieldsMode;for(var b:layoutButtons)b.visible=layout&&!fieldsMode;for(var b:fieldButtons)b.visible=fieldsMode;layoutToggle.setMessage(Component.literal(layout?"Style":"Layout"));
        boolean holo=menu.panel.holographic();pitchButton.visible=yawButton.visible=depthButton.visible=resetProjectionButton.visible=layout&&holo;
        slopeHButton.visible=slopeVButton.visible=resetSlopeButton.visible=thicknessButton.visible=thickerButton.visible=resetCaseButton.visible=layout&&!holo;
    }
    private void send(int id){minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id);}
    private String cardText(){var stack=menu.getSlot(selectedSlot).getItem();return CardItem.data(stack).getString(stack.getItem() instanceof CardItem c && c.kind()==CardItem.Kind.TEXT?"text":"title");}
    private void loadText(){loading=true;loadedText=cardText();text.setValue(loadedText);loading=false;dirty=false;}
    @Override protected void containerTick() {
        super.containerTick();
        // Initial slot data can arrive after opening the menu. Preserve unsaved typing.
        String server=cardText();
        if(!dirty && !server.equals(loadedText))loadText();
        var selected=menu.getSlot(selectedSlot).getItem();fieldsToggle.active=selected.getItem() instanceof CardItem c&&c.kind()==CardItem.Kind.INVENTORY;if(!fieldsToggle.active)fieldsMode=false;updateGroups();
        int mask=com.zuxelus.energycontrol.port.inventory.InventorySnapshot.fields(CardItem.data(selected));for(int k=0;k<5;k++)fieldButtons.get(k).setMessage(Component.literal(new String[]{"Name","Total items","Slots used","Sided","Item details"}[k]+": "+((mask&(1<<k))!=0?"On":"Off")));
        var p=menu.panel;
        slotButton.setMessage(Component.literal("Card "+(selectedSlot+1)+" / "+menu.cardSlots));
        powerButton.setMessage(Component.literal("Power: "+new String[]{"Redstone","Inverted","Always on","Off"}[p.powerMode()]));
        scaleButton.setMessage(Component.literal("Scale: "+p.scalePercent()+"%"));
        alignButton.setMessage(Component.literal("Align: "+new String[]{"Left","Center","Right"}[p.alignment()]));
        fontButton.setMessage(Component.literal("Font: "+(p.uniformFont()?"Uniform":"Default")));
        rateButton.setMessage(Component.literal("Refresh: "+p.refreshTicks()+" ticks"));
        nextPageButton.setMessage(Component.literal("Next "+(p.page()+1)+"/"+p.pageCount()));
        pageSizeButton.setMessage(Component.literal("Lines per page: "+p.pageSize()));autoPageButton.setMessage(Component.literal(p.pageTicks()==0?"Auto page: Off":"Auto page: "+p.pageTicks()+" ticks"));
        pitchButton.setMessage(Component.literal("Pitch: "+p.projection().pitch()+" deg"));yawButton.setMessage(Component.literal("Yaw: "+p.projection().yaw()+" deg"));depthButton.setMessage(Component.literal("Depth: "+p.projection().depth()+" / 16"));
        pitchButton.active=yawButton.active=depthButton.active=p.holographic();
        slopeHButton.setMessage(Component.literal("Slope H: "+p.slopeHorizontal()*7));slopeVButton.setMessage(Component.literal("Slope V: "+p.slopeVertical()*7));
        thicknessButton.setMessage(Component.literal("Thickness -1: "+p.thickness()+" / 16"));thicknessButton.active=p.advanced()&&p.thickness()>1;thickerButton.active=p.advanced()&&p.thickness()<16;
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
