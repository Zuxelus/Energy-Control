package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class PacketTileEntity implements IMessage, IMessageHandler<PacketTileEntity, IMessage> {
	private int x;
	private int y;
	private int z;
	private NBTTagCompound tag;

	public PacketTileEntity() { }

	public PacketTileEntity(int x, int y, int z, NBTTagCompound tag) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeTag(buf, tag);
	}

	@Override
	public IMessage onMessage(PacketTileEntity message, MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
			if (!(te instanceof ITilePacketHandler))
				return null;
			((ITilePacketHandler) te).onServerMessageReceived(message.tag);
		}
		if (ctx.side == Side.CLIENT) {
			TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
			if (!(te instanceof ITilePacketHandler))
				return null;
			((ITilePacketHandler) te).onClientMessageReceived(message.tag);
		}
		return null;
	}
}
