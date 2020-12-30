package com.zuxelus.energycontrol.network;

import java.util.Map;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.NetworkHelper;
import com.zuxelus.zlib.network.PacketTileEntity;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ChannelHandler {
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("EnCtrl");

	public static void init() {
		network.registerMessage(PacketAlarm.class, PacketAlarm.class, 1, Side.CLIENT); //server to client
		network.registerMessage(PacketCard.class, PacketCard.class, 2, Side.CLIENT);
		network.registerMessage(PacketTileEntity.class, PacketTileEntity.class, 5, Side.CLIENT);
		network.registerMessage(PacketTileEntity.class, PacketTileEntity.class, 6, Side.SERVER);
		network.registerMessage(PacketClientSensor.class, PacketClientSensor.class, 7, Side.SERVER);
	}

	// server
	public static void setSensorCardField(TileEntity panel, int slot, Map<String, Object> fields) {
		if (fields == null || fields.isEmpty() || panel == null || !(panel instanceof TileEntityInfoPanel) || slot == -1)
			return;

		if (panel.getWorldObj().isRemote)
			return;

		NetworkHelper.sendPacketToAllAround(panel.xCoord, panel.yCoord, panel.zCoord, 64, panel.getWorldObj(), new PacketCard(panel, slot, fields));
	}

	// client
	public static void setCardSettings(ItemStack card, TileEntity panel, Map<String, Object> fields, byte slot) {
		if (card == null || fields == null || fields.isEmpty() || panel == null || !(panel instanceof TileEntityInfoPanel))
			return;

		if (!panel.getWorldObj().isRemote)
			return;

		ChannelHandler.network.sendToServer(new PacketClientSensor(panel, slot, card.getItem().getClass().getName(), fields));
	}
}