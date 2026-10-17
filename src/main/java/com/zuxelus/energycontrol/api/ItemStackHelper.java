package com.zuxelus.energycontrol.api;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Since 1.20.5 item stacks no longer carry a free-form NBT tag. The mod's data is
 * kept in the {@code minecraft:custom_data} component instead. Components are
 * immutable, so tags returned from here are copies: write changes back with
 * {@link #updateTag} or {@link #setTag}.
 */
public final class ItemStackHelper {

	public static CompoundTag getTag(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	public static boolean hasTag(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && !data.isEmpty();
	}

	public static void setTag(ItemStack stack, CompoundTag tag) {
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
	}

	public static void updateTag(ItemStack stack, Consumer<CompoundTag> updater) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
	}

	public static void setCoordinates(ItemStack stack, BlockPos pos) {
		updateTag(stack, tag -> {
			tag.putInt("x", pos.getX());
			tag.putInt("y", pos.getY());
			tag.putInt("z", pos.getZ());
		});
	}

	public static ItemStack getStackWithEnergy(Item item, String name, double energy) {
		ItemStack stack = new ItemStack(item);
		updateTag(stack, tag -> tag.putDouble(name, energy));
		return stack;
	}
}
