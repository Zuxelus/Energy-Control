package com.zuxelus.energycontrol;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.network.ChannelHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientTickHandler {
	public final static ClientTickHandler instance = new ClientTickHandler();
	public static List<TileEntity> holo_panels = Lists.newArrayList();
	public static boolean altPressed;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			return;
		boolean alt = GuiScreen.isAltKeyDown();
		if (altPressed != alt) {
			altPressed = alt;
			if (Minecraft.getMinecraft().getConnection() != null)
				ChannelHandler.updateSeverKeys(alt);
		}
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (TileEntityRendererDispatcher.instance.renderEngine == null)
			return;
		TileEntityRendererDispatcher.instance.preDrawBatch();
		for (TileEntity te : holo_panels)
			TileEntityRendererDispatcher.instance.renderTileEntity(te, -1, -1);
		TileEntityRendererDispatcher.instance.drawBatch(0);
		holo_panels.clear();
	}
}
