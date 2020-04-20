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
	public static void sendEnergyCounterValue(TileEntityEnergyCounter counter, IContainerListener crafter) {
		if (counter == null || !(crafter instanceof EntityPlayerMP))
			return;
		ChannelHandler.network.sendTo(new PacketEncounter(counter.getPos(), counter.counter), (EntityPlayerMP) crafter);
	}

	// server
	public static void sendAverageCounterValue(TileEntityAverageCounter counter, IContainerListener crafter, int average) {
		if (counter == null || !(crafter instanceof EntityPlayerMP))
			return;
		ChannelHandler.network.sendTo(new PacketAcounter(counter.getPos(), average), (EntityPlayerMP) crafter);
	}

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
/*
	// client
	public static void setDisplaySettings(TileEntityInfoPanel panel, byte slot, int settings) {
		if (panel == null)
			return;

		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;

		ChannelHandler.network.sendToServer(new PacketClientApplySettings(panel.xCoord, panel.yCoord, panel.zCoord, slot, settings));
	}*/

	// client
	public static void setCardSettings(ItemStack card, TileEntity panelTE, Map<String, Object> fields, byte slot) {
		if (card == null || fields == null || fields.isEmpty() || panelTE == null || !(panelTE instanceof TileEntityInfoPanel))
			return;

		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;

		ChannelHandler.network.sendToServer(new PacketClientSensor(panelTE.getPos(), slot, card.getItem().getClass().getName(), fields));
	}
/*
	// server
	public static void setSensorCardTitle(TileEntityInfoPanel panel, byte slot, String title) {
		if (title == null || panel == null)
			return;

		sendPacketToAllAround(panel.xCoord, panel.yCoord, panel.zCoord, 64, panel.getWorldObj(),
				new PacketSensorTitle(panel.xCoord, panel.yCoord, panel.zCoord, slot, title));
	}
*/
	public static void chatMessage(EntityPlayer player, String message) {
		if (player instanceof EntityPlayerMP)
			ChannelHandler.network.sendTo(new PacketChat(message), (EntityPlayerMP) player);
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
/*
	// client
	public static void setRangeTrigger(int x, int y, int z, double value, boolean isEnd) {
		ChannelHandler.network.sendToServer(new PacketClientRangeTrigger(x, y, z, value, isEnd));
	}

	// client
	public static void setScreenColor(int x, int y, int z, int back, int text) {
		ChannelHandler.network.sendToServer(new PacketClientColor(x, y, z, (back << 4) | text));
	}

	// client
	public static void requestDisplaySettings(TileEntityInfoPanel panel) {
		ChannelHandler.network.sendToServer(new PacketClientRequestSettings(panel.xCoord, panel.yCoord, panel.zCoord));
	}

	// server
	public static void sendDisplaySettingsToPlayer(int x, int y, int z, EntityPlayerMP player) {
		TileEntity tileEntity = player.worldObj.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityInfoPanel))
			return;
		Map<Byte, Map<Integer, Integer>> settings = ((TileEntityInfoPanel) tileEntity).getDisplaySettings();
		if (settings == null)
			return;
		ChannelHandler.network.sendTo(new PacketDispSettingsAll(x, y, z, settings), player);
	}

	// server
	public static void sendDisplaySettingsUpdate(TileEntityInfoPanel panel, byte slot, int key, int value) {
		sendPacketToAllAround(panel.xCoord, panel.yCoord, panel.zCoord, 64, panel.getWorldObj(),
				new PacketDispSettingsUpdate(panel.xCoord, panel.yCoord, panel.zCoord, slot, key, value));
	}*/
}
