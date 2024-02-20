package com.zuxelus.energycontrol.network;

import java.util.function.Supplier;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketCard {
	private CompoundTag tag;
	private BlockPos pos;
	private int slot;
	private String className;

	public PacketCard(ItemStack stack, BlockPos pos, int slot) {
		this(ItemStackHelper.getTagCompound(stack), pos, slot, stack.getItem().getClass().getName());
	}

	public PacketCard(CompoundTag tag, BlockPos pos, int slot, String className) {
		this.tag = tag;
		this.pos = pos;
		this.slot = slot;
		this.className = className;
	}

	public static void encode(PacketCard pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeInt(pkt.slot);
		buf.writeUtf(pkt.className);
		buf.writeNbt(pkt.tag);
	}

	public static PacketCard decode(FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		int slot = buf.readInt();
		String className = buf.readUtf();
		CompoundTag tag = buf.readNbt();

		return new PacketCard(tag, pos, slot, className);
	}

	public static void handle(PacketCard message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			BlockEntity te = null;
			if (ctx.getDirection().getReceptionSide() == LogicalSide.SERVER) {
				ServerPlayer player = ctx.getSender();
				if (player == null || player.level() == null)
					return;
				te = player.level().getBlockEntity(message.pos);
			} else {
				@SuppressWarnings("resource")
				ClientLevel world = Minecraft.getInstance().level;
				if (world == null)
					return;
				te = world.getBlockEntity(message.pos);
			}
			if (te == null || !(te instanceof TileEntityInfoPanel))
				return;
			TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
			ItemStack stack = panel.getItem(message.slot);
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
