package com.zuxelus.energycontrol.network;

import java.util.function.Supplier;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketCard {
	private CompoundNBT tag;
	private BlockPos pos;
	private int slot;
	private String className;

	public PacketCard(ItemStack stack, BlockPos pos, int slot) {
		this(ItemStackHelper.getTagCompound(stack), pos, slot, stack.getItem().getClass().getName());
	}

	public PacketCard(CompoundNBT tag, BlockPos pos, int slot, String className) {
		this.tag = tag;
		this.pos = pos;
		this.slot = slot;
		this.className = className;
	}

	public static void encode(PacketCard pkt, PacketBuffer buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeInt(pkt.slot);
		buf.writeString(pkt.className);
		buf.writeCompoundTag(pkt.tag);
	}

	public static PacketCard decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		int slot = buf.readInt();
		String className = buf.readString();
		CompoundNBT tag = buf.readCompoundTag();

		return new PacketCard(tag, pos, slot, className);
	}

	public static void handle(PacketCard message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			TileEntity te = null;
			if (ctx.getDirection().getReceptionSide() == LogicalSide.SERVER) {
				ServerPlayerEntity player = ctx.getSender();
				if (player == null || player.world == null)
					return;
				te = player.world.getTileEntity(message.pos);
			} else {
				@SuppressWarnings("resource")
				ClientWorld world = Minecraft.getInstance().world;
				if (world == null)
					return;
				te = world.getTileEntity(message.pos);
			}
			if (te == null || !(te instanceof TileEntityInfoPanel))
				return;
			TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
			ItemStack stack = panel.getStackInSlot(message.slot);
			if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
				return;
			if (!stack.getItem().getClass().getName().equals(message.className)) {
				EnergyControl.LOGGER.warn("Class mismatch: '%s'!='%s'", message.className, stack.getItem().getClass().getName());
				return;
			}
			stack.setTag(message.tag);
			if (ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT)
				panel.resetCardData();
		});
		ctx.setPacketHandled(true);
	}
}
