package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEncounter implements IMessage, IMessageHandler<PacketEncounter, IMessage> {
	private int x;
	private int y;
	private int z;
	private Double counter;

	public PacketEncounter() { }

	public PacketEncounter(BlockPos pos, Double counter) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.counter = counter;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		counter = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeDouble(counter);
	}

	@Override
	public IMessage onMessage(PacketEncounter message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().world.getTileEntity(new BlockPos(message.x, message.y, message.z));
		if (tileEntity == null || !(tileEntity instanceof TileEntityEnergyCounter))
			return null;
		((TileEntityEnergyCounter) tileEntity).counter = message.counter;
		return null;
	}
}