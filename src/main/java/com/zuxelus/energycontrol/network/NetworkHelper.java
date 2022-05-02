package com.zuxelus.energycontrol.network;

import java.util.List;

import com.zuxelus.zlib.network.PacketTileEntity;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NetworkHelper {
	public static SimpleNetworkWrapper network;

	public static void createChannel(String name) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel(name);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerClientToServer(Class<? extends IMessageHandler<REQ, REPLY>> handler, Class<REQ> request, int id) {
		network.registerMessage(handler, request, id, Side.SERVER);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerServerToClient(Class<? extends IMessageHandler<REQ, REPLY>> handler, Class<REQ> request, int id) {
		network.registerMessage(handler, request, id, Side.CLIENT);
	}

	// server
	public static void sendPacketToAllAround(World world, int x, int y, int z, int dist, IMessage packet) {
		@SuppressWarnings("unchecked")
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer player : players)
			if (player instanceof EntityPlayerMP) {
				double dx = x - player.posX;
				double dy = y - player.posY;
				double dz = z - player.posZ;
	
				if (dx * dx + dy * dy + dz * dz < dist * dist)
					network.sendTo(packet, (EntityPlayerMP)player);
			}
	}

	// server
	public static void updateClientTileEntity(ICrafting crafter, int x, int y, int z, int type, int value) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setInteger("value", value);
		network.sendTo(new PacketTileEntity(x, y, z, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(ICrafting crafter, int x, int y, int z, int type, double value) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setDouble("value", value);
		network.sendTo(new PacketTileEntity(x, y, z, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(ICrafting crafter, int x, int y, int z, NBTTagCompound tag) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		network.sendTo(new PacketTileEntity(x, y, z, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(World world, int x, int y, int z, NBTTagCompound tag) {
		sendPacketToAllAround(world, x, y, z, 64, new PacketTileEntity(x, y, z, tag));
	}

	// client
	public static void updateSeverTileEntity(int x, int y, int z, int type, String string) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setString("string", string);
		network.sendToServer(new PacketTileEntity(x, y, z, tag));
	}

	public static void updateSeverTileEntity(int x, int y, int z, int type, int value) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setInteger("value", value);
		network.sendToServer(new PacketTileEntity(x, y, z, tag));
	}

	public static void updateSeverTileEntity(int x, int y, int z, NBTTagCompound tag) {
		network.sendToServer(new PacketTileEntity(x, y, z, tag));
	}
}
