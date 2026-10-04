package com.zuxelus.energycontrol.api;

import java.util.function.Consumer;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Since 1.20.5 item stacks no longer carry a free-form NBT tag. The mod's data is
 * kept in the {@code minecraft:custom_data} component instead. Components are
 * immutable, so tags returned from here are copies: write changes back with
 * {@link #updateTag} or {@link #setTag}.
 */
public final class ItemStackHelper {

	/** @return a copy of the stack's custom data, or null if it has none */
	public static NbtCompound getTag(ItemStack stack) {
		NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
		return data == null ? null : data.copyNbt();
	}

	/** @return a copy of the stack's custom data, or an empty tag if it has none */
	public static NbtCompound getTagCompound(ItemStack stack) {
		return stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
	}

	public static boolean hasTag(ItemStack stack) {
		NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
		return data != null && !data.isEmpty();
	}

	public static void setTag(ItemStack stack, NbtCompound tag) {
		NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, tag == null ? new NbtCompound() : tag);
	}

	public static void updateTag(ItemStack stack, Consumer<NbtCompound> updater) {
		NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, updater);
	}

	public static void setCoordinates(ItemStack stack, BlockPos pos) {
		updateTag(stack, tag -> {
			tag.putInt("x", pos.getX());
			tag.putInt("y", pos.getY());
			tag.putInt("z", pos.getZ());
		});
	}

	public static NbtCompound getOrCreateNbtData(ItemStack stack) {
		return getTagCompound(stack);
	}

	public static ItemStack getStackWithEnergy(Item item, double energy) {
		ItemStack stack = new ItemStack(item);
		updateTag(stack, tag -> tag.putDouble("charge", energy));
		return stack;
	}
}
