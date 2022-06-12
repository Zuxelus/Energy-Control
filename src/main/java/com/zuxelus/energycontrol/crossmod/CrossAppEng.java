package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.cards.ItemCardAppEng;
import com.zuxelus.energycontrol.items.cards.ItemCardAppEngInv;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitAppEng;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.utils.DataHelper;

import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.IReadOnlyCollection;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.crafting.TileCraftingMonitorTile;
import appeng.tile.storage.TileChest;
import appeng.tile.storage.TileDrive;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;

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
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		int[] values = { 0, 0, 0, 0, 0 };
		IReadOnlyCollection<IGridNode> list = null;
		NBTTagCompound tag = new NBTTagCompound();

		if (te instanceof TileCraftingMonitorTile) {
			TileCraftingMonitorTile tile = (TileCraftingMonitorTile) te;
			tag.setInteger("type", 0);
			if (tile.getJobProgress() != null) {
				tag.setInteger("type", 2);
				tag.setString("name", EnergyControl.proxy.getItemName(tile.getJobProgress().createItemStack()));
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
				ItemStack stack = ((TileChest) host).getCell();
				cells += calcValues(stack, values);
			} else if (host instanceof TileDrive) {
				for (int i = 0; i < ((TileDrive) host).getInternalInventory().getSlots(); i++) {
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
		for (IStorageChannel<?> channel : AEApi.instance().storage().storageChannels()) {
			ICellInventoryHandler<?> handler = AEApi.instance().registries().cell().getCellInventory(stack, null, channel);
			if (handler != null) {
				ICellInventory<?> inv = handler.getCellInv();
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
	public void registerItems(Register<Item> event) {
		ItemKitMain.register(ItemKitAppEng::new);
		ItemCardMain.register(ItemCardAppEng::new);
		ItemCardMain.register(ItemCardAppEngInv::new);
	}
}
