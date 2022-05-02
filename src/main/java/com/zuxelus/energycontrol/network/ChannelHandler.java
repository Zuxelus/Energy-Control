package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ChannelHandler {

	public static void init() {
		NetworkHelper.createChannel(EnergyControl.MODID);
		NetworkHelper.registerServerToClient(PacketAlarm.class, PacketAlarm.class, 1);
		NetworkHelper.registerServerToClient(PacketCard.class, PacketCard.class, 2);
		NetworkHelper.registerClientToServer(PacketCard.class, PacketCard.class, 3);
		NetworkHelper.registerClientToServer(PacketKeys.class, PacketKeys.class, 4);
		NetworkHelper.registerServerToClient(PacketTileEntity.class, PacketTileEntity.class, 5);
		NetworkHelper.registerClientToServer(PacketTileEntity.class, PacketTileEntity.class, 6);
	}

	// server
	public static void updateClientCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card == null || panel == null || slot < 0)
			return;

		World world = panel.getWorldObj();
		if (world == null || world.isRemote)
			return;

		NetworkHelper.sendPacketToAllAround(world, panel.xCoord, panel.yCoord, panel.zCoord, 64, new PacketCard(card, panel.xCoord, panel.yCoord, panel.zCoord, slot));
	}

	// client
	public static void updateServerCard(ItemStack card, TileEntityInfoPanel panel, int slot) {
		if (card == null || panel == null || slot < 0)
			return;

		World world = panel.getWorldObj();
		if (world == null || !world.isRemote)
			return;

		NetworkHelper.network.sendToServer(new PacketCard(card, panel.xCoord, panel.yCoord, panel.zCoord, slot));
	}

	public static void updateSeverKeys(boolean altPressed) {
		NetworkHelper.network.sendToServer(new PacketKeys(altPressed));
	}
}