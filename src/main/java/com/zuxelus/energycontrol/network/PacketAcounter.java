package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAcounter implements IMessage, IMessageHandler<PacketAcounter, IMessage> {
	private int x;
	private int y;
	private int z;
	private int average;

	public PacketAcounter() { }

	public PacketAcounter(BlockPos pos, int average) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.average = average;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		average = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(average);
	}

	@Override
	public IMessage onMessage(PacketAcounter message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().world.getTileEntity(new BlockPos(message.x, message.y, message.z));
		if (tileEntity == null || !(tileEntity instanceof TileEntityAverageCounter))
			return null;
		((TileEntityAverageCounter) tileEntity).setClientAverage(message.average);
		return null;
	}
}