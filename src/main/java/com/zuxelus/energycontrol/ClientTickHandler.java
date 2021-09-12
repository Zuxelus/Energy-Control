package com.zuxelus.energycontrol;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.network.ChannelHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnergyControl.MODID, value = Dist.CLIENT)
public class ClientTickHandler {
	public final static ClientTickHandler instance = new ClientTickHandler();
	public static List<TileEntity> holo_panels = Lists.newArrayList();
	public static boolean altPressed;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			return;
		boolean alt = Screen.hasAltDown();
		if (altPressed != alt) {
			altPressed = alt;
			ChannelHandler.updateSeverKeys(alt);
		}
	}

	@SubscribeEvent
	public static void render(RenderWorldLastEvent event) {
		Minecraft client = Minecraft.getInstance();
		ActiveRenderInfo info = client.gameRenderer.getMainCamera();
		info.setup(client.level, client.getCameraEntity() == null ? client.player : client.getCameraEntity(),
				!client.options.getCameraType().isFirstPerson(), client.options.getCameraType().isMirrored(),
				event.getPartialTicks());
		EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(client.gameRenderer, info, event.getPartialTicks());
		info.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
		MatrixStack matrixStack = event.getMatrixStack();
		Vector3d vector3d = info.getPosition();
		double d0 = vector3d.x();
		double d1 = vector3d.y();
		double d2 = vector3d.z();

		IRenderTypeBuffer buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		for (TileEntity te : holo_panels) {
			matrixStack.pushPose();
			BlockPos pos = te.getBlockPos();
			matrixStack.translate(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2);
			TileEntityRendererDispatcher.instance.render(te, -1, matrixStack, buffers);
			matrixStack.popPose();
		}
		holo_panels.clear();
	}
}
