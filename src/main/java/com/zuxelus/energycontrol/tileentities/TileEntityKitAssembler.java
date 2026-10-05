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
import com.zuxelus.zlib.tileentities.TileEntityItemHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorageUtil;

public class TileEntityKitAssembler extends TileEntityItemHandler implements ExtendedScreenHandlerFactory, ITilePacketHandler, ISlotItemFilter, SidedInventory {
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
	private static final Identifier TRANSFORMER_UPGRADE = new Identifier("techreborn", "transformer_upgrade");
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
		return getStack(SLOT_TRANSFORMER).getCount();
	}

	// each transformer upgrade doubles speed and consumption and quadruples the input rate
	private long getInputRate() {
		return OUTPUT * (long) Math.pow(4, getUpgrades());
	}

	private long getEnergyNeeded() {
		return CONSUMPTION * (long) Math.pow(2, getUpgrades());
	}

	@Override
	public void onServerMessageReceived(NbtCompound tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 4:
			if (tag.contains("slot") && tag.contains("title")) {
				ItemStack itemStack = getStack(tag.getInt("slot"));
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemCardMain)
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
			}
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NbtCompound tag) {
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
		updateActive();
		tag.putBoolean("active", active);
		return tag;
	}

	@Override
	protected void readProperties(NbtCompound tag) {
		super.readProperties(tag);
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
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		readProperties(tag);
		lastEnergy = storage.getAmount();
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag) {
		tag = super.writeProperties(tag);
		tag.putLong("energy", storage.getAmount());
		tag.putInt("buffer", buffer);
		tag.putDouble("production", production);
		return tag;
	}

	@Override
	protected void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		writeProperties(tag);
	}

	public static void tickStatic(World level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityKitAssembler))
			return;
		TileEntityKitAssembler te = (TileEntityKitAssembler) be;
		te.tick();
	}

	protected void tick() {
		if (world.isClient)
			return;
		// energy also changes from cables, which do not mark the chunk for saving
		if (storage.getAmount() != lastEnergy) {
			lastEnergy = storage.getAmount();
			world.markDirty(pos);
		}
		handleDischarger(SLOT_DISCHARGER);
		long energyNeeded = getEnergyNeeded();
		if (!active) {
			// energy from cables arrives without an inventory change, so check now and then whether work can start
			if (storage.getAmount() >= energyNeeded && world.getTime() % 10 == 0)
				updateState();
			return;
		}
		if (storage.getAmount() >= energyNeeded) {
			storage.extract(energyNeeded, false);
			production += Math.pow(2, getUpgrades());
			if (recipe != null && production >= recipe.time) {
				ItemStack stack1 = getStack(SLOT_CARD1);
				ItemStack stack2 = getStack(SLOT_ITEM);
				ItemStack stack3 = getStack(SLOT_CARD2);
				ItemStack result = getStack(SLOT_RESULT);
				stack1.decrement(recipe.count1);
				if (stack1.getCount() == 0)
					removeStack(SLOT_CARD1);
				stack2.decrement(recipe.count2);
				stack3.decrement(recipe.count3);
				if (result.isEmpty())
					setStack(SLOT_RESULT, recipe.output.copy());
				else
					result.increment(recipe.output.getCount());
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
		ItemStack stack = getStack(slot);
		if (stack.isEmpty() || needed <= 0)
			return;
		if (stack.getItem().equals(Items.LAVA_BUCKET)) {
			buffer += 2000;
			buffer -= (int) storage.insert(Math.min(buffer, needed), false);
			setStack(slot, new ItemStack(Items.BUCKET));
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

	@Override
	public void markDirty() {
		super.markDirty();
		if (world == null || world.isClient)
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

		BlockState blockstate = world.getBlockState(pos);
		Block block = blockstate.getBlock();
		if (!(block instanceof KitAssembler) || blockstate.get(KitAssembler.ACTIVE) == active)
			return;
		BlockState newState = block.getDefaultState()
				.with(KitAssembler.FACING, blockstate.get(KitAssembler.FACING))
				.with(KitAssembler.ACTIVE, active);
		world.setBlockState(pos, newState, 3);
	}

	// ------- Inventory -------
	@Override
	public int size() {
		return 7;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
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
			return Registry.ITEM.getId(stack.getItem()).equals(TRANSFORMER_UPGRADE);
		case SLOT_RESULT:
		default:
			return false;
		}
	}

	// SidedInventory: hoppers fill the recipe inputs from the top and take the result from the bottom
	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP)
			return SLOTS_TOP;
		if (side == Direction.DOWN)
			return SLOTS_BOTTOM;
		return SLOTS_NONE;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction side) {
		return side == Direction.UP && (slot == SLOT_CARD1 || slot == SLOT_ITEM || slot == SLOT_CARD2);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction side) {
		return side == Direction.DOWN && slot == SLOT_RESULT;
	}

	// NamedScreenHandlerFactory
	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerKitAssembler(windowId, inventory, this);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText(ModItems.kit_assembler.getTranslationKey());
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeBlockPos(pos);
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
