package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.containers.ContainerRemoteThermalMonitor;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TileEntityRemoteThermalMonitor extends TileEntityThermalMonitor implements ExtendedScreenHandlerFactory<BlockPos>, ISlotItemFilter {
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
	protected void readProperties(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
		super.readProperties(tag, registries);
		if (tag.contains("heat"))
			heat = tag.getInt("heat");
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
		tag = super.writeProperties(tag, registries);
		tag.putInt("heat", heat);
		return tag;
	}

	@Override
	protected void checkStatus() {
		int newStatus = -2;
		int newHeat = 0;

		if (!getStack(SLOT_CARD).isEmpty()) {
			BlockPos target = new ItemCardReader(getStack(SLOT_CARD)).getTarget();
			if (target != null) {
				int upgradeCountRange = 0;
				ItemStack stack = getStack(SLOT_UPGRADE_RANGE);
				if (!stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_range))
					upgradeCountRange = stack.getCount();
				int range = LOCATION_RANGE * (int) Math.pow(2, upgradeCountRange);
				if (Math.abs(target.getX() - pos.getX()) <= range && Math.abs(target.getY() - pos.getY()) <= range && Math.abs(target.getZ() - pos.getZ()) <= range) {
					newHeat = CrossModLoader.getReactorHeat(world, target);
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
			world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock());
		}
	}

	@Override
	protected boolean hasRotation() {
		return false;
	}

	// Inventory
	@Override
	public int size() {
		return 2;
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
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
	public ScreenHandler createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerRemoteThermalMonitor(windowId, inventory, this);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable(ModItems.remote_thermo.getTranslationKey());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
		return pos;
	}
}
