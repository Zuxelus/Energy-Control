package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.KitAssembler;
import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;
import com.zuxelus.zlib.containers.EnergyStorage;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.EnergyStorageUtil;

public class TileEntityKitAssembler extends TileEntityInventory implements ExtendedMenuProvider<BlockPos>, ITilePacketHandler, ISlotItemFilter {
	public static final byte SLOT_INFO = 0;
	public static final byte SLOT_CARD1 = 1;
	public static final byte SLOT_ITEM = 2;
	public static final byte SLOT_CARD2 = 3;
	public static final byte SLOT_RESULT = 4;
	public static final byte SLOT_DISCHARGER = 5;
	public static final byte SLOT_TRANSFORMER = 6;
	private static final int[] SLOTS_TOP = { SLOT_CARD1, SLOT_ITEM, SLOT_CARD2 };
	private static final int[] SLOTS_BOTTOM = { SLOT_RESULT };
	private static final int[] SLOTS_NONE = {};
	private static final Identifier TRANSFORMER_UPGRADE = Identifier.fromNamespaceAndPath("techreborn", "transformer_upgrade");
	private EnergyStorage storage;
	// what cables and other mods see: insert only, at the rate set by the transformer upgrades
	private final team.reborn.energy.api.EnergyStorage energyInput = new EnergyInput();
	private int buffer;
	private static final long CONSUMPTION = 5;
	private KitAssemblerRecipe recipe;
	private int recipeTime; // client Only
	public static final long CAPACITY = 5000;
	public static final long OUTPUT = 32;
	private double production;
	private boolean active;
	private long lastEnergy;

	public TileEntityKitAssembler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		// the input rate depends on the upgrades, so it is limited by getInputRate() instead of the storage
		storage = new EnergyStorage(CAPACITY, CAPACITY, CAPACITY, 0);
		active = false;
		production = 0;
	}

	public TileEntityKitAssembler(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.kit_assembler, pos, state);
	}

	public team.reborn.energy.api.EnergyStorage getEnergyInput(Direction side) {
		return energyInput;
	}

	public double getEnergy() {
		return storage.getAmount();
	}

	public int getEnergyFactor() {
		return (int) Math.round(storage.getAmount() * 52.0F / CAPACITY);
	}

	public double getProduction() {
		return production;
	}

	public int getProductionFactor() {
		if (recipeTime == 0)
			return 0;
		return (int) Math.round(production * 24.0F / recipeTime);
	}

	public int getRecipeTime() {
		if (recipe == null)
			return 0;
		return recipe.time;
	}

	private int getUpgrades() {
		return getItem(SLOT_TRANSFORMER).getCount();
	}

	// each transformer upgrade doubles speed and consumption and quadruples the input rate
	private long getInputRate() {
		return OUTPUT * (long) Math.pow(4, getUpgrades());
	}

	private long getEnergyNeeded() {
		return CONSUMPTION * (long) Math.pow(2, getUpgrades());
	}

	@Override
	public void onServerMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getIntOr("type", 0)) {
		case 4:
			if (tag.contains("slot") && tag.contains("title")) {
				ItemStack itemStack = getItem(tag.getIntOr("slot", 0));
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemCardMain)
					new ItemCardReader(itemStack).setTitle(tag.getStringOr("title", ""));
			}
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getIntOr("type", 0)) {
		case 1:
			if (tag.contains("energy") && tag.contains("production")) {
				storage.setEnergy(tag.getLongOr("energy", 0L));
				production = tag.getDoubleOr("production", 0.0);
			}
			if (tag.contains("time"))
				recipeTime = tag.getIntOr("time", 0);
			else
				recipeTime = 0;
			break;
		}
	}

	@Override
	protected void writeUpdateData(ValueOutput tag) {
		updateActive();
		tag.putBoolean("active", active);
	}

	@Override
	protected void readProperties(ValueInput tag) {
		super.readProperties(tag);
		tag.getLong("energy").ifPresent(storage::setEnergy);
		buffer = tag.getIntOr("buffer", buffer);
		production = tag.getDoubleOr("production", production);
		active = tag.getBooleanOr("active", active);
	}

	@Override
	protected void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);
		lastEnergy = storage.getAmount();
	}

	@Override
	protected void writeProperties(ValueOutput tag) {
		super.writeProperties(tag);
		tag.putLong("energy", storage.getAmount());
		tag.putInt("buffer", buffer);
		tag.putDouble("production", production);
	}

	public static void tickStatic(Level level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityKitAssembler))
			return;
		TileEntityKitAssembler te = (TileEntityKitAssembler) be;
		te.tick();
	}

	protected void tick() {
		if (level.isClientSide())
			return;
		// energy also changes from cables, which do not mark the chunk for saving
		if (storage.getAmount() != lastEnergy) {
			lastEnergy = storage.getAmount();
			level.blockEntityChanged(worldPosition);
		}
		handleDischarger(SLOT_DISCHARGER);
		long energyNeeded = getEnergyNeeded();
		if (!active) {
			// energy from cables arrives without an inventory change, so check now and then whether work can start
			if (storage.getAmount() >= energyNeeded && level.getGameTime() % 10 == 0)
				updateState();
			return;
		}
		if (storage.getAmount() >= energyNeeded) {
			storage.extract(energyNeeded, false);
			production += Math.pow(2, getUpgrades());
			if (recipe != null && production >= recipe.time) {
				ItemStack stack1 = getItem(SLOT_CARD1);
				ItemStack stack2 = getItem(SLOT_ITEM);
				ItemStack stack3 = getItem(SLOT_CARD2);
				ItemStack result = getItem(SLOT_RESULT);
				stack1.shrink(recipe.count1);
				if (stack1.getCount() == 0)
					removeItemNoUpdate(SLOT_CARD1);
				stack2.shrink(recipe.count2);
				stack3.shrink(recipe.count3);
				if (result.isEmpty())
					setItem(SLOT_RESULT, recipe.output.create());
				else
					result.grow(recipe.output.count());
				production = 0;
				updateState();
			}
		} else {
			storage.setEnergy(0);
			production = 0;
			updateState();
		}
	}

	private void handleDischarger(int slot) {
		long rate = getInputRate();
		long needed = Math.min(rate, storage.getCapacity() - storage.getAmount());
		if (needed <= 0)
			return;
		if (buffer > 0) {
			long inserted = storage.insert(Math.min(buffer, needed), false);
			buffer -= (int) inserted;
			needed -= inserted;
		}
		ItemStack stack = getItem(slot);
		if (stack.isEmpty() || needed <= 0)
			return;
		if (stack.getItem().equals(Items.LAVA_BUCKET)) {
			buffer += 5000;
			buffer -= (int) storage.insert(Math.min(buffer, needed), false);
			setItem(slot, new ItemStack(Items.BUCKET));
			return;
		}
		// TechReborn batteries and other items with Team Reborn Energy
		team.reborn.energy.api.EnergyStorage itemStorage = team.reborn.energy.api.EnergyStorage.ITEM.find(stack, ContainerItemContext.ofSingleSlot(ContainerStorage.of(this, null).getSlot(slot)));
		if (itemStorage == null)
			return;
		try (Transaction transaction = Transaction.openOuter()) {
			EnergyStorageUtil.move(itemStorage, storage, needed, transaction);
			transaction.commit();
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level == null || level.isClientSide())
			return;
		updateState();
	}

	private void updateActive() {
		active = false;
		if (storage.getAmount() < getEnergyNeeded())
			return;
		KitAssemblerRecipe newRecipe;
		if (recipe == null) {
			newRecipe = KitAssemblerRecipeType.TYPE.findRecipe(this);
			if (newRecipe == null)
				return;
			recipe = newRecipe;
		} else if (!recipe.isSuitable(this)) {
			newRecipe = KitAssemblerRecipeType.TYPE.findRecipe(this);
			if (newRecipe == null) {
				recipe = null;
				return;
			}
			recipe = newRecipe;
		}
		active = true;
	}

	private void updateState() {
		boolean old = active;
		updateActive();
		if (active == old)
			return;

		production = 0;

		BlockState blockstate = level.getBlockState(worldPosition);
		Block block = blockstate.getBlock();
		if (!(block instanceof KitAssembler) || blockstate.getValue(KitAssembler.ACTIVE) == active)
			return;
		BlockState newState = block.defaultBlockState()
				.setValue(KitAssembler.FACING, blockstate.getValue(KitAssembler.FACING))
				.setValue(KitAssembler.ACTIVE, active);
		level.setBlock(worldPosition, newState, 3);
	}

	// ------- Inventory -------
	@Override
	public int getContainerSize() {
		return 7;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return isItemValid(slot, stack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) { // ISlotItemFilter
		switch (slot) {
		case SLOT_CARD1:
		case SLOT_CARD2:
		case SLOT_ITEM:
			return true;
		case SLOT_INFO:
			return stack.getItem() instanceof ItemCardMain;
		case SLOT_DISCHARGER:
			return EnergyStorageUtil.isEnergyStorage(stack) || stack.getItem().equals(Items.LAVA_BUCKET);
		case SLOT_TRANSFORMER:
			return BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(TRANSFORMER_UPGRADE);
		case SLOT_RESULT:
		default:
			return false;
		}
	}

	// SidedInventory: hoppers fill the recipe inputs from the top and take the result from the bottom
	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.UP)
			return SLOTS_TOP;
		if (side == Direction.DOWN)
			return SLOTS_BOTTOM;
		return SLOTS_NONE;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return side == Direction.UP && (slot == SLOT_CARD1 || slot == SLOT_ITEM || slot == SLOT_CARD2);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return side == Direction.DOWN && slot == SLOT_RESULT;
	}

	// NamedScreenHandlerFactory
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerKitAssembler(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.kit_assembler.getDescriptionId());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return worldPosition;
	}

	private class EnergyInput implements team.reborn.energy.api.EnergyStorage {
		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			return storage.insert(Math.min(maxAmount, getInputRate()), transaction);
		}

		@Override
		public boolean supportsExtraction() {
			return false;
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public long getAmount() {
			return storage.getAmount();
		}

		@Override
		public long getCapacity() {
			return storage.getCapacity();
		}
	}
}
