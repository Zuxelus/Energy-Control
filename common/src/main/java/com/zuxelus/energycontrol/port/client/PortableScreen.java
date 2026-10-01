// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.client;
import com.zuxelus.energycontrol.port.card.*;
import com.zuxelus.energycontrol.port.menu.PortableMenu;
import com.zuxelus.energycontrol.port.network.PanelEditPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
public final class PortableScreen extends AbstractContainerScreen<PortableMenu> {
 private MultiLineEditBox editor;private boolean dirty,loading;private String loaded="";
 public PortableScreen(PortableMenu m,Inventory i,Component t){super(m,i,t);imageWidth=340;imageHeight=246;inventoryLabelY=146;}
 @Override protected void init(){
  super.init();editor=new MultiLineEditBox(font,leftPos+8,topPos+51,162,67,Component.literal("Card text / sensor title"),Component.literal("Card text"));editor.setCharacterLimit(512);editor.setValueListener(v->{if(!loading)dirty=true;});addRenderableWidget(editor);
  addRenderableWidget(Button.builder(Component.literal("Save text"),b->{NetworkManager.sendToServer(new PanelEditPayload(menu.containerId,0,editor.getValue()));dirty=false;loaded=editor.getValue();}).bounds(leftPos+8,topPos+121,78,20).build());
  for(int k=0;k<3;k++){final int id=100+k;addRenderableWidget(Button.builder(Component.literal(new String[]{"Labels","Percent","Each"}[k]),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id)).bounds(leftPos+180+k*50,topPos+218,48,20).build());}
 }
 @Override protected void containerTick(){super.containerTick();var s=menu.contents.getItem(0);String value=CardItem.data(s).getString(s.getItem() instanceof CardItem c&&c.kind()==CardItem.Kind.TEXT?"text":"title");if(!dirty&&!value.equals(loaded)){loaded=value;loading=true;editor.setValue(value);loading=false;}}
 @Override protected void renderBg(GuiGraphics g,float tick,int x,int y){
  g.fill(leftPos,topPos,leftPos+imageWidth,topPos+imageHeight,0xffc6c6c6);g.fill(leftPos+177,topPos+24,leftPos+334,topPos+213,0xff080b0c);
  for(var slot:menu.slots){g.fill(leftPos+slot.x-1,topPos+slot.y-1,leftPos+slot.x+17,topPos+slot.y+17,0xff555555);g.fill(leftPos+slot.x,topPos+slot.y,leftPos+slot.x+16,topPos+slot.y+16,0xff8b8b8b);}
  g.drawString(font,"Card   Range   Cap.   Prec.",leftPos+8,topPos+17,0xff333333,false);
  int maxWidth=1;for(var value:menu.lines)maxWidth=Math.max(maxWidth,font.width(value));
  float fit=Math.min(.75f,Math.min(147f/maxWidth,178f/Math.max(10,menu.lines.size()*10)));
  g.pose().pushPose();g.pose().translate(leftPos+182,topPos+30,0);g.pose().scale(fit,fit,1);
  int line=0;for(var value:menu.lines)g.drawString(font,value,0,line++*10,0xff55ff55,false);g.pose().popPose();
 }
 @Override public void render(GuiGraphics g,int x,int y,float tick){super.render(g,x,y,tick);renderTooltip(g,x,y);}
 @Override public boolean keyPressed(int key,int scan,int mods){if(editor.isFocused()&&key!=256){editor.keyPressed(key,scan,mods);return true;}return super.keyPressed(key,scan,mods);}
}
