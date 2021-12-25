package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketCardS2C extends PacketBase {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "s2c_card");

	public PacketCardS2C(ItemStack stack, BlockPos pos, int slot) {
		this(ItemStackHelper.getTagCompound(stack), pos, slot, stack.getItem().getClass().getName());
	}

	public PacketCardS2C(NbtCompound tag, BlockPos pos, int slot, String className) {
		writeBlockPos(pos);
		writeVarInt(slot);
		writeString(className);
		writeNbt(tag);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void handleClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		BlockPos pos = buf.readBlockPos();
		int slot = buf.readVarInt();
		String className = buf.readString();
		NbtCompound tag = buf.readNbt();
		client.execute(() -> {
			World world = client.player.getWorld();
			if (world == null)
				return;
			BlockEntity te = world.getBlockEntity(pos);
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
			panel.resetCardData();
		});
	}
}
