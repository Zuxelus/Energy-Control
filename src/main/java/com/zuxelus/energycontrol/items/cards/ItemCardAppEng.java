package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.StringUtils;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardAppEng extends ItemCardMain {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		int[] values = { 0, 0, 0, 0, 0 };
		IReadOnlyCollection<IGridNode> list = null;

		TileEntity te = world.getBlockEntity(target);
		if (te instanceof CraftingMonitorTileEntity) {
			CraftingMonitorTileEntity tile = (CraftingMonitorTileEntity) te;
			reader.setInt("type", 0);
			if (tile.getJobProgress() != null) {
				reader.setInt("type", 2);
				reader.setString("name",  StringUtils.getItemName(tile.getJobProgress().createItemStack())); // TODO
				reader.setInt("size", (int) tile.getJobProgress().getStackSize());
			}
			return CardState.OK;
		}

		if (te instanceof IGridProxyable)
			list = ((IGridProxyable) te).getProxy().getNode().getGrid().getNodes();

		if (list == null)
			return CardState.NO_TARGET;

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

		reader.setInt("type", 1);
		reader.setInt("nodes", list.size());
		reader.setInt("cells", cells);
		reader.setInt("bytesTotal", values[0]);
		reader.setInt("bytesUsed", values[1]);
		reader.setInt("typesTotal", values[2]);
		reader.setInt("typesUsed", values[3]);
		reader.setInt("items", values[4]);
		return CardState.OK;
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

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		switch (reader.getInt("type")) {
		case 1:
			result.add(new PanelString("msg.ec.InfoPanelTotalNodes", reader.getInt("nodes"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelStorageCells", reader.getInt("cells"), showLabels));
			if (isServer) {
				result.add(new PanelString(String.format("%d of %d Bytes Used", reader.getInt("bytesUsed"), reader.getInt("bytesTotal"))));
				result.add(new PanelString(String.format("%d of %d Types", reader.getInt("typesUsed"), reader.getInt("typesTotal"))));
			} else {
				result.add(new PanelString(I18n.get("msg.ec.InfoPanelBytesUsed", reader.getInt("bytesUsed"), reader.getInt("bytesTotal"))));
				result.add(new PanelString(I18n.get("msg.ec.InfoPanelTypes", reader.getInt("typesUsed"), reader.getInt("typesTotal"))));
			}
			result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), showLabels));
			break;
		case 2:
			result.add(new PanelString("msg.ec.InfoPanelName", reader.getString("name"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSize", reader.getInt("size"), showLabels));
			break;
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}
}
