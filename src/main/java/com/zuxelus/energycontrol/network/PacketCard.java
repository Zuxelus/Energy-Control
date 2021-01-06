package com.zuxelus.energycontrol.network;

import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class PacketCard implements IMessage, IMessageHandler<PacketCard, IMessage> {
	private int x;
	private int y;
	private int z;
	private int slot;
	private String className;
	private Map<String, Object> fields;

	public static final int FIELD_DOUBLE = 1;
	public static final int FIELD_INT = 2;
	public static final int FIELD_STRING = 3;
	public static final int FIELD_BOOLEAN = 4;
	public static final int FIELD_TAG = 5;
	public static final int FIELD_NULL = 6;
	public static final int FIELD_LONG = 7;

	public PacketCard() { }

	public PacketCard(TileEntity panel, int slot, String className, Map<String, Object> fields) {
		this.x = panel.xCoord;
		this.y = panel.yCoord;
		this.z = panel.zCoord;
		this.slot = slot;
		this.className = className;
		this.fields = fields;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		slot = buf.readInt();
		className = ByteBufUtils.readUTF8String(buf);
		int fieldCount = buf.readShort();
		fields = new HashMap<String, Object>();
		for (int i = 0; i < fieldCount; i++) {
			String name = ByteBufUtils.readUTF8String(buf);
			byte type = buf.readByte();
			switch (type) {
			case FIELD_INT:
				fields.put(name, buf.readInt());
				break;
			case FIELD_BOOLEAN:
				fields.put(name, buf.readBoolean());
				break;
			case FIELD_LONG:
				fields.put(name, buf.readLong());
				break;
			case FIELD_DOUBLE:
				fields.put(name, buf.readDouble());
				break;
			case FIELD_STRING:
				fields.put(name, ByteBufUtils.readUTF8String(buf));
				break;
			case FIELD_TAG:
				fields.put(name, ByteBufUtils.readTag(buf));
				break;
			case FIELD_NULL:
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
		ByteBufUtils.writeUTF8String(buf, className);
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
		TileEntity te = null;
		if (ctx.side == Side.SERVER)
			te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if (ctx.side == Side.CLIENT)
			te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
		if (te == null || !(te instanceof TileEntityInfoPanel))
			return null;
		TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
		ItemStack stack = panel.getStackInSlot(message.slot);
		if (stack == null || !(stack.getItem() instanceof ItemCardMain))
			return null;
		if (!stack.getItem().getClass().getName().equals(message.className)) {
			EnergyControl.logger.warn("Class mismatch: '%s'!='%s'", message.className, stack.getItem().getClass().getName());
			return null;
		}
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
		/*if (ctx.side == Side.SERVER)
			reader.commit(panel, message.slot);*/
		if (ctx.side == Side.CLIENT)
			panel.resetCardData();
		return null;
	}
}