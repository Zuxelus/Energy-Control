package com.zuxelus.energycontrol;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.network.ChannelHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ClientTickHandler {
	public final static ClientTickHandler instance = new ClientTickHandler();
	public static List<TileEntity> holo_panels = Lists.newArrayList();
	public static boolean altPressed;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			return;
		boolean alt = Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
		if (altPressed != alt) {
			altPressed = alt;
			if (Minecraft.getMinecraft().getNetHandler() != null)
				ChannelHandler.updateSeverKeys(alt);
		}
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (TileEntityRendererDispatcher.instance.field_147553_e == null)
			return;
		//TileEntityRendererDispatcher.instance.preDrawBatch();
		for (TileEntity te : holo_panels)
			TileEntityRendererDispatcher.instance.renderTileEntity(te, -1);
		//TileEntityRendererDispatcher.instance.drawBatch(0);
		holo_panels.clear();
	}
}
