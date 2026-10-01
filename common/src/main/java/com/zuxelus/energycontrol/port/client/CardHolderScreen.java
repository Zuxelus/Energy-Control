// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.client;
import com.zuxelus.energycontrol.port.menu.CardHolderMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
public final class CardHolderScreen extends AbstractContainerScreen<CardHolderMenu> {
 public CardHolderScreen(CardHolderMenu menu,Inventory inv,Component title){super(menu,inv,title);imageWidth=176;imageHeight=222;inventoryLabelY=128;}
 @Override protected void renderBg(GuiGraphics g,float tick,int x,int y){g.fill(leftPos,topPos,leftPos+imageWidth,topPos+imageHeight,0xffc6c6c6);for(var s:menu.slots){g.fill(leftPos+s.x-1,topPos+s.y-1,leftPos+s.x+17,topPos+s.y+17,0xff555555);g.fill(leftPos+s.x,topPos+s.y,leftPos+s.x+16,topPos+s.y+16,0xff8b8b8b);}}
 @Override public void render(GuiGraphics g,int x,int y,float tick){super.render(g,x,y,tick);renderTooltip(g,x,y);}
}
