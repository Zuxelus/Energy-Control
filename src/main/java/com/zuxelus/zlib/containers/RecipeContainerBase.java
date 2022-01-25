package com.zuxelus.zlib.containers;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class RecipeContainerBase<T extends Inventory> extends AbstractRecipeScreenHandler<T> {
	public final T te;
	private final Block block;
	private final ScreenHandlerContext posCallable;
	public List<ServerPlayerEntity> listeners = Lists.newArrayList();

	protected RecipeContainerBase(T te, ScreenHandlerType<?> type, int id) {
		this(te, type, id, null, null);
	}

	protected RecipeContainerBase(T te, ScreenHandlerType<?> type, int id, Block block, ScreenHandlerContext posCallable) {
		super(type, id);
		this.te = te;
		this.block = block;
		this.posCallable = posCallable;
	}

	protected void addPlayerInventorySlots(Inventory inventory, int height) {
		addPlayerInventorySlots(inventory, 178, height);
	}

	protected void addPlayerInventorySlots(Inventory inventory, int width, int height) {
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlot(new Slot(inventory, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(inventory, xStart, height);
	}

	protected void addPlayerInventoryTopSlots(Inventory inventory, int width, int height) {
		for (int col = 0; col < 9; col++)
			addSlot(new Slot(inventory, col, width + col * 18, height - 24));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		if (posCallable == null || block == null)
			return true;
		return canUse(posCallable, player, block);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		Slot slot = slots.get(index);
		if (slot == null || !slot.hasStack())
			return ItemStack.EMPTY;

		ItemStack stack = slot.getStack();
		ItemStack result = stack.copy();

		int containerSlots = slots.size() - player.getInventory().main.size();
		if (index < containerSlots) {
			if (!insertItem(stack, containerSlots, slots.size(), true))
				return ItemStack.EMPTY;
		} else if (!insertItem(stack, 0, containerSlots, false))
			return ItemStack.EMPTY;
		if (stack.getCount() == 0)
			slot.setStack(ItemStack.EMPTY);
		else
			slot.markDirty();
		if (stack.getCount() == result.getCount())
			return ItemStack.EMPTY;
		slot.onTakeItem(player, stack);
		return result;
	}

	public static BlockEntity getBlockEntity(PlayerInventory player, PacketByteBuf data) {
		return ContainerBase.getBlockEntity(player, data);
	}
}
