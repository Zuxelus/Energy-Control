package com.zuxelus.energycontrol.network;

import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.ITilePacketHandler;
import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.MainCardItem;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ChannelHandler {
	public static final Identifier SERVER_PLAYERS_PACKET_ID = new Identifier(EnergyControl.MODID, "server_players");
	public static final Identifier CLIENT_BLOCK_ENTITY_PACKET_ID = new Identifier(EnergyControl.MODID, "client_block_entity");
	public static final Identifier CLIENT_CARD_SETTINGS_PACKET_ID = new Identifier(EnergyControl.MODID, "client_card_settings");

	public static void init() {
		ServerSidePacketRegistry.INSTANCE.register(CLIENT_BLOCK_ENTITY_PACKET_ID, (packetContext, data) -> {
			BlockPos pos = data.readBlockPos();
			CompoundTag tag = data.readCompoundTag();
			packetContext.getTaskQueue().execute(() -> {
				BlockEntity be = packetContext.getPlayer().world.getBlockEntity(pos);
				if (be instanceof ITilePacketHandler)
					((ITilePacketHandler) be).onServerMessageReceived(tag);
			});
		});

		ServerSidePacketRegistry.INSTANCE.register(CLIENT_CARD_SETTINGS_PACKET_ID, (packetContext, data) -> {
			BlockPos pos = data.readBlockPos();
			byte slot = data.readByte();
			String className = data.readString();
			int fieldCount = data.readShort();
			Map<String, Object> fields = fieldsFromData(fieldCount, data);
			packetContext.getTaskQueue().execute(() -> {
				BlockEntity be = packetContext.getPlayer().world.getBlockEntity(pos);
				if (!(be instanceof InfoPanelBlockEntity))
					return;

				InfoPanelBlockEntity panel = (InfoPanelBlockEntity) be;
				ItemStack stack = panel.getInvStack(slot);
				if (stack.isEmpty() || !(stack.getItem() instanceof MainCardItem))
					return;
				if (!stack.getItem().getClass().getName().equals(className)) {
					EnergyControl.LOGGER.warn("Class mismatch: '%s'!='%s'", className, stack.getItem().getClass().getName());
					return;
				}
				ItemCardReader reader = new ItemCardReader(stack);
				for (Map.Entry<String, Object> entry : fields.entrySet()) {
					String name = entry.getKey();
					Object value = entry.getValue();
					if (value instanceof Long)
						reader.setLong(name, (Long) value);
					else if (value instanceof Double)
						reader.setDouble(name, (Double) value);
					else if (value instanceof Integer)
						reader.setInt(name, (Integer) value);
					else if (value instanceof String)
						reader.setString(name, (String) value);
					else if (value instanceof Boolean)
						reader.setBoolean(name, (Boolean) value);
				}
			});
		});
	}
	
	public static void initClient() {
		ClientSidePacketRegistry.INSTANCE.register(SERVER_PLAYERS_PACKET_ID, (packetContext, data) -> {
			BlockPos pos = data.readBlockPos();
			byte slot = data.readByte();
			String className = data.readString();
			int fieldCount = data.readShort();
			Map<String, Object> fields = fieldsFromData(fieldCount, data);
			packetContext.getTaskQueue().execute(() -> {
				BlockEntity be = packetContext.getPlayer().world.getBlockEntity(pos);
				if (!(be instanceof InfoPanelBlockEntity))
					return;

				InfoPanelBlockEntity panel = (InfoPanelBlockEntity) be;
				ItemStack stack = panel.getInvStack(slot);
				if (stack.isEmpty() || !(stack.getItem() instanceof MainCardItem))
					return;
				
				ItemCardReader reader = new ItemCardReader(stack);
				for (Map.Entry<String, Object> entry : fields.entrySet()) {
					String name = entry.getKey();
					Object value = entry.getValue();
					if (value instanceof Long) {
						reader.setLong(name, (Long) value);
					} else if (value instanceof Double) {
						reader.setDouble(name, (Double) value);
					} else if (value instanceof Integer) {
						reader.setInt(name, (Integer) value);
					} else if (value instanceof String) {
						reader.setString(name, (String) value);
					} else if (value instanceof Boolean) {
						reader.setBoolean(name, (Boolean) value);
					} else if (value instanceof CompoundTag) {
						reader.setTag(name, (CompoundTag) value);
					} else if (value == null)
						reader.clearField(name);
				}
				panel.resetCardData();
			});
		});
	}
	
	public static Map<String, Object> fieldsFromData(int fieldCount, PacketByteBuf data) {
		Map<String, Object> fields = new HashMap<String, Object>();
		for (int i = 0; i < fieldCount; i++) {
			String name = data.readString();
			byte type = data.readByte();
			switch (type) {
			case NetworkHelper.FIELD_INT:
				fields.put(name, data.readInt());
				break;
			case NetworkHelper.FIELD_BOOLEAN:
				fields.put(name, data.readBoolean());
				break;
			case NetworkHelper.FIELD_LONG:
				fields.put(name, data.readLong());
				break;
			case NetworkHelper.FIELD_DOUBLE:
				fields.put(name, data.readDouble());
				break;
			case NetworkHelper.FIELD_STRING:
				fields.put(name, data.readString());
				break;
			case NetworkHelper.FIELD_TAG:
				fields.put(name, data.readCompoundTag());
				break;
			case NetworkHelper.FIELD_NULL:
				fields.put(name, null);
			default:
				EnergyControl.LOGGER.warn("Invalid field type in Packet: %d", type);
				break;
			}
		}
		return fields;
	}
}
