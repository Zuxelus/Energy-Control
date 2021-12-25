package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketTileEntityC2S;
import com.zuxelus.zlib.network.PacketTileEntityS2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ChannelHandler {

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(PacketCardC2S.ID, PacketCardC2S::handle);
		ServerPlayNetworking.registerGlobalReceiver(PacketTileEntityC2S.ID, PacketTileEntityC2S::handle);
		ServerPlayNetworking.registerGlobalReceiver(PacketKeys.ID, PacketKeys::handle);
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

		World world = panel.getWorld();
		if (world == null || world.isClient)
			return;

		NetworkHelper.sendPacketToAllAround((ServerWorld) world, new PacketCardS2C(card, panel.getPos(), slot));
	}

	// client
	public static void updateServerCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card.isEmpty() || panel == null || slot < 0)
			return;

		World world = panel.getWorld();
		if (world == null || !world.isClient)
			return;

		NetworkHelper.sendToServer(new PacketCardC2S(card, panel.getPos(), slot));
	}

	public static void updateSeverKeys(boolean altPressed) {
		ClientPlayNetworking.send(PacketKeys.ID, new PacketKeys(altPressed));
	}
}
