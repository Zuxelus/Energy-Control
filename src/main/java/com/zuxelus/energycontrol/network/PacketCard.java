package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
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
	private NBTTagCompound tag;
	private int x;
	private int y;
	private int z;
	private int slot;
	private String className;

	public PacketCard() { }

	public PacketCard(ItemStack stack, int x, int y, int z, int slot) {
		this.tag = ItemStackHelper.getTagCompound(stack);
		this.x = x;
		this.y = y;
		this.z = z;
		this.slot = slot;
		this.className = stack.getItem().getClass().getName();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		slot = buf.readInt();
		className = ByteBufUtils.readUTF8String(buf);
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(slot);
		ByteBufUtils.writeUTF8String(buf, className);
		ByteBufUtils.writeTag(buf, tag);
	}

	@Override
	public IMessage onMessage(PacketCard message, MessageContext ctx) {
		TileEntity te = null;
		if (ctx.side == Side.SERVER)
			te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if (ctx.side == Side.CLIENT)
			te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
		if (!(te instanceof TileEntityInfoPanel))
			return null;
		TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
		ItemStack stack = panel.getStackInSlot(message.slot);
		if (!ItemCardMain.isCard(stack))
			return null;
		if (!stack.getItem().getClass().getName().equals(message.className)) {
			EnergyControl.logger.warn("Class mismatch: '{}' != '{}'", message.className, stack.getItem().getClass().getName());
			return null;
		}
		stack.setTagCompound(message.tag);
		if (ctx.side == Side.CLIENT)
			panel.resetCardData();
		return null;
	}
}