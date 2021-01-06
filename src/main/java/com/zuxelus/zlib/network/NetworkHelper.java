package com.zuxelus.zlib.network;

import java.util.List;

import com.zuxelus.energycontrol.network.ChannelHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NetworkHelper {

	// server
	public static void sendPacketToAllAround(int x, int y, int z, int dist, World world, IMessage packet) {
		@SuppressWarnings("unchecked")
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
