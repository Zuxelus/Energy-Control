package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record PacketCardC2S(BlockPos pos, int slot, String className, NbtCompound tag) implements PacketBase {
	public static final Id<PacketCardC2S> ID = new Id<>(Identifier.of(EnergyControl.MODID, "c2s_card"));
	public static final PacketCodec<RegistryByteBuf, PacketCardC2S> CODEC = PacketCodec.tuple(
			BlockPos.PACKET_CODEC, PacketCardC2S::pos,
			PacketCodecs.VAR_INT, PacketCardC2S::slot,
			PacketCodecs.STRING, PacketCardC2S::className,
			PacketCodecs.NBT_COMPOUND, PacketCardC2S::tag,
			PacketCardC2S::new);

	public PacketCardC2S(ItemStack stack, BlockPos pos, int slot) {
		this(pos, slot, stack.getItem().getClass().getName(), ItemStackHelper.getTagCompound(stack));
	}

	@Override
	public Id<PacketCardC2S> getId() {
		return ID;
	}

	public static void handle(PacketCardC2S packet, ServerPlayNetworking.Context context) {
		ServerPlayerEntity player = context.player();
		context.server().execute(() -> {
			if (player == null || player.getWorld() == null)
				return;
			BlockEntity te = player.getWorld().getBlockEntity(packet.pos());
			if (te == null || !(te instanceof TileEntityInfoPanel))
				return;
			TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
			ItemStack stack = panel.getStack(packet.slot());
			if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
				return;
			if (!stack.getItem().getClass().getName().equals(packet.className())) {
				EnergyControl.LOGGER.warn("Class mismatch: '{}'!='{}'", packet.className(), stack.getItem().getClass().getName());
				return;
			}
			ItemStackHelper.setTag(stack, packet.tag());
		});
	}
}
