package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketCard(CompoundTag tag, BlockPos pos, int slot, String className) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketCard> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketCard> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.COMPOUND_TAG, PacketCard::tag,
			BlockPos.STREAM_CODEC, PacketCard::pos,
			ByteBufCodecs.INT, PacketCard::slot,
			ByteBufCodecs.STRING_UTF8, PacketCard::className,
			PacketCard::new);

	public PacketCard(ItemStack stack, BlockPos pos, int slot) {
		this(ItemStackHelper.getTag(stack), pos, slot, stack.getItem().getClass().getName());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleServer(PacketCard message, IPayloadContext ctx) {
		handle(message, ctx.player().level(), false);
	}

	public static void handleClient(PacketCard message, IPayloadContext ctx) {
		handle(message, ctx.player().level(), true);
	}

	private static void handle(PacketCard message, Level level, boolean client) {
		if (!level.isLoaded(message.pos))
			return;
		BlockEntity te = level.getBlockEntity(message.pos);
		if (!(te instanceof TileEntityInfoPanel panel))
			return;
		ItemStack stack = panel.getItem(message.slot);
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
			return;
		if (!stack.getItem().getClass().getName().equals(message.className)) {
			EnergyControl.LOGGER.warn("Class mismatch: '{}'!='{}'", message.className, stack.getItem().getClass().getName());
			return;
		}
		ItemStackHelper.setTag(stack, message.tag);
		if (client)
			panel.resetCardData();
	}
}
