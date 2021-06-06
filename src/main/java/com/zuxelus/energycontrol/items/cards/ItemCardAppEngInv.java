package com.zuxelus.energycontrol.items.cards;

import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IReadOnlyCollection;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.MEMonitorHandler;
import appeng.parts.CableBusContainer;
import appeng.parts.reporting.PartStorageMonitor;
import appeng.tile.networking.TileCableBus;
import appeng.tile.storage.TileChest;
import appeng.tile.storage.TileDrive;
import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ItemCardAppEngInv extends ItemCardBase implements ITouchAction {

	public ItemCardAppEngInv() {
		super(ItemCardType.CARD_APPENG_INV, "card_app_eng_inv");
	}

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
		if (te instanceof TileCableBus) {
			CableBusContainer cb = ((TileCableBus) te).getCableBus();
			if (cb != null)
				for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
					IPart part = cb.getPart(side);
					if (part instanceof PartStorageMonitor) {
						PartStorageMonitor monitor = (PartStorageMonitor) part;
						AENetworkProxy proxy = monitor.getProxy();
						if (proxy != null && proxy.getNode() != null && proxy.getNode().getGrid() != null) {
							gridList = proxy.getNode().getGrid().getNodes();
							if (gridList != null)
								for (IGridNode node : gridList) {
									IGridHost host = node.getMachine();
									if (host instanceof TileChest) {
										ItemStack stack = ((TileChest) host).getCell();
										updateValues(stack, stacks);
									} else if (host instanceof TileDrive) {
										for (int i = 0; i < ((TileDrive) host).getInternalInventory().getSlots(); i++) {
											ItemStack stack = ((TileDrive) host).getInternalInventory().getStackInSlot(i);
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
		for (IStorageChannel<? extends IAEStack<?>> channel : AEApi.instance().storage().storageChannels()) {
			ICellInventoryHandler<? extends IAEStack<?>> handler = AEApi.instance().registries().cell().getCellInventory(cell, null, channel);
			if (handler != null) {
				MEMonitorHandler<? extends IAEStack<?>> monitor = new MEMonitorHandler(handler);
				for (Object st : monitor.getStorageList()) {
					if (st instanceof IAEStack && ((IAEStack) st).isItem()) {
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
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		NBTTagList list = reader.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			ItemStack stack = new ItemStack(stackTag);
			result.add(new PanelString(String.format("%s %d", StringUtils.getItemName(stack), stack.getCount() - 1)));
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_APPENG;
	}

	@Override
	public boolean runTouchAction(World world, ICardReader reader, ItemStack current) {
		NBTTagList list = reader.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		if (current.isEmpty()) {
			if (list.tagCount() > 0) {
				list.removeTag(list.tagCount() - 1);
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
		list.appendTag(item.writeToNBT(new NBTTagCompound()));
		reader.setTag("Items", list);
		return true;
	}

	@Override
	public void renderImage(TextureManager manager, ICardReader reader) { }
}
