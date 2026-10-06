package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketTileEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ChannelHandler {

	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		registrar.playBidirectional(PacketCard.TYPE, PacketCard.STREAM_CODEC, PacketCard::handleServer, null);
		registrar.playBidirectional(PacketTileEntity.TYPE, PacketTileEntity.STREAM_CODEC, PacketTileEntity::handleServer, null);
		registrar.playToClient(PacketAlarm.TYPE, PacketAlarm.STREAM_CODEC);
		registrar.playToServer(PacketKeys.TYPE, PacketKeys.STREAM_CODEC, PacketKeys::handle);
		registrar.playToServer(PacketPortableBars.TYPE, PacketPortableBars.STREAM_CODEC, PacketPortableBars::handle);
	}

	// server
	public static void updateClientCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card.isEmpty() || panel == null || slot < 0)
			return;

		Level world = panel.getLevel();
		if (world == null || world.isClientSide())
			return;

		NetworkHelper.sendPacketToAllAround(panel.getLevel(), panel.getBlockPos(), new PacketCard(card, panel.getBlockPos(), slot));
	}

	// client
	public static void updateServerCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card.isEmpty() || panel == null || slot < 0)
			return;

		Level world = panel.getLevel();
		if (world == null || !world.isClientSide())
			return;

		NetworkHelper.sendToServer(new PacketCard(card, panel.getBlockPos(), slot));
	}

	public static void updateSeverKeys(boolean altPressed) {
		NetworkHelper.sendToServer(new PacketKeys(altPressed));
	}
}
