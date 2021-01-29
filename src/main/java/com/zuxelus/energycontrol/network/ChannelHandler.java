package com.zuxelus.energycontrol.network;

import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.NetworkHelper;
import com.zuxelus.zlib.network.PacketTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ChannelHandler {

	public static void init() {
		NetworkHelper.createChannel(EnergyControl.MODID);
		NetworkHelper.registerServerToClient(PacketAlarm.class, PacketAlarm.class, 1);
		NetworkHelper.registerServerToClient(PacketCard.class, PacketCard.class, 2);
		NetworkHelper.registerClientToServer(PacketCard.class, PacketCard.class, 3);
		NetworkHelper.registerServerToClient(PacketTileEntity.class, PacketTileEntity.class, 5);
		NetworkHelper.registerClientToServer(PacketTileEntity.class, PacketTileEntity.class, 6);
		NetworkHelper.registerServerToClient(PacketOreHelper.class, PacketOreHelper.class, 8);
	}

	// server
	public static void updateClientCard(ItemStack card, TileEntity panel, int slot, Map<String, Object> fields) {
		if (fields == null || fields.isEmpty() || panel == null || !(panel instanceof TileEntityInfoPanel) || slot == -1)
			return;

		if (panel.getWorld().isRemote)
			return;

		NetworkHelper.sendPacketToAllAround(panel.getPos(), 64, panel.getWorld(), new PacketCard(panel.getPos(), slot, card.getItem().getClass().getName(), fields));
	}

	// client
	public static void updateServerCard(ItemStack card, TileEntityInfoPanel panel, Map<String, Object> fields, int slot) {
		if (card.isEmpty() || fields == null || fields.isEmpty() || panel == null)
			return;

		if (!panel.getWorld().isRemote)
			return;

		NetworkHelper.network.sendToServer(new PacketCard(panel.getPos(), slot, card.getItem().getClass().getName(), fields));
	}
}