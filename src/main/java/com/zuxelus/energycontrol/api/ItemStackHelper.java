package com.zuxelus.energycontrol.api;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Card data is kept in the {@link DataComponents#CUSTOM_DATA} component of the stack.
 */
public final class ItemStackHelper {

	/**
	 * @return a copy of the stack's custom data. Changes to it are not saved, use {@link #update(ItemStack, Consumer)}.
	 */
	public static CompoundTag getTag(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	public static boolean hasTag(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && !data.isEmpty();
	}

	public static boolean contains(ItemStack stack, String name) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains(name);
	}

	public static void setTag(ItemStack stack, CompoundTag tag) {
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
	}

	public static void update(ItemStack stack, Consumer<CompoundTag> consumer) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, consumer);
	}

	public static void setCoordinates(ItemStack stack, BlockPos pos) {
		update(stack, tag -> {
			tag.putInt("x", pos.getX());
			tag.putInt("y", pos.getY());
			tag.putInt("z", pos.getZ());
		});
	}

	public static ItemStack getStackWithEnergy(Item item, String name, double energy) {
		ItemStack stack = new ItemStack(item);
		update(stack, tag -> tag.putDouble(name, energy));
		return stack;
	}

	public static HolderLookup.Provider registries() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null)
			return server.registryAccess();
		return ClientRegistries.get();
	}

	public static CompoundTag saveStack(ItemStack stack) {
		if (stack.isEmpty())
			return new CompoundTag();
		Tag tag = ItemStack.CODEC.encodeStart(registries().createSerializationContext(NbtOps.INSTANCE), stack).result().orElse(null);
		return tag instanceof CompoundTag compound ? compound : new CompoundTag();
	}

	public static ItemStack loadStack(CompoundTag tag) {
		if (tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		return ItemStack.OPTIONAL_CODEC.parse(registries().createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
	}

	private static class ClientRegistries {
		static HolderLookup.Provider get() {
			net.minecraft.client.multiplayer.ClientLevel level = net.minecraft.client.Minecraft.getInstance().level;
			return level != null ? level.registryAccess() : null;
		}
	}
}
