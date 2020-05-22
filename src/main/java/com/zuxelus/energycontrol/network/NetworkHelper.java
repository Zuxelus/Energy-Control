package com.zuxelus.energycontrol.network;

import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NetworkHelper {
	public static final int FIELD_DOUBLE = 1;
	public static final int FIELD_INT = 2;
	public static final int FIELD_STRING = 3;
	public static final int FIELD_BOOLEAN = 4;
	public static final int FIELD_TAG = 5;
	public static final int FIELD_NULL = 6;
	public static final int FIELD_LONG = 7;

	// server
	private static void sendPacketToAllAround(BlockPos pos, int dist, World world, IMessage packet) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer player : players) {
			if (player instanceof EntityPlayerMP) {
				double dx = pos.getX() - player.posX;
				double dy = pos.getY() - player.posY;
				double dz = pos.getZ() - player.posZ;
	
				if (dx * dx + dy * dy + dz * dz < dist * dist)
					ChannelHandler.network.sendTo(packet, (EntityPlayerMP)player);
			}
		}
	}

	// server
	public static void setSensorCardField(TileEntity panel, int slot, Map<String, Object> fields) {
		if (fields == null || fields.isEmpty() || panel == null || !(panel instanceof TileEntityInfoPanel) || slot == -1)
			return;

		if (panel.getWorld().isRemote)
			return;

		sendPacketToAllAround(panel.getPos(), 64, panel.getWorld(), new PacketCard(panel.getPos(), slot, fields));
	}

	// client
	public static void setCardSettings(ItemStack card, TileEntity panelTE, Map<String, Object> fields, byte slot) {
		if (card == null || fields == null || fields.isEmpty() || panelTE == null || !(panelTE instanceof TileEntityInfoPanel))
			return;

		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;

		ChannelHandler.network.sendToServer(new PacketClientSensor(panelTE.getPos(), slot, card.getItem().getClass().getName(), fields));
	}

	public static void chatMessage(EntityPlayer player, String message) {
		if (player instanceof EntityPlayerMP)
			ChannelHandler.network.sendTo(new PacketChat(message), (EntityPlayerMP) player);
	}

	// server
	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, int type, int value) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setInteger("value", value);
		ChannelHandler.network.sendTo(new PacketTileEntity(pos, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, int type, double value) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setDouble("value", value);
		ChannelHandler.network.sendTo(new PacketTileEntity(pos, tag), (EntityPlayerMP) crafter);
	}

	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, NBTTagCompound tag) {
		if (!(crafter instanceof EntityPlayerMP))
			return;
		ChannelHandler.network.sendTo(new PacketTileEntity(pos, tag), (EntityPlayerMP) crafter);
	}

	// client
	public static void updateSeverTileEntity(BlockPos pos, int type, String string) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setString("string", string);
		ChannelHandler.network.sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, int value) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type);
		tag.setInteger("value", value);
		ChannelHandler.network.sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, NBTTagCompound tag) {
		ChannelHandler.network.sendToServer(new PacketTileEntity(pos, tag));
	}
}
