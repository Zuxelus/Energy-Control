package com.zuxelus.zlib.containers;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class ContainerBase<T extends Container> extends AbstractContainerMenu {
	public final T te;
	private final Block block;
	private final ContainerLevelAccess posCallable;
	public List<ServerPlayer> listeners = Lists.newArrayList();

	protected ContainerBase(T te, MenuType<?> type, int id) {
		this(te, type, id, null, null);
	}

	protected ContainerBase(T te, MenuType<?> type, int id, Block block, ContainerLevelAccess posCallable) {
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
	public boolean stillValid(Player player) {
		if (posCallable == null || block == null)
			return true;
		return stillValid(posCallable, player, block);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (slot == null || !slot.hasItem())
			return ItemStack.EMPTY;

		ItemStack stack = slot.getItem();
		ItemStack result = stack.copy();

		int containerSlots = slots.size() - player.getInventory().items.size();
		if (index < containerSlots) {
			if (!moveItemStackTo(stack, containerSlots, slots.size(), true))
				return ItemStack.EMPTY;
		} else if (!moveItemStackTo(stack, 0, containerSlots, false))
			return ItemStack.EMPTY;
		if (stack.getCount() == 0)
			slot.set(ItemStack.EMPTY);
		else
			slot.setChanged();
		if (stack.getCount() == result.getCount())
			return ItemStack.EMPTY;
		slot.onTake(player, stack);
		return result;
	}

	public static BlockEntity getBlockEntity(Inventory player, FriendlyByteBuf data) {
		Objects.requireNonNull(player, "Player cannot be null!");
		Objects.requireNonNull(data, "Data cannot be null!");
		BlockEntity te = player.player.level.getBlockEntity(data.readBlockPos());
		if (te instanceof TileEntityInfoPanelExtender)
			te = ((TileEntityInfoPanelExtender) te).getCore();
		if (te instanceof TileEntityAdvancedInfoPanelExtender)
			te = ((TileEntityAdvancedInfoPanelExtender) te).getCore();
		return te;
	}
}
