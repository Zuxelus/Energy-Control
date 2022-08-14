package com.zuxelus.energycontrol;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.network.ChannelHandler;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnergyControl.MODID, value = Dist.CLIENT)
public class ClientTickHandler {
	public final static ClientTickHandler instance = new ClientTickHandler();
	public static List<BlockEntity> holo_panels = Lists.newArrayList();
	public static boolean altPressed;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			return;
		boolean alt = Screen.hasAltDown();
		if (altPressed != alt) {
			altPressed = alt;
			if (Minecraft.getInstance().getConnection() != null)
				ChannelHandler.updateSeverKeys(alt);
		}
	}

	@SubscribeEvent
	public static void render(RenderLevelLastEvent event) {
		Minecraft client = Minecraft.getInstance();
		BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();
		Camera info = client.gameRenderer.getMainCamera();
		info.setup(client.level, client.getCameraEntity() == null ? client.player : client.getCameraEntity(),
				!client.options.getCameraType().isFirstPerson(), client.options.getCameraType().isMirrored(),
				event.getPartialTick());
		ComputeCameraAngles cameraSetup = ForgeHooksClient.onCameraSetup(client.gameRenderer, info, event.getPartialTick());
		info.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
		PoseStack matrixStack = event.getPoseStack();
		Vec3 vector3d = info.getPosition();
		double d0 = vector3d.x();
		double d1 = vector3d.y();
		double d2 = vector3d.z();

		MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		for (BlockEntity te : holo_panels) {
			matrixStack.pushPose();
			BlockPos pos = te.getBlockPos();
			matrixStack.translate(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2);
			dispatcher.render(te, -1, matrixStack, buffers);
			matrixStack.popPose();
		}
		holo_panels.clear();
	}
}
