package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.blocks.RangeTrigger;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.ContainerRangeTrigger;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardEnergy;
import com.zuxelus.energycontrol.items.cards.ItemCardLiquid;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityInventory;
import com.zuxelus.energycontrol.utils.DataHelper;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileEntityRangeTrigger extends TileEntityInventory implements ExtendedMenuProvider<BlockPos>, ISlotItemFilter, ITilePacketHandler {
	public static final int SLOT_CARD = 0;
	public static final int SLOT_UPGRADE = 1;

	private static final int STATE_UNKNOWN = 0;
	private static final int STATE_PASSIVE = 1;
	private static final int STATE_ACTIVE = 2;

	protected int updateTicker;
	protected int tickRate;
	protected boolean init;

	private int status;
	private boolean poweredBlock;
	private boolean invertRedstone;
	public double levelStart;
	public double levelEnd;

	public TileEntityRangeTrigger(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		init = false;
		tickRate = ConfigHandler.rangeTriggerRefreshPeriod;
		updateTicker = tickRate;
		status = -1;
		invertRedstone = false;
		levelStart = 0;
		levelEnd = 40000;
	}

	public TileEntityRangeTrigger(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.range_trigger, pos, state);
	}

	public boolean getInvertRedstone() {
		return invertRedstone;
	}

	public void setInvertRedstone(boolean value) {
		boolean old = invertRedstone;
		invertRedstone = value;
		if (!level.isClientSide() && invertRedstone != old)
			notifyBlockUpdate();
	}

	public void setStatus(int value) {
		int old = status;
		status = value;
		if (!level.isClientSide() && status != old) {
			BlockState iblockstate = level.getBlockState(worldPosition);
			Block block = iblockstate.getBlock();
			if (block instanceof RangeTrigger) {
				BlockState newState = block.defaultBlockState()
						.setValue(FacingHorizontal.FACING, iblockstate.getValue(FacingHorizontal.FACING))
						.setValue(RangeTrigger.STATE, RangeTrigger.EnumState.getState(status));
				level.setBlock(worldPosition, newState, 3);
			}
			notifyBlockUpdate();
		}
	}

	public void setLevelStart(double start) {
		if (!level.isClientSide() && levelStart != start)
			notifyBlockUpdate();
		levelStart = start;
	}

	public void setLevelEnd(double end) {
		if (!level.isClientSide() && levelEnd != end)
			notifyBlockUpdate();
		levelEnd = end;
	}

	public int getStatus() {
		return status;
	}

	public boolean getPowered() {
		return poweredBlock;
	}

	public static void tickStatic(Level level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityRangeTrigger))
			return;
		TileEntityRangeTrigger te = (TileEntityRangeTrigger) be;
		te.tick();
	}

	protected void tick() {
		if (!level.isClientSide()) {
			if (updateTicker-- > 0)
				return;
			updateTicker = tickRate;
			setChanged();
		}
	}

	@Override
	public void onServerMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getIntOr("type", 0)) {
		case 1:
			if (tag.contains("value"))
				setLevelStart(tag.getDoubleOr("value", 0.0));
			break;
		case 2:
			if (tag.contains("value"))
				setInvertRedstone(tag.getIntOr("value", 0) == 1);
			break;
		case 3:
			if (tag.contains("value"))
				setLevelEnd(tag.getDoubleOr("value", 0.0));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	protected void writeUpdateData(ValueOutput tag) {
		tag.putBoolean("poweredBlock", poweredBlock);
	}

	@Override
	protected void readProperties(ValueInput tag) {
		super.readProperties(tag);
		invertRedstone = tag.getBooleanOr("invert", false);
		levelStart = tag.getDoubleOr("levelStart", 0.0);
		levelEnd = tag.getDoubleOr("levelEnd", 0.0);
		poweredBlock = tag.getBooleanOr("poweredBlock", poweredBlock);
	}

	@Override
	protected void writeProperties(ValueOutput tag) {
		super.writeProperties(tag);
		tag.putBoolean("invert", invertRedstone);
		tag.putDouble("levelStart", levelStart);
		tag.putDouble("levelEnd", levelEnd);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level == null || level.isClientSide())
			return;
		
		int status = STATE_UNKNOWN;
		ItemStack card = getItem(SLOT_CARD);
		if (!card.isEmpty()) {
			Item item = card.getItem();
			if (item instanceof ItemCardMain) {
				ItemCardReader reader = new ItemCardReader(card);
				CardState state = ((ItemCardMain) item).updateCardNBT(level, worldPosition, reader, getItem(SLOT_UPGRADE));
				if (state == CardState.OK) {
					double cur = item instanceof ItemCardEnergy ? reader.getDouble(DataHelper.ENERGY) : reader.getLong("amount");
					status = cur > Math.max(levelStart, levelEnd) || cur < Math.min(levelStart, levelEnd) ? STATE_ACTIVE : STATE_PASSIVE;
				} else
					status = STATE_UNKNOWN;
			}
		}
		setStatus(status);
	}

	@Override
	protected void notifyBlockUpdate() {
		BlockState state = level.getBlockState(worldPosition);
		Block block = state.getBlock();
		if (!(block instanceof RangeTrigger))
			return;
		boolean newValue = status >= 1 && (status == 1 != invertRedstone);
		if (poweredBlock != newValue) {
			poweredBlock = newValue;
			level.updateNeighborsAt(worldPosition, block);
		}
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	// Inventory
	@Override
	public int getContainerSize() {
		return 2;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slotIndex, ItemStack stack) { // ISlotItemFilter
		if (slotIndex == SLOT_CARD)
			return stack.getItem() instanceof ItemCardEnergy || stack.getItem() instanceof ItemCardLiquid;
		return stack.getItem().equals(ModItems.upgrade_range);
	}

	// NamedScreenHandlerFactory
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRangeTrigger(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.range_trigger.getDescriptionId());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return worldPosition;
	}
}
