package com.zuxelus.energycontrol.network;

import java.util.Map;
import java.util.stream.Stream;

import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.config.ConfigHandler;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
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
	private static void sendPacketToAllAround(BlockPos pos, World world, PacketByteBuf data) {
		Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(world, pos);

		watchingPlayers.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player,
				ChannelHandler.SERVER_PLAYERS_PACKET_ID, data));
	}

	// server
	public static void setSensorCardField(BlockEntity panel, int slot, Map<String, Object> fields) {
		if (fields == null || fields.isEmpty() || panel == null || !(panel instanceof InfoPanelBlockEntity) || slot == -1)
			return;

		if (panel.getWorld().isClient)
			return;
		
		PacketByteBuf data = fieldsToPacket(panel, slot, "", fields);
		sendPacketToAllAround(panel.getPos(), panel.getWorld(), data);
	}

	// server
	public static void sendAlarmList(PlayerEntity player) {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeInt(ConfigHandler.maxAlarmRange);
		data.writeString(ConfigHandler.allowedAlarms);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ChannelHandler.SERVER_ALARM_LIST_PACKET_ID, data);
	}

	// client
	public static void setCardSettings(ItemStack card, InfoPanelBlockEntity panel, Map<String, Object> fields, byte slot) {
		if (card.isEmpty() || fields == null || fields.isEmpty() || panel == null)
			return;

		if (!panel.getWorld().isClient)
			return;

		PacketByteBuf data = fieldsToPacket(panel, slot, card.getItem().getClass().getName(), fields);
		ClientSidePacketRegistry.INSTANCE.sendToServer(ChannelHandler.CLIENT_CARD_SETTINGS_PACKET_ID, data);
	}

	public static void updateSeverKeys(PlayerEntity player, boolean mode, boolean alt) {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeUuid(player.getGameProfile().getId());
		data.writeBoolean(mode);
		data.writeBoolean(alt);
		ClientSidePacketRegistry.INSTANCE.sendToServer(ChannelHandler.CLIENT_KEYS_PACKET_ID, data);
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, String string) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putString("string", string);
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeBlockPos(pos);
		data.writeCompoundTag(tag);
		ClientSidePacketRegistry.INSTANCE.sendToServer(ChannelHandler.CLIENT_BLOCK_ENTITY_PACKET_ID, data);
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, int value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putInt("value", value);
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeBlockPos(pos);
		data.writeCompoundTag(tag);
		ClientSidePacketRegistry.INSTANCE.sendToServer(ChannelHandler.CLIENT_BLOCK_ENTITY_PACKET_ID, data);
	}

	public static void updateSeverTileEntity(BlockPos pos, CompoundTag tag) {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeBlockPos(pos);
		data.writeCompoundTag(tag);
		ClientSidePacketRegistry.INSTANCE.sendToServer(ChannelHandler.CLIENT_BLOCK_ENTITY_PACKET_ID, data);
	}
	
	private static PacketByteBuf fieldsToPacket(BlockEntity be, int slot, String name, Map<String, Object> fields) {
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeBlockPos(be.getPos());
		data.writeByte(slot);
		data.writeString(name);
		data.writeShort(fields.size());
		for (Map.Entry<String, Object> entry : fields.entrySet()) {
			data.writeString(entry.getKey());
			Object value = entry.getValue();
			if (value instanceof Long) {
				data.writeByte(FIELD_LONG);
				data.writeLong((Long) value);
			} else if (value instanceof Double) {
				data.writeByte(FIELD_DOUBLE);
				data.writeDouble((Double) value);
			} else if (value instanceof Integer) {
				data.writeByte(FIELD_INT);
				data.writeInt((Integer) value);
			} else if (value instanceof String) {
				data.writeByte(FIELD_STRING);
				data.writeString((String) value);
			} else if (value instanceof Boolean) {
				data.writeByte(FIELD_BOOLEAN);
				data.writeBoolean((Boolean) value);
			} else if (value instanceof CompoundTag) {
				data.writeByte(FIELD_TAG);
				data.writeCompoundTag((CompoundTag) value);
			} else if (value == null)
				data.writeByte(FIELD_NULL);
		}
		return data;
	}
}
