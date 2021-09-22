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
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityRangeTrigger extends TileEntityInventory implements MenuProvider, ISlotItemFilter, ITilePacketHandler {
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
		tickRate = ConfigHandler.RANGE_TRIGGER_REFRESH_PERIOD.get();
		updateTicker = tickRate;
		status = -1;
		invertRedstone = false;
		levelStart = 0;
		levelEnd = 40000;
	}

	public TileEntityRangeTrigger(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.range_trigger.get(), pos, state);
	}

	public boolean getInvertRedstone() {
		return invertRedstone;
	}

	public void setInvertRedstone(boolean value) {
		boolean old = invertRedstone;
		invertRedstone = value;
		if (!level.isClientSide && invertRedstone != old)
			notifyBlockUpdate();
	}

	public void setStatus(int value) {
		int old = status;
		status = value;
		if (!level.isClientSide && status != old) {
			BlockState iblockstate = level.getBlockState(worldPosition);
			Block block = iblockstate.getBlock();
			if (block instanceof RangeTrigger) {
				BlockState newState = block.defaultBlockState()
						.setValue(HorizontalDirectionalBlock.FACING, iblockstate.getValue(HorizontalDirectionalBlock.FACING))
						.setValue(RangeTrigger.STATE, RangeTrigger.EnumState.getState(status));
				level.setBlock(worldPosition, newState, 3);
			}
			notifyBlockUpdate();
		}
	}

	public void setLevelStart(double start) {
		if (!level.isClientSide && levelStart != start)
			notifyBlockUpdate();
		levelStart = start;
	}

	public void setLevelEnd(double end) {
		if (!level.isClientSide && levelEnd != end)
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
		if (!level.isClientSide) {
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
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("value"))
				setLevelStart(tag.getDouble("value"));
			break;
		case 2:
			if (tag.contains("value"))
				setInvertRedstone(tag.getInt("value") == 1);
			break;
		case 3:
			if (tag.contains("value"))
				setLevelEnd(tag.getDouble("value"));
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		tag = writeProperties(tag);
		tag.putBoolean("poweredBlock", poweredBlock);
		return new ClientboundBlockEntityDataPacket(getBlockPos(), 0, tag);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		readProperties(pkt.getTag());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag = writeProperties(tag);
		tag.putBoolean("poweredBlock", poweredBlock);
		return tag;
	}

	@Override
	protected void readProperties(CompoundTag tag) {
		super.readProperties(tag);
		invertRedstone = tag.getBoolean("invert");
		levelStart = tag.getDouble("levelStart");
		levelEnd = tag.getDouble("levelEnd");
		if (tag.contains("poweredBlock"))
			poweredBlock = tag.getBoolean("poweredBlock");
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		readProperties(tag);
	}

	@Override
	protected CompoundTag writeProperties(CompoundTag tag) {
		tag = super.writeProperties(tag);
		tag.putBoolean("invert", invertRedstone);
		tag.putDouble("levelStart", levelStart);
		tag.putDouble("levelEnd", levelEnd);
		return tag;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		return writeProperties(super.save(tag));
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level == null || level.isClientSide)
			return;
		
		int status = STATE_UNKNOWN;
		ItemStack card = getItem(SLOT_CARD);
		if (!card.isEmpty()) {
			Item item = card.getItem();
			if (item instanceof ItemCardMain) {
				ItemCardReader reader = new ItemCardReader(card);
				CardState state = ((ItemCardMain) item).updateCardNBT(level, worldPosition, reader, getItem(SLOT_UPGRADE));
				if (state == CardState.OK) {
					double cur = item instanceof ItemCardEnergy ? reader.getDouble("storage") :  reader.getLong("amount");
					status = cur > Math.max(levelStart, levelEnd) || cur < Math.min(levelStart, levelEnd) ? STATE_ACTIVE : STATE_PASSIVE;
				} else
					status = STATE_UNKNOWN;
			}
		}
		setStatus(status);
	}

	public void notifyBlockUpdate() {
		BlockState iblockstate = level.getBlockState(worldPosition);
		Block block = iblockstate.getBlock();
		if (!(block instanceof RangeTrigger))
			return;
		boolean newValue = status >= 1 && (status == 1 != invertRedstone);
		if (poweredBlock != newValue) {
			poweredBlock = newValue;
			level.updateNeighborsAt(worldPosition, block);
		}
		level.sendBlockUpdated(worldPosition, iblockstate, iblockstate, 2);
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
		return stack.getItem().equals(ModItems.upgrade_range.get());
	}

	// MenuProvider
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRangeTrigger(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent(ModItems.range_trigger.get().getDescriptionId());
	}
}
