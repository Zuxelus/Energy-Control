package com.zuxelus.energycontrol.network;

import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class NetworkHelper {
	public static final int FIELD_DOUBLE = 1;
	public static final int FIELD_INT = 2;
	public static final int FIELD_STRING = 3;
	public static final int FIELD_BOOLEAN = 4;
	public static final int FIELD_TAG = 5;
	public static final int FIELD_NULL = 6;
	public static final int FIELD_LONG = 7;

	// server
	private static void sendPacketToAllAround(int x, int y, int z, int dist, World world, IMessage packet) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer player : players) {
			if (player instanceof EntityPlayerMP) {
				double dx = x - player.posX;
				double dy = y - player.posY;
				double dz = z - player.posZ;
	
				if (dx * dx + dy * dy + dz * dz < dist * dist)
					ChannelHandler.network.sendTo(packet, (EntityPlayerMP)player);
			}
		}
	}

	// server
	public static void setSensorCardField(TileEntity panel, int slot, Map<String, Object> fields) {
		if (fields == null || fields.isEmpty() || panel == null || !(panel instanceof TileEntityInfoPanel) || slot == -1)
			return;

		if (panel.getWorldObj().isRemote)
			return;

		sendPacketToAllAround(panel.xCoord, panel.yCoord, panel.zCoord, 64, panel.getWorldObj(), new PacketCard(panel, slot, fields));
	}

	// client
	public static void setCardSettings(ItemStack card, TileEntity panel, Map<String, Object> fields, byte slot) {
		if (card == null || fields == null || fields.isEmpty() || panel == null || !(panel instanceof TileEntityInfoPanel))
			return;

		if (!panel.getWorldObj().isRemote)
			return;

		ChannelHandler.network.sendToServer(new PacketClientSensor(panel, slot, card.getItem().getClass().getName(), fields));
	}

	public static void chatMessage(EntityPlayer player, String message) {
		chatMessage(player, message, 0, 0);
	}

	public static void chatMessage(EntityPlayer player, String message, int type, int value) {
		if (player instanceof EntityPlayerMP)
			ChannelHandler.network.sendTo(new PacketChat(message, type, value), (EntityPlayerMP) player);
	}

	// server
	public static void updateClientTileEntity(ICrafting crafter, int x, int y, int z, int type, int value) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setInteger("value", value);
		ChannelHandler.network.sendTo(new PacketTileEntity(x, y, z, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(ICrafting crafter, int x, int y, int z, int type, double value) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setDouble("value", value);
		ChannelHandler.network.sendTo(new PacketTileEntity(x, y, z, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(ICrafting crafter, int x, int y, int z, NBTTagCompound tag) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		ChannelHandler.network.sendTo(new PacketTileEntity(x, y, z, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(World world, int x, int y, int z, NBTTagCompound tag) {
		sendPacketToAllAround(x, y, z, 64, world, new PacketTileEntity(x, y, z, tag));
	}

	// client
	public static void updateSeverTileEntity(int x, int y, int z, int type, String string) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setString("string", string);
		ChannelHandler.network.sendToServer(new PacketTileEntity(x, y, z, tag));
	}

	public static void updateSeverTileEntity(int x, int y, int z, int type, int value) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setInteger("value", value);
		ChannelHandler.network.sendToServer(new PacketTileEntity(x, y, z, tag));
	}

	public static void updateSeverTileEntity(int x, int y, int z, NBTTagCompound tag) {
		ChannelHandler.network.sendToServer(new PacketTileEntity(x, y, z, tag));
	}
}
