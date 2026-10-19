package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.KitAssembler;
import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;
import com.zuxelus.zlib.containers.EnergyStorage;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.level.material.Fluids;
import team.reborn.energy.api.EnergyStorageUtil;

public class TileEntityKitAssembler extends TileEntityInventory implements ExtendedScreenHandlerFactory<BlockPos>, ITilePacketHandler, ISlotItemFilter {
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
	private static final ResourceLocation TRANSFORMER_UPGRADE = ResourceLocation.fromNamespaceAndPath("techreborn", "transformer_upgrade");
	private EnergyStorage storage;
	private int buffer;
	private static final long CONSUMPTION = 5;
	private KitAssemblerRecipe recipe;
	private int recipeTime; // client Only
	public static final long CAPACITY = 5000;
	public static final long OUTPUT = 32;
	private static final int LAVA_ENERGY = 5000; // per bucket of lava
	private double production;
	private boolean active;

	public TileEntityKitAssembler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		// what cables and other mods see: insert only, at the rate set by the transformer upgrades
		storage = new EnergyStorage(CAPACITY, CAPACITY, CAPACITY, 0) {
			@Override
			public long insert(long maxAmount, TransactionContext transaction) {
				return super.insert(Math.min(maxAmount, getInputRate()), transaction);
			}

			// cables and batteries change the energy through transactions, which do not mark the chunk for saving
			@Override
			protected void onFinalCommit() {
				markEnergyChanged();
			}

			@Override
			public boolean supportsExtraction() {
				return false;
			}

			@Override
			public long extract(long maxAmount, TransactionContext transaction) {
				return 0;
			}
		};
		active = false;
		production = 0;
	}

	public TileEntityKitAssembler(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.kit_assembler, pos, state);
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
		switch (tag.getInt("type")) {
		case 4:
			if (tag.contains("slot") && tag.contains("title")) {
				ItemStack itemStack = getItem(tag.getInt("slot"));
				if (ItemCardMain.isCard(itemStack))
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
			}
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("energy") && tag.contains("production")) {
				storage.setEnergy(tag.getLong("energy"));
				production = tag.getDouble("production");
			}
			if (tag.contains("time"))
				recipeTime = tag.getInt("time");
			else
				recipeTime = 0;
			break;
		}
	}

	@Override
	protected void writeUpdateData(CompoundTag tag, HolderLookup.Provider registries) {
		updateActive();
		tag.putBoolean("active", active);
	}

	@Override
	protected void readProperties(CompoundTag tag, HolderLookup.Provider registries) {
		super.readProperties(tag, registries);
		if (tag.contains("energy"))
			storage.setEnergy(tag.getLong("energy"));
		if (tag.contains("buffer"))
			buffer = tag.getInt("buffer");
		if (tag.contains("production"))
			production = tag.getDouble("production");
		if (tag.contains("active"))
			active = tag.getBoolean("active");
	}

	@Override
	protected void writeProperties(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeProperties(tag, registries);
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
		if (level.isClientSide)
			return;
		handleDischarger(SLOT_DISCHARGER);
		long energyNeeded = getEnergyNeeded();
		if (!active) {
			// energy from cables arrives without an inventory change, so check now and then whether work can start
			if (storage.getAmount() >= energyNeeded && level.getGameTime() % 10 == 0)
				updateState();
			return;
		}
		if (storage.getAmount() >= energyNeeded) {
			storage.consume(energyNeeded);
			markEnergyChanged(); // fabric only
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
					setItem(SLOT_RESULT, recipe.output.copy());
				else
					result.grow(recipe.output.getCount());
				production = 0;
				updateState();
			}
		} else {
			storage.setEnergy(0);
			markEnergyChanged(); // fabric only
			production = 0;
			updateState();
		}
	}

	private void handleDischarger(int slot) {
		long needed = Math.min(getInputRate(), storage.getCapacity() - storage.getAmount());
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
			buffer += LAVA_ENERGY;
			buffer -= (int) storage.insert(Math.min(buffer, needed), false);
			setItem(slot, new ItemStack(Items.BUCKET));
			return;
		}
		// TechReborn Lava Cell and any other item holding a bucket of lava (Fabric Transfer API)
		if (burnLava(slot, stack)) {
			buffer -= (int) storage.insert(Math.min(buffer, needed), false);
			return;
		}
		// TechReborn batteries and other items with Team Reborn Energy
		team.reborn.energy.api.EnergyStorage itemStorage = team.reborn.energy.api.EnergyStorage.ITEM.find(stack, ContainerItemContext.ofSingleSlot(InventoryStorage.of(this, null).getSlot(slot)));
		if (itemStorage == null)
			return;
		try (Transaction transaction = Transaction.openOuter()) {
			EnergyStorageUtil.move(itemStorage, storage, needed, transaction);
			transaction.commit();
		}
	}

	// takes a bucket of lava out of the item in the slot, the item keeps its other contents (an empty cell stays in the slot).
	// The item is emptied in a temporary container: the real slot would check the emptied item with isItemValid() in the middle of the transaction
	private boolean burnLava(int slot, ItemStack stack) {
		if (stack.getCount() != 1) // the emptied item could not stay in the slot next to the rest of the stack
			return false;
		SimpleContainer temp = new SimpleContainer(stack.copy());
		Storage<FluidVariant> fluids = FluidStorage.ITEM.find(temp.getItem(0), ContainerItemContext.ofSingleSlot(InventoryStorage.of(temp, null).getSlot(0)));
		if (fluids == null)
			return false;
		try (Transaction transaction = Transaction.openOuter()) {
			if (fluids.extract(FluidVariant.of(Fluids.LAVA), FluidConstants.BUCKET, transaction) != FluidConstants.BUCKET)
				return false;
			transaction.commit();
		}
		setItem(slot, temp.getItem(0));
		buffer += LAVA_ENERGY;
		return true;
	}

	// isItemValid() is also called by the transfer api in the middle of other transactions, so the check joins the current one
	@SuppressWarnings("deprecation") // getCurrentUnsafe() is meant for exactly this: code that cannot know whether a transaction is open
	private static boolean isLavaContainer(ItemStack stack) {
		Storage<FluidVariant> fluids = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
		if (fluids == null)
			return false;
		try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
			return fluids.extract(FluidVariant.of(Fluids.LAVA), FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET;
		}
	}

	// each transformer upgrade doubles speed and consumption and quadruples the input rate
	private void markEnergyChanged() { // fabric only
		if (level != null && !level.isClientSide)
			level.blockEntityChanged(worldPosition);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level == null || level.isClientSide)
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
			return ItemCardMain.isCard(stack);
		case SLOT_DISCHARGER:
			return CrossModLoader.isElectricItem(stack) || stack.getItem().equals(Items.LAVA_BUCKET) || isLavaContainer(stack);
		case SLOT_TRANSFORMER:
			return BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(TRANSFORMER_UPGRADE);
		case SLOT_RESULT:
		default:
			return false;
		}
	}

	public team.reborn.energy.api.EnergyStorage getEnergyInput(Direction side) {
		return storage;
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

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerKitAssembler(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.kit_assembler.getDescriptionId());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) { // fabric only
		return worldPosition;
	}
}
