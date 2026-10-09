package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record PacketCardS2C(BlockPos pos, int slot, String className, CompoundTag tag) implements CustomPacketPayload {
	public static final Type<PacketCardS2C> ID = new Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "s2c_card"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketCardS2C> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PacketCardS2C::pos,
			ByteBufCodecs.VAR_INT, PacketCardS2C::slot,
			ByteBufCodecs.STRING_UTF8, PacketCardS2C::className,
			ByteBufCodecs.COMPOUND_TAG, PacketCardS2C::tag,
			PacketCardS2C::new);

	public PacketCardS2C(ItemStack stack, BlockPos pos, int slot) {
		this(pos, slot, stack.getItem().getClass().getName(), ItemStackHelper.getTag(stack));
	}

	@Override
	public Type<PacketCardS2C> type() {
		return ID;
	}

	public static void handleClient(PacketCardS2C packet, ClientPlayNetworking.Context context) {
		Minecraft client = context.client();
		client.execute(() -> {
			Level world = client.player.level();
			if (world == null)
				return;
			BlockEntity te = world.getBlockEntity(packet.pos());
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
			panel.resetCardData();
		});
	}
}
