package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record PacketCardS2C(BlockPos pos, int slot, String className, NbtCompound tag) implements PacketBase {
	public static final Id<PacketCardS2C> ID = new Id<>(Identifier.of(EnergyControl.MODID, "s2c_card"));
	public static final PacketCodec<RegistryByteBuf, PacketCardS2C> CODEC = PacketCodec.tuple(
			BlockPos.PACKET_CODEC, PacketCardS2C::pos,
			PacketCodecs.VAR_INT, PacketCardS2C::slot,
			PacketCodecs.STRING, PacketCardS2C::className,
			PacketCodecs.NBT_COMPOUND, PacketCardS2C::tag,
			PacketCardS2C::new);

	public PacketCardS2C(ItemStack stack, BlockPos pos, int slot) {
		this(pos, slot, stack.getItem().getClass().getName(), ItemStackHelper.getTagCompound(stack));
	}

	@Override
	public Id<PacketCardS2C> getId() {
		return ID;
	}

	public static void handleClient(PacketCardS2C packet, ClientPlayNetworking.Context context) {
		MinecraftClient client = context.client();
		client.execute(() -> {
			World world = client.player.getWorld();
			if (world == null)
				return;
			BlockEntity te = world.getBlockEntity(packet.pos());
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
			panel.resetCardData();
		});
	}
}
