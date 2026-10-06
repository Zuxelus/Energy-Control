package com.zuxelus.energycontrol.api;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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

	/** @return a copy of the stack's custom data, or null if it has none */
	public static CompoundTag getTag(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? null : data.copyTag();
	}

	/** @return a copy of the stack's custom data, or an empty tag if it has none */
	public static CompoundTag getTagCompound(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	public static boolean hasTag(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && !data.isEmpty();
	}

	public static void setTag(ItemStack stack, CompoundTag tag) {
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag == null ? new CompoundTag() : tag);
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

	public static CompoundTag getOrCreateNbtData(ItemStack stack) {
		return getTagCompound(stack);
	}

	public static ItemStack getStackWithEnergy(Item item, double energy) {
		ItemStack stack = new ItemStack(item);
		updateTag(stack, tag -> tag.putDouble("charge", energy));
		return stack;
	}

	/** ItemStack.saveOptional was removed in 1.21.5: encode with the codec instead */
	public static Tag saveOptional(ItemStack stack, HolderLookup.Provider registries) {
		return ItemStack.OPTIONAL_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
	}

	public static ItemStack parseOptional(HolderLookup.Provider registries, Tag tag) {
		return ItemStack.OPTIONAL_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
	}
}
