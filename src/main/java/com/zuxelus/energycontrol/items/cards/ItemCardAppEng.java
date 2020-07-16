package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.IReadOnlyCollection;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.storage.TileChest;
import appeng.tile.storage.TileDrive;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardAppEng extends ItemCardBase {

	public ItemCardAppEng() {
		super(ItemCardType.CARD_APPENG, "card_app_eng");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		int[] values = { 0, 0, 0, 0, 0 };
		IReadOnlyCollection<IGridNode> list = null;

		TileEntity te = world.getTileEntity(target);
		if (te instanceof IGridProxyable)
			list = ((IGridProxyable) te).getProxy().getNode().getGrid().getNodes();

		if (list == null)
			return CardState.NO_TARGET;

		int cells = 0;
		for (IGridNode node : list) {
			IGridHost host = node.getMachine();
			if (host instanceof TileChest) {
				ItemStack stack = ((TileChest) host).getCell();
				cells += calcValues(stack, values);
			} else if (host instanceof TileDrive) {
				for (int i = 0; i < ((TileDrive) host).getInternalInventory().getSlots(); i++) {
					ItemStack stack = ((TileDrive) host).getInternalInventory().getStackInSlot(i);
					cells += calcValues(stack, values);
				}
			}
		}

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
		for (IStorageChannel channel : AEApi.instance().storage().storageChannels()) {
			ICellInventoryHandler handler = AEApi.instance().registries().cell().getCellInventory(stack, null, channel);
			if (handler != null) {
				ICellInventory inv = handler.getCellInv();
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
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		result.add(new PanelString("msg.ec.InfoPanelTotalNodes", reader.getInt("nodes"), true));
		result.add(new PanelString("msg.ec.InfoPanelStorageCells", reader.getInt("cells"), true));
		result.add(new PanelString(I18n.format("msg.ec.InfoPanelBytesUsed", reader.getInt("bytesUsed"),reader.getInt("bytesTotal"))));
		result.add(new PanelString(I18n.format("msg.ec.InfoPanelTypes", reader.getInt("typesUsed"), reader.getInt("typesTotal"))));
		result.add(new PanelString("msg.ec.InfoPanelTotalItems", reader.getInt("items"), true));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		return null;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_APPENG;
	}
}
