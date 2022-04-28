package com.zuxelus.energycontrol.network;

import java.util.List;

import com.zuxelus.zlib.network.PacketTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

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
	public static void sendPacketToAllAround(World world, BlockPos pos, int dist, IMessage packet) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer player : players)
			if (player instanceof EntityPlayerMP) {
				double dx = pos.getX() - player.posX;
				double dy = pos.getY() - player.posY;
				double dz = pos.getZ() - player.posZ;
	
				if (dx * dx + dy * dy + dz * dz < dist * dist)
					network.sendTo(packet, (EntityPlayerMP)player);
			}
	}

	// server
	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, int type, int value) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setInteger("value", value);
		network.sendTo(new PacketTileEntity(pos, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, int type, double value) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setDouble("value", value);
		network.sendTo(new PacketTileEntity(pos, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, NBTTagCompound tag) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		network.sendTo(new PacketTileEntity(pos, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(World world, BlockPos pos, NBTTagCompound tag) {
		sendPacketToAllAround(world, pos, 64,new PacketTileEntity(pos, tag));
	}

	// client
	public static void updateSeverTileEntity(BlockPos pos, int type, String string) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setString("string", string);
		network.sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, int value) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setInteger("value", value);
		network.sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, NBTTagCompound tag) {
		network.sendToServer(new PacketTileEntity(pos, tag));
	}
}
