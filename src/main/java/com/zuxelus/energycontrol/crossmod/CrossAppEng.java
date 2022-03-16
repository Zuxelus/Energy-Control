package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.utils.StringUtils;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.cells.ICellInventory;
import appeng.api.storage.cells.ICellInventoryHandler;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.IReadOnlyCollection;
import appeng.core.Api;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.crafting.CraftingMonitorTileEntity;
import appeng.tile.storage.ChestTileEntity;
import appeng.tile.storage.DriveTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class CrossAppEng extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof IAEPowerStorage) {
			CompoundNBT tag = new CompoundNBT();
			IAEPowerStorage storage = (IAEPowerStorage) te;
			tag.putString("euType", "AE");
			tag.putDouble("storage", storage.getAECurrentPower());
			tag.putDouble("maxStorage", storage.getAEMaxPower());
			return tag;
		}
		return null;
	}

	@Override
	public CompoundNBT getCardData(TileEntity te) {
		int[] values = { 0, 0, 0, 0, 0 };
		IReadOnlyCollection<IGridNode> list = null;
		CompoundNBT tag = new CompoundNBT();

		if (te instanceof CraftingMonitorTileEntity) {
			CraftingMonitorTileEntity tile = (CraftingMonitorTileEntity) te;
			tag.putInt("type", 0);
			if (tile.getJobProgress() != null) {
				tag.putInt("type", 2);
				tag.putString("name",  StringUtils.getItemName(tile.getJobProgress().createItemStack())); // TODO
				tag.putInt("size", (int) tile.getJobProgress().getStackSize());
			}
			return tag;
		}

		if (te instanceof IGridProxyable)
			list = ((IGridProxyable) te).getProxy().getNode().getGrid().getNodes();

		if (list == null)
			return null;

		int cells = 0;
		for (IGridNode node : list) {
			IGridHost host = node.getMachine();
			if (host instanceof ChestTileEntity) {
				ItemStack stack = ((ChestTileEntity) host).getCell();
				cells += calcValues(stack, values);
			} else if (host instanceof DriveTileEntity) {
				for (int i = 0; i < ((DriveTileEntity) host).getInternalInventory().getSlots(); i++) {
					ItemStack stack = ((DriveTileEntity) host).getInternalInventory().getStackInSlot(i);
					cells += calcValues(stack, values);
				}
			}
		}

		tag.putInt("type", 1);
		tag.putInt("nodes", list.size());
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
		for (IStorageChannel<? extends IAEStack<?>> channel : Api.instance().storage().storageChannels()) {
			ICellInventoryHandler<? extends IAEStack<?>> handler = Api.instance().registries().cell().getCellInventory(stack, null, channel);
			if (handler != null) {
				ICellInventory<? extends IAEStack<?>> inv = handler.getCellInv();
				if (inv != null) {
					values[0] += inv.getTotalBytes();
					values[1] += inv.getUsedBytes();
					values[2] += inv.getTotalItemTypes();
					values[3] += inv.getStoredItemTypes();
					values[4] += inv.getStoredItemCount();
					cells++;
				}
			}
		}
		return cells;
	}
}
