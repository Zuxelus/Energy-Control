package com.zuxelus.energycontrol.network;

import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCard implements IMessage, IMessageHandler<PacketCard, IMessage> {
	private int x;
	private int y;
	private int z;
	private int slot;
	private Map<String, Object> fields;

	public static final int FIELD_DOUBLE = 1;
	public static final int FIELD_INT = 2;
	public static final int FIELD_STRING = 3;
	public static final int FIELD_BOOLEAN = 4;
	public static final int FIELD_TAG = 5;
	public static final int FIELD_NULL = 6;
	public static final int FIELD_LONG = 7;

	public PacketCard() {	}

	public PacketCard(BlockPos pos, int slot, Map<String, Object> fields) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.slot = slot;
		this.fields = fields;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		slot = buf.readInt();
		int fieldCount = buf.readShort();
		fields = new HashMap<String, Object>();
		for (int i = 0; i < fieldCount; i++) {
			String name = ByteBufUtils.readUTF8String(buf);
			byte type = buf.readByte();
			switch (type) {
			case NetworkHelper.FIELD_INT:
				fields.put(name, buf.readInt());
				break;
			case NetworkHelper.FIELD_BOOLEAN:
				fields.put(name, buf.readBoolean());
				break;
			case NetworkHelper.FIELD_LONG:
				fields.put(name, buf.readLong());
				break;
			case NetworkHelper.FIELD_DOUBLE:
				fields.put(name, buf.readDouble());
				break;
			case NetworkHelper.FIELD_STRING:
				fields.put(name, ByteBufUtils.readUTF8String(buf));
				break;
			case NetworkHelper.FIELD_TAG:
				fields.put(name, ByteBufUtils.readTag(buf));
				break;
			case NetworkHelper.FIELD_NULL:
				fields.put(name, null);
				break;
			default:
				EnergyControl.logger.warn(String.format("Invalid field type in PacketCard: %d", type));
				break;
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(slot);
		buf.writeShort(fields.size());
		for (Map.Entry<String, Object> entry : fields.entrySet()) {
			ByteBufUtils.writeUTF8String(buf, entry.getKey());
			Object value = entry.getValue();
			if (value instanceof Long) {
				buf.writeByte(FIELD_LONG);
				buf.writeLong((Long) value);
			} else if (value instanceof Double) {
				buf.writeByte(FIELD_DOUBLE);
				buf.writeDouble((Double) value);
			} else if (value instanceof Integer) {
				buf.writeByte(FIELD_INT);
				buf.writeInt((Integer) value);
			} else if (value instanceof String) {
				buf.writeByte(FIELD_STRING);
				ByteBufUtils.writeUTF8String(buf, (String) value);
			} else if (value instanceof Boolean) {
				buf.writeByte(FIELD_BOOLEAN);
				buf.writeBoolean((Boolean) value);
			} else if (value instanceof NBTTagCompound) {
				buf.writeByte(FIELD_TAG);
				ByteBufUtils.writeTag(buf, (NBTTagCompound) value);
			} else if (value == null) {
				buf.writeByte(FIELD_NULL);
			}
		}
	}

	@Override
	public IMessage onMessage(PacketCard message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().world.getTileEntity(new BlockPos(message.x, message.y, message.z));
		if (tileEntity == null || !(tileEntity instanceof TileEntityInfoPanel))
			return null;
		TileEntityInfoPanel panel = (TileEntityInfoPanel) tileEntity;
		ItemStack stack = panel.getStackInSlot(message.slot);
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
			return null;

		ItemCardReader reader = new ItemCardReader(stack);
		for (Map.Entry<String, Object> entry : message.fields.entrySet()) {
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
			} else if (value instanceof NBTTagCompound) {
				reader.setTag(name, (NBTTagCompound) value);
			} else if (value == null)
				reader.removeField(name);
		}
		panel.resetCardData();
		return null;
	}
}