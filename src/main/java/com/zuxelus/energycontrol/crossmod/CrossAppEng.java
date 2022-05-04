package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardAppEng;
import com.zuxelus.energycontrol.items.cards.ItemCardAppEngInv;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitAppEng;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;

import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.util.IReadOnlyCollection;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.crafting.TileCraftingMonitorTile;
import appeng.tile.storage.TileChest;
import appeng.tile.storage.TileDrive;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CrossAppEng extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof IAEPowerStorage) {
			NBTTagCompound tag = new NBTTagCompound();
			IAEPowerStorage storage = (IAEPowerStorage) te;
			tag.setString(DataHelper.EUTYPE, "AE");
			tag.setDouble(DataHelper.ENERGY, storage.getAECurrentPower());
			tag.setDouble(DataHelper.CAPACITY, storage.getAEMaxPower());
			return tag;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		int[] values = { 0, 0, 0, 0, 0 };
		IReadOnlyCollection<IGridNode> list = null;
		NBTTagCompound tag = new NBTTagCompound();

		if (te instanceof TileCraftingMonitorTile) {
			TileCraftingMonitorTile tile = (TileCraftingMonitorTile) te;
			tag.setInteger("type", 0);
			if (tile.getJobProgress() != null) {
				tag.setInteger("type", 2);
				tag.setString("name", EnergyControl.proxy.getItemName(tile.getJobProgress().getItemStack()));
				tag.setInteger("size", (int) tile.getJobProgress().getStackSize());
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
			if (host instanceof TileChest) {
				ItemStack stack = ((TileChest) host).getInternalInventory().getStackInSlot(1);
				cells += calcValues(stack, values);
			} else if (host instanceof TileDrive) {
				for (int i = 0; i < ((TileDrive) host).getInternalInventory().getSizeInventory(); i++) {
					ItemStack stack = ((TileDrive) host).getInternalInventory().getStackInSlot(i);
					cells += calcValues(stack, values);
				}
			}
		}

		tag.setInteger("type", 1);
		tag.setInteger("nodes", list.size());
		tag.setInteger("cells", cells);
		tag.setInteger("bytesTotal", values[0]);
		tag.setInteger("bytesUsed", values[1]);
		tag.setInteger("typesTotal", values[2]);
		tag.setInteger("typesUsed", values[3]);
		tag.setInteger("items", values[4]);
		return tag;
	}

	private int calcValues(ItemStack stack, int[] values) {
		if (stack == null)
			return 0;

		int cells = 0;
		for (StorageChannel channel : StorageChannel.values()) {
			IMEInventory handler = AEApi.instance().registries().cell().getCellInventory(stack, null, channel);
			if (handler instanceof ICellInventoryHandler) {
				ICellInventory inv = ((ICellInventoryHandler) handler).getCellInv();
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

	public void registerItems() {
		ItemKitMain.register(ItemKitAppEng::new);
		ItemCardMain.register(ItemCardAppEng::new);
		ItemCardMain.register(ItemCardAppEngInv::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_APPENG,
			new Object[] { "IT", "PD", 'P', Items.paper, 'D', "dyePurple",
				'T', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER),
				'I', AEApi.instance().definitions().materials().fluixCrystal().maybeStack(1).get().copy() });

		Recipes.addKitRecipe(ItemCardType.KIT_APPENG, ItemCardType.CARD_APPENG);
		Recipes.addKitRecipe(ItemCardType.KIT_APPENG, ItemCardType.CARD_APPENG_INV);
	}
}
