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

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityRangeTrigger extends TileEntityInventory implements ExtendedScreenHandlerFactory, ISlotItemFilter, ITilePacketHandler {
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
		if (!world.isClient && invertRedstone != old)
			notifyBlockUpdate();
	}

	public void setStatus(int value) {
		int old = status;
		status = value;
		if (!world.isClient && status != old) {
			BlockState iblockstate = world.getBlockState(pos);
			Block block = iblockstate.getBlock();
			if (block instanceof RangeTrigger) {
				BlockState newState = block.getDefaultState()
						.with(FacingHorizontal.FACING, iblockstate.get(FacingHorizontal.FACING))
						.with(RangeTrigger.STATE, RangeTrigger.EnumState.getState(status));
				world.setBlockState(pos, newState, 3);
			}
			notifyBlockUpdate();
		}
	}

	public void setLevelStart(double start) {
		if (!world.isClient && levelStart != start)
			notifyBlockUpdate();
		levelStart = start;
	}

	public void setLevelEnd(double end) {
		if (!world.isClient && levelEnd != end)
			notifyBlockUpdate();
		levelEnd = end;
	}

	public int getStatus() {
		return status;
	}

	public boolean getPowered() {
		return poweredBlock;
	}

	public static void tickStatic(World level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityRangeTrigger))
			return;
		TileEntityRangeTrigger te = (TileEntityRangeTrigger) be;
		te.tick();
	}

	protected void tick() {
		if (!world.isClient) {
			if (updateTicker-- > 0)
				return;
			updateTicker = tickRate;
			markDirty();
		}
	}

	@Override
	public void onServerMessageReceived(NbtCompound tag) {
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
	public void onClientMessageReceived(NbtCompound tag) { }

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public void onDataPacket(BlockEntityUpdateS2CPacket pkt) {
		readProperties(pkt.getNbt());
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound tag = super.toInitialChunkDataNbt();
		tag = writeProperties(tag);
		tag.putBoolean("poweredBlock", poweredBlock);
		return tag;
	}

	@Override
	protected void readProperties(NbtCompound tag) {
		super.readProperties(tag);
		invertRedstone = tag.getBoolean("invert");
		levelStart = tag.getDouble("levelStart");
		levelEnd = tag.getDouble("levelEnd");
		if (tag.contains("poweredBlock"))
			poweredBlock = tag.getBoolean("poweredBlock");
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		readProperties(tag);
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag) {
		tag = super.writeProperties(tag);
		tag.putBoolean("invert", invertRedstone);
		tag.putDouble("levelStart", levelStart);
		tag.putDouble("levelEnd", levelEnd);
		return tag;
	}

	@Override
	protected void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		writeProperties(tag);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (world == null || world.isClient)
			return;
		
		int status = STATE_UNKNOWN;
		ItemStack card = getStack(SLOT_CARD);
		if (!card.isEmpty()) {
			Item item = card.getItem();
			if (item instanceof ItemCardMain) {
				ItemCardReader reader = new ItemCardReader(card);
				CardState state = ((ItemCardMain) item).updateCardNBT(world, pos, reader, getStack(SLOT_UPGRADE));
				if (state == CardState.OK) {
					double cur = item instanceof ItemCardEnergy ? reader.getDouble("storage") :  reader.getLong("amount");
					status = cur > Math.max(levelStart, levelEnd) || cur < Math.min(levelStart, levelEnd) ? STATE_ACTIVE : STATE_PASSIVE;
				} else
					status = STATE_UNKNOWN;
			}
		}
		setStatus(status);
	}

	@Override
	protected void notifyBlockUpdate() {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (!(block instanceof RangeTrigger))
			return;
		boolean newValue = status >= 1 && (status == 1 != invertRedstone);
		if (poweredBlock != newValue) {
			poweredBlock = newValue;
			world.updateNeighborsAlways(pos, block);
		}
		world.updateListeners(pos, state, state, 2);
	}

	// Inventory
	@Override
	public int size() {
		return 2;
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
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
	public ScreenHandler createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerRangeTrigger(windowId, inventory, this);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText(ModItems.range_trigger.getTranslationKey());
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}
}
