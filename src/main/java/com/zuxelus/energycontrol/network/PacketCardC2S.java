package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public record PacketCardC2S(BlockPos pos, int slot, String className, CompoundTag tag) implements CustomPacketPayload {
	public static final Type<PacketCardC2S> ID = new Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "c2s_card"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketCardC2S> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PacketCardC2S::pos,
			ByteBufCodecs.VAR_INT, PacketCardC2S::slot,
			ByteBufCodecs.STRING_UTF8, PacketCardC2S::className,
			ByteBufCodecs.COMPOUND_TAG, PacketCardC2S::tag,
			PacketCardC2S::new);

	public PacketCardC2S(ItemStack stack, BlockPos pos, int slot) {
		this(pos, slot, stack.getItem().getClass().getName(), ItemStackHelper.getTag(stack));
	}

	@Override
	public Type<PacketCardC2S> type() {
		return ID;
	}

	public static void handle(PacketCardC2S packet, ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();
		context.server().execute(() -> {
			if (player == null || player.level() == null)
				return;
			BlockEntity te = player.level().getBlockEntity(packet.pos());
			if (te == null || !(te instanceof TileEntityInfoPanel))
				return;
			TileEntityInfoPanel panel = (TileEntityInfoPanel) te;
			ItemStack stack = panel.getItem(packet.slot());
			if (!ItemCardMain.isCard(stack))
				return;
			if (!stack.getItem().getClass().getName().equals(packet.className())) {
				EnergyControl.LOGGER.warn("Class mismatch: '{}'!='{}'", packet.className(), stack.getItem().getClass().getName());
				return;
			}
			ItemStackHelper.setTag(stack, packet.tag());
		});
	}
}
