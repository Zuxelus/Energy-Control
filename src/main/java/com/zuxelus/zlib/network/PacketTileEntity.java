package com.zuxelus.zlib.network;

import com.zuxelus.zlib.tileentities.ITilePacketHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketTileEntity implements IMessage, IMessageHandler<PacketTileEntity, IMessage> {
	private int x;
	private int y;
	private int z;
	private NBTTagCompound tag;

	public PacketTileEntity() { }

	public PacketTileEntity(BlockPos pos, NBTTagCompound tag) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
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
			TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
			if (!(te instanceof ITilePacketHandler))
				return null;
			((ITilePacketHandler) te).onServerMessageReceived(message.tag);
		}
		if (ctx.side == Side.CLIENT) {
			TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(new BlockPos(message.x, message.y, message.z));
			if (!(te instanceof ITilePacketHandler))
				return null;
			((ITilePacketHandler) te).onClientMessageReceived(message.tag);
		}
		return null;
	}
}
