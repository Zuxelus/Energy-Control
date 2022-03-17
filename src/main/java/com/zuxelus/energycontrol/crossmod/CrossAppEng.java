package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Method;
import java.util.Collection;

import com.zuxelus.energycontrol.utils.StringUtils;

import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.StorageCell;
import appeng.blockentity.crafting.CraftingMonitorBlockEntity;
import appeng.blockentity.storage.ChestBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.me.helpers.IGridConnectedBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CrossAppEng extends CrossModBase {

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		if (te instanceof IAEPowerStorage) {
			CompoundTag tag = new CompoundTag();
			IAEPowerStorage storage = (IAEPowerStorage) te;
			tag.putString("euType", "AE");
			tag.putDouble("storage", storage.getAECurrentPower());
			tag.putDouble("maxStorage", storage.getAEMaxPower());
			return tag;
		}
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		int[] values = { 0, 0, 0, 0, 0 };
		Iterable<IGridNode> list = null;
		CompoundTag tag = new CompoundTag();

		if (te instanceof CraftingMonitorBlockEntity) {
			CraftingMonitorBlockEntity tile = (CraftingMonitorBlockEntity) te;
			tag.putInt("type", 0);
			if (tile.getJobProgress() != null) {
				tag.putInt("type", 2);
				//tag.putString("name",  StringUtils.getItemName(tile.getJobProgress().createItemStack())); // TODO
				//tag.putInt("size", (int) tile.getJobProgress().getStackSize());
			}
			return tag;
		}

		if (te instanceof IGridConnectedBlockEntity)
			list = ((IGridConnectedBlockEntity) te).getMainNode().getNode().getGrid().getNodes();

		if (list == null)
			return null;

		int cells = 0;
		for (IGridNode node : list) {
			Object host = node.getOwner();
			if (host instanceof ChestBlockEntity) {
				ItemStack stack = ((ChestBlockEntity) host).getCell();
				cells += calcValues(stack, values);
			} else if (host instanceof DriveBlockEntity) {
				for (int i = 0; i < ((DriveBlockEntity) host).getInternalInventory().size(); i++) {
					ItemStack stack = ((DriveBlockEntity) host).getInternalInventory().getStackInSlot(i);
					cells += calcValues(stack, values);
				}
			}
		}

		tag.putInt("type", 1);
		tag.putInt("nodes", list instanceof Collection ? ((Collection<?>) list).size() : 0);
		tag.putInt("cells", cells);
		tag.putInt("bytesTotal", values[0]);
		tag.putInt("bytesUsed", values[1]);
		tag.putInt("typesTotal", values[2]);
		tag.putInt("typesUsed", values[3]);
		tag.putInt("items", values[4]);
		return tag;
	}

	private int calcValues(ItemStack stack, int[] values) {
		if (stack == null)
			return 0;

		int cells = 0;
		StorageCell cell = StorageCells.getCellInventory(stack, null);
		
		/*for (IStorageChannel<?> channel : StorageChannels.getAll()) {
			ICellInventoryHandler<?> handler = cellHandler.getCellInventory(stack, null, channel);
			if (handler instanceof BasicCellInventoryHandler) {
				IMEInventory<?> invHandler = ((BasicCellInventoryHandler<?>) handler).getInternal();
				if (invHandler instanceof MEPassThrough) {
					try {
						Method method = invHandler.getClass().getDeclaredMethod("getInternal");
						method.setAccessible(true);
						Object internal = method.invoke(invHandler);

						method = internal.getClass().getDeclaredMethod("getTotalBytes");
						method.setAccessible(true);
						Object result = method.invoke(internal);
						values[0] += ((Long) result).intValue();
						method = internal.getClass().getDeclaredMethod("getUsedBytes");
						method.setAccessible(true);
						result = method.invoke(internal);
						values[1] += ((Long) result).intValue();
						method = internal.getClass().getDeclaredMethod("getTotalItemTypes");
						method.setAccessible(true);
						result = method.invoke(internal);
						values[2] += ((Long) result).intValue();
						method = internal.getClass().getDeclaredMethod("getStoredItemTypes");
						method.setAccessible(true);
						result = method.invoke(internal);
						values[3] += ((Long) result).intValue();
						method = internal.getClass().getDeclaredMethod("getStoredItemCount");
						method.setAccessible(true);
						result = method.invoke(internal);
						values[4] += ((Long) result).intValue();
					} catch (Throwable t) { };
					cells++;
				}
			}
		}*/
		return cells;
	}
}
