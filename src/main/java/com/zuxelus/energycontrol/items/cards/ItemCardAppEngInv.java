package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.utils.StringUtils;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.parts.IPart;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.StorageCell;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.storage.ChestBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.parts.CableBusContainer;
import appeng.parts.reporting.StorageMonitorPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemCardAppEngInv extends ItemCardMain implements ITouchAction {

	@Override
	public CardState update(Level world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		ArrayList<ItemStack> stacks = reader.getItemStackList(true);
		if (stacks.size() < 1)
			return CardState.OK;

		Iterable<IGridNode> gridList;

		BlockEntity te = world.getBlockEntity(target);
		if (te instanceof CableBusBlockEntity) {
			CableBusContainer cb = ((CableBusBlockEntity) te).getCableBus();
			if (cb != null)
				for (Direction side : Direction.values()) {
					IPart part = cb.getPart(side);
					if (part instanceof StorageMonitorPart) {
						StorageMonitorPart monitor = (StorageMonitorPart) part;
						IManagedGridNode proxy = monitor.getMainNode();
						if (proxy != null && proxy.getNode() != null && proxy.getNode().getGrid() != null) {
							gridList = proxy.getNode().getGrid().getNodes();
							if (gridList != null)
								for (IGridNode node : gridList) {
									Object host = node.getOwner();
									if (host instanceof ChestBlockEntity) {
										ItemStack stack = ((ChestBlockEntity) host).getCell();
										updateValues(stack, stacks);
									} else if (host instanceof DriveBlockEntity) {
										for (int i = 0; i < ((DriveBlockEntity) host).getInternalInventory().size(); i++) {
											ItemStack stack = ((DriveBlockEntity) host).getInternalInventory().getStackInSlot(i);
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
		ICellHandler cellHandler = StorageCells.getHandler(cell);
		if (cellHandler == null)
			return;

		StorageCell handler = StorageCells.getCellInventory(cell, null);
		/*for (IStorageChannel<?> channel : StorageChannels.getAll()) {
			ICellInventoryHandler<?> handler = cellHandler.getCellInventory(cell, null, channel);
			if (handler != null) {
				MEMonitorHandler<? extends IAEStack> monitor = new MEMonitorHandler<>(handler);
				for (Object st : monitor.getStorageList()) {
					if (st instanceof IAEStack) {
						IAEStack ae = (IAEStack) st;
						for (ItemStack stack : stacks)
							if (ae.asItemStackRepresentation().sameItem(stack))
								stack.setCount(stack.getCount() + (int) ae.getStackSize());
					}
				}
			}
		}*/
	}

	@Override
	public List<PanelString> getStringData(Level world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		ListTag list = reader.getTagList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag stackTag = list.getCompound(i);
			ItemStack stack = ItemStack.of(stackTag);
			result.add(new PanelString(String.format("%s %d", StringUtils.getItemName(stack), stack.getCount() - 1)));
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public boolean enableTouch() {
		return true;
	}

	@Override
	public boolean runTouchAction(Level world, ICardReader reader, ItemStack current) {
		ListTag list = reader.getTagList("Items", Tag.TAG_COMPOUND);
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
			if (stack.sameItem(current))
				return false;
		ItemStack item = current.copy();
		item.setCount(1);
		list.add(item.save(new CompoundTag()));
		reader.setTag("Items", list);
		return true;
	}

	@Override
	public void renderImage(ICardReader reader, PoseStack matrixStack) { }
}
