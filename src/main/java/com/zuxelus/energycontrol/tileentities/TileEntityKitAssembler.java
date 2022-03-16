package com.zuxelus.energycontrol.tileentities;

import javax.annotation.Nonnull;

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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityKitAssembler extends TileEntityItemHandler implements ITickableTileEntity, INamedContainerProvider, ITilePacketHandler, ISlotItemFilter {
	public static final byte SLOT_INFO = 0;
	public static final byte SLOT_CARD1 = 1;
	public static final byte SLOT_ITEM = 2;
	public static final byte SLOT_CARD2 = 3;
	public static final byte SLOT_RESULT = 4;
	public static final byte SLOT_DISCHARGER = 5;
	private EnergyStorage storage;
	private int buffer;
	private static final int CONSUMPTION = 5;
	private KitAssemblerRecipe recipe;
	private int recipeTime; // client Only
	public static final int CAPACITY = 2000;
	private double production;
	private boolean active;

	public TileEntityKitAssembler(TileEntityType<?> type) {
		super(type);
		storage = new EnergyStorage(CAPACITY, 32, 32, 0);
		active = false;
		production = 0;
	}

	public TileEntityKitAssembler() {
		this(ModTileEntityTypes.kit_assembler.get());
	}

	public double getEnergy() {
		return storage.getEnergyStored();
	}

	public int getEnergyFactor() {
		return (int) Math.round(storage.getEnergyStored() * 52.0F / CAPACITY);
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

	@Override
	public void onServerMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 4:
			if (tag.contains("slot") && tag.contains("title")) {
				ItemStack itemStack = getItem(tag.getInt("slot"));
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemCardMain)
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
			}
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("energy") && tag.contains("production")) {
				storage.setEnergy(tag.getInt("energy"));
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
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readProperties(pkt.getTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag = writeProperties(tag);
		updateActive();
		tag.putBoolean("active", active);
		return tag;
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		if (tag.contains("energy"))
			storage.setEnergy(tag.getInt("energy"));
		if (tag.contains("buffer"))
			buffer = tag.getInt("buffer");
		if (tag.contains("production"))
			production = tag.getDouble("production");
		if (tag.contains("active"))
			active = tag.getBoolean("active");
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putInt("energy", storage.getEnergyStored());
		tag.putInt("buffer", buffer);
		tag.putDouble("production", production);
		return tag;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		return writeProperties(super.save(tag));
	}

	@Override
	public void tick() {
		if (level.isClientSide)
			return;
		handleDischarger(SLOT_DISCHARGER);
		if (!active)
			return;
		if (storage.getEnergyStored() >= CONSUMPTION) {
			storage.extractEnergy(CONSUMPTION, false);
			production += 1;
			if (recipe != null && production >= recipe.time) {
				ItemStack stack1 = getItem(SLOT_CARD1);
				ItemStack stack2 = getItem(SLOT_ITEM);
				ItemStack stack3 = getItem(SLOT_CARD2);
				ItemStack result = getItem(SLOT_RESULT);
				stack1.shrink(recipe.count1);
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
			production = 0;
			updateState();
		}
	}

	private void handleDischarger(int slot) {
		int needed = Math.min(32, storage.getMaxEnergyStored() - storage.getEnergyStored());
		if (needed <= 0)
			return;
		if (buffer > 0)
			buffer -= storage.receiveEnergy(buffer, false);
		needed = Math.min(32, storage.getMaxEnergyStored() - storage.getEnergyStored());
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty() && needed > 0) {
			if (stack.getItem().equals(Items.LAVA_BUCKET)) {
				buffer += 2000;
				buffer -= storage.receiveEnergy(buffer, false);
				setItem(slot, new ItemStack(Items.BUCKET));
				return;
			}
			IEnergyStorage stackStorage = getStackEnergyStorage(stack);
			if (stackStorage != null)
				if (storage.receiveEnergy(stackStorage.extractEnergy(needed, false), false) > 0)
					active = true;
		}
	}

	private IEnergyStorage getStackEnergyStorage(ItemStack stack) {
		LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
		if(cap.isPresent())
			return cap.orElseThrow( NullPointerException::new );
		return null;
	}

	public void notifyBlockUpdate() {
		BlockState iblockstate = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, iblockstate, iblockstate, 2);
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
		if (storage.getEnergyStored() < CONSUMPTION)
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
		return 6;
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
			return getStackEnergyStorage(stack) != null || stack.getItem().equals(Items.LAVA_BUCKET);
		case SLOT_RESULT:
		default:
			return false;
		}
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY)
			return LazyOptional.of(() -> this.storage).cast();
		return super.getCapability(cap, side);
	}

	// ISidedInventory
	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.UP)
			return new int[] { SLOT_CARD1, SLOT_ITEM, SLOT_CARD2 };
		if (side == Direction.DOWN)
			return new int[] { SLOT_RESULT };
		return super.getSlotsForFace(side);
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return side == Direction.UP && (slot == SLOT_CARD1 || slot == SLOT_ITEM || slot == SLOT_CARD2);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return side == Direction.DOWN && slot == SLOT_RESULT;
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerKitAssembler(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.kit_assembler.get().getDescriptionId());
	}
}
