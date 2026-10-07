package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.containers.ContainerRemoteThermalMonitor;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileEntityRemoteThermalMonitor extends TileEntityThermalMonitor implements ExtendedMenuProvider<BlockPos>, ISlotItemFilter {
	public static final int SLOT_CARD = 0;
	public static final byte SLOT_UPGRADE_RANGE = 1;
	private static final int LOCATION_RANGE = 8;
	private int heat;

	public TileEntityRemoteThermalMonitor(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		heat = 0;
	}

	public TileEntityRemoteThermalMonitor(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.remote_thermo, pos, state);
	}

	public int getHeat() {
		return heat;
	}

	@Override
	protected void readProperties(ValueInput tag) {
		super.readProperties(tag);
		if (tag.contains("heat"))
			heat = tag.getIntOr("heat", 0);
	}

	@Override
	protected void writeProperties(ValueOutput tag) {
		super.writeProperties(tag);
		tag.putInt("heat", heat);
	}

	@Override
	protected void checkStatus() {
		int newStatus = -2;
		int newHeat = 0;

		if (!getItem(SLOT_CARD).isEmpty()) {
			BlockPos target = new ItemCardReader(getItem(SLOT_CARD)).getTarget();
			if (target != null) {
				int upgradeCountRange = 0;
				ItemStack stack = getItem(SLOT_UPGRADE_RANGE);
				if (!stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_range))
					upgradeCountRange = stack.getCount();
				int range = LOCATION_RANGE * (int) Math.pow(2, upgradeCountRange);
				if (Math.abs(target.getX() - worldPosition.getX()) <= range && Math.abs(target.getY() - worldPosition.getY()) <= range && Math.abs(target.getZ() - worldPosition.getZ()) <= range) {
					newHeat = CrossModLoader.getReactorHeat(level, target);
					newStatus = newHeat == -1 ? -2 : newHeat >= getHeatLevel() ? 1 : 0;
					if (newHeat == -1)
						newHeat = 0;
				}
			}
		}

		if (newStatus != status || newHeat != heat) {
			status = newStatus;
			heat = newHeat;
			notifyBlockUpdate();
			level.updateNeighborsAt(worldPosition, level.getBlockState(worldPosition).getBlock());
		}
	}

	@Override
	protected boolean hasRotation() {
		return false;
	}

	// Inventory
	@Override
	public int getContainerSize() {
		return 2;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slotIndex, ItemStack stack) { // ISlotItemFilter
		if (stack.isEmpty())
			return false;
		switch (slotIndex) {
		case SLOT_CARD:
			return stack.getItem() instanceof ItemCardMain;
		case SLOT_UPGRADE_RANGE:
			return stack.getItem().equals(ModItems.upgrade_range);
		default:
			return false;
		}
	}

	// NamedScreenHandlerFactory
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRemoteThermalMonitor(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.remote_thermo.getDescriptionId());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return worldPosition;
	}
}
