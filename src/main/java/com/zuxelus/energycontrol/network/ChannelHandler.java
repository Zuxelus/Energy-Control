package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketTileEntityC2S;
import com.zuxelus.zlib.network.PacketTileEntityS2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ChannelHandler {

	public static void init() {
		PayloadTypeRegistry.serverboundPlay().register(PacketCardC2S.ID, PacketCardC2S.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(PacketTileEntityC2S.ID, PacketTileEntityC2S.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(PacketKeys.ID, PacketKeys.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(PacketPortableBars.ID, PacketPortableBars.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(PacketCardS2C.ID, PacketCardS2C.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(PacketTileEntityS2C.ID, PacketTileEntityS2C.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(PacketAlarm.ID, PacketAlarm.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(PacketCardC2S.ID, PacketCardC2S::handle);
		ServerPlayNetworking.registerGlobalReceiver(PacketTileEntityC2S.ID, PacketTileEntityC2S::handle);
		ServerPlayNetworking.registerGlobalReceiver(PacketKeys.ID, PacketKeys::handle);
		ServerPlayNetworking.registerGlobalReceiver(PacketPortableBars.ID, PacketPortableBars::handle);
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ClientPlayNetworking.registerGlobalReceiver(PacketCardS2C.ID, PacketCardS2C::handleClient);
		ClientPlayNetworking.registerGlobalReceiver(PacketTileEntityS2C.ID, PacketTileEntityS2C::handleClient);
		ClientPlayNetworking.registerGlobalReceiver(PacketAlarm.ID, PacketAlarm::handleClient);
	}

	// server
	public static void updateClientCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card.isEmpty() || panel == null || slot < 0)
			return;

		Level world = panel.getLevel();
		if (world == null || world.isClientSide())
			return;

		NetworkHelper.sendPacketToAllAround((ServerLevel) world, new PacketCardS2C(card, panel.getBlockPos(), slot));
	}

	// client
	public static void updateServerCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card.isEmpty() || panel == null || slot < 0)
			return;

		Level world = panel.getLevel();
		if (world == null || !world.isClientSide())
			return;

		NetworkHelper.sendToServer(new PacketCardC2S(card, panel.getBlockPos(), slot));
	}

	public static void updateSeverKeys(boolean altPressed) {
		ClientPlayNetworking.send(new PacketKeys(altPressed));
	}
}
