package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PacketCardC2S extends PacketBase {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "c2s_card");

	public PacketCardC2S(ItemStack stack, BlockPos pos, int slot) {
		this(ItemStackHelper.getTagCompound(stack), pos, slot, stack.getItem().getClass().getName());
	}

	public PacketCardC2S(NbtCompound tag, BlockPos pos, int slot, String className) {
		writeBlockPos(pos);
		writeVarInt(slot);
		writeString(className);
		writeNbt(tag);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		BlockPos pos = buf.readBlockPos();
		int slot = buf.readVarInt();
		String className = buf.readString();
		NbtCompound tag = buf.readNbt();
		server.execute(() -> {
			if (player == null || player.world == null)
				return;
			BlockEntity te = player.world.getBlockEntity(pos);
			if (te == null || !(te instanceof TileEntityInfoPanel))
				return;
			TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
			ItemStack stack = panel.getStack(slot);
			if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
				return;
			if (!stack.getItem().getClass().getName().equals(className)) {
				EnergyControl.LOGGER.warn("Class mismatch: '%s'!='%s'", className, stack.getItem().getClass().getName());
				return;
			}
			stack.setNbt(tag);
		});
	}
}
