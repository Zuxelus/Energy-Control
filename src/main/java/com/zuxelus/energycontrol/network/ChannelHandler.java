package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ChannelHandler {

	public static void init() {
		NetworkHelper.createChannel(EnergyControl.MODID, EnergyControl.VERSION);
		NetworkHelper.registerBoth(1, PacketCard.class, PacketCard::encode, PacketCard::decode, PacketCard::handle);
		NetworkHelper.registerBoth(2, PacketTileEntity.class, PacketTileEntity::encode, PacketTileEntity::decode, PacketTileEntity::handle);
		NetworkHelper.registerServerToClient(3, PacketAlarm.class, PacketAlarm::encode, PacketAlarm::decode, PacketAlarm::handle);
		NetworkHelper.registerClientToServer(4, PacketKeys.class, PacketKeys::encode, PacketKeys::decode, PacketKeys::handle);
	}

	// server
	public static void updateClientCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card.isEmpty() || panel == null || slot < 0)
			return;

		World world = panel.getWorld();
		if (world == null || world.isRemote)
			return;

		NetworkHelper.sendPacketToAllAround(world, panel.getPos(), new PacketCard(card, panel.getPos(), slot));
	}

	// client
	public static void updateServerCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card.isEmpty() || panel == null || slot < 0)
			return;

		World world = panel.getWorld();
		if (world == null || !world.isRemote)
			return;

		NetworkHelper.network.sendToServer(new PacketCard(card, panel.getPos(), slot));
	}

	public static void updateSeverKeys(boolean altPressed) {
		NetworkHelper.network.sendToServer(new PacketKeys(altPressed));
	}
}
