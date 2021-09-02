package com.zuxelus.zlib.containers;

import java.util.Objects;

import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public abstract class ContainerBase<T extends IInventory> extends Container {
	public final T te;
	private final Block block;
	private final IWorldPosCallable posCallable;

	protected ContainerBase(T te, ContainerType<?> type, int id) {
		this(te, type, id, null, null);
	}

	protected ContainerBase(T te, ContainerType<?> type, int id, Block block, IWorldPosCallable posCallable) {
		super(type, id);
		this.te = te;
		this.block = block;
		this.posCallable = posCallable;
	}

	protected void addPlayerInventorySlots(PlayerInventory inventory, int height) {
		addPlayerInventorySlots(inventory, 178, height);
	}
	
	protected void addPlayerInventorySlots(PlayerInventory inventory, int width, int height) {
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlot(new Slot(inventory, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(inventory, xStart, height);
	}

	protected void addPlayerInventoryTopSlots(PlayerInventory inventory, int width, int height) {
		for (int col = 0; col < 9; col++)
			addSlot(new Slot(inventory, col, width + col * 18, height - 24));
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		if (posCallable == null || block == null)
			return true;
		return isWithinUsableDistance(posCallable, player, block);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		Slot slot = inventorySlots.get(index);
		if (slot == null || !slot.getHasStack())
			return ItemStack.EMPTY;

		ItemStack stack = slot.getStack();
		ItemStack result = stack.copy();
		
		int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
		if (index < containerSlots) {
			if (!mergeItemStack(stack, containerSlots, inventorySlots.size(), true))
				return ItemStack.EMPTY;
		} else if (!mergeItemStack(stack, 0, containerSlots, false))
			return ItemStack.EMPTY;
		if (stack.getCount() == 0)
			slot.putStack(ItemStack.EMPTY);
		else
			slot.onSlotChanged();
		if (stack.getCount() == result.getCount())
			return ItemStack.EMPTY;
		slot.onTake(player, stack);
		return result;
	}

	public static TileEntity getTileEntity(PlayerInventory player, PacketBuffer data) {
		Objects.requireNonNull(player, "Player cannot be null!");
		Objects.requireNonNull(data, "Data cannot be null!");
		TileEntity te = player.player.world.getTileEntity(data.readBlockPos());
		if (te instanceof TileEntityInfoPanelExtender)
			te = ((TileEntityInfoPanelExtender) te).getCore();
		if (te instanceof TileEntityAdvancedInfoPanelExtender)
			te = ((TileEntityAdvancedInfoPanelExtender) te).getCore();
		return te;
	}
}
