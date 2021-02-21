package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.StringUtils;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.cells.ICellInventoryHandler;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IReadOnlyCollection;
import appeng.core.Api;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.MEMonitorHandler;
import appeng.parts.CableBusContainer;
import appeng.parts.reporting.StorageMonitorPart;
import appeng.tile.networking.CableBusTileEntity;
import appeng.tile.storage.ChestTileEntity;
import appeng.tile.storage.DriveTileEntity;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemCardAppEngInv extends ItemCardMain implements ITouchAction {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		ArrayList<ItemStack> stacks = reader.getItemStackList(true);
		if (stacks.size() < 1)
			return CardState.OK;

		IReadOnlyCollection<IGridNode> gridList = null;

		TileEntity te = world.getTileEntity(target);
		if (te instanceof CableBusTileEntity) {
			CableBusContainer cb = ((CableBusTileEntity) te).getCableBus();
			if (cb != null)
				for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
					IPart part = cb.getPart(side);
					if (part instanceof StorageMonitorPart) {
						StorageMonitorPart monitor = (StorageMonitorPart) part;
						AENetworkProxy proxy = monitor.getProxy();
						if (proxy != null && proxy.getNode() != null && proxy.getNode().getGrid() != null) {
							gridList = proxy.getNode().getGrid().getNodes();
							if (gridList != null)
								for (IGridNode node : gridList) {
									IGridHost host = node.getMachine();
									if (host instanceof ChestTileEntity) {
										ItemStack stack = ((ChestTileEntity) host).getCell();
										updateValues(stack, stacks);
									} else if (host instanceof DriveTileEntity) {
										for (int i = 0; i < ((DriveTileEntity) host).getInternalInventory().getSlots(); i++) {
											ItemStack stack = ((DriveTileEntity) host).getInternalInventory().getStackInSlot(i);
											updateValues(stack, stacks);
										}
									}
								}
						}
						reader.setItemStackList(stacks);
						return CardState.OK;
					}
				}
		}
		reader.setItemStackList(stacks);
		return CardState.NO_TARGET;
	}

	private void updateValues(ItemStack cell, ArrayList<ItemStack> stacks) {
		for (IStorageChannel<? extends IAEStack<?>> channel : Api.instance().storage().storageChannels()) {
			ICellInventoryHandler<? extends IAEStack<?>> handler = Api.instance().registries().cell().getCellInventory(cell, null, channel);
			if (handler != null) {
				MEMonitorHandler<? extends IAEStack<?>> monitor = new MEMonitorHandler(handler);
				for (Object st : monitor.getStorageList()) {
					if (st instanceof IAEStack) {
						IAEStack ae = (IAEStack) st;
						for (ItemStack stack : stacks)
							if (ae.asItemStackRepresentation().isItemEqual(stack))
								stack.setCount(stack.getCount() + (int) ((IAEStack) st).getStackSize());
					}
				}
			}
		}
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		ListNBT list = reader.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT stackTag = list.getCompound(i);
			ItemStack stack = ItemStack.read(stackTag);
			result.add(new PanelString(String.format("%s %d", StringUtils.getItemName(stack), stack.getCount() - 1)));
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public Item getKitFromCard() {
		return ModItems.kit_app_eng.get();
	}

	@Override
	public boolean runTouchAction(World world, ICardReader reader, ItemStack current) {
		ListNBT list = reader.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		if (current.isEmpty()) {
			if (list.size() > 0) {
				list.remove(list.size() - 1);
				reader.setTag("Items", list);
				return true;
			}
			return false;
		}
		ArrayList<ItemStack> stacks = reader.getItemStackList(true);
		for (ItemStack stack : stacks)
			if (stack.isItemEqual(current))
				return false;
		ItemStack item = current.copy();
		item.setCount(1);
		list.add(item.write(new CompoundNBT()));
		reader.setTag("Items", list);
		return true;
	}

	@Override
	public void renderImage(TextureManager manager, ICardReader reader) { }
}
