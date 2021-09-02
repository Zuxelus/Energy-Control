package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.KitAssembler;
import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityKitAssembler extends TileEntityInventory implements ITickableTileEntity, INamedContainerProvider, ITilePacketHandler, ISlotItemFilter {
	public static final byte SLOT_INFO = 0;
	public static final byte SLOT_CARD1 = 1;
	public static final byte SLOT_ITEM = 2;
	public static final byte SLOT_CARD2 = 3;
	public static final byte SLOT_KIT = 4;
	private static final double CONSUMPTION = 1;
	private static final double PRODUCTION_TIME = 6000;
	private double maxStorage;
	private double energy;
	private double production;
	private boolean addedToEnet;
	private boolean active;

	public TileEntityKitAssembler(TileEntityType<?> type) {
		super(type);
		addedToEnet = false;
		active = false;
		maxStorage = 1000;
		energy = 0;
		production = 0;
	}

	public TileEntityKitAssembler() {
		this(ModTileEntityTypes.kit_assembler.get());
	}

	public double getEnergy() {
		return energy;
	}

	public int getEnergyFactor() {
		return (int) Math.round(energy / maxStorage * 14);
	}

	public double getProduction() {
		return production;
	}

	public int getProductionFactor() {
		return (int) Math.round(production / PRODUCTION_TIME * 24);
	}

	@Override
	public void onServerMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 4:
			if (tag.contains("slot") && tag.contains("title")) {
				ItemStack itemStack = getStackInSlot(tag.getInt("slot"));
				if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemCardMain) {
					new ItemCardReader(itemStack).setTitle(tag.getString("title"));
				}
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
				energy = tag.getDouble("energy");
				production = tag.getDouble("production");
			}
			break;
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		tag = writeProperties(tag);
		tag.putBoolean("active", active);
		return new SUpdateTileEntityPacket(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readProperties(pkt.getNbtCompound());
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
			energy = tag.getDouble("energy");
		if (tag.contains("production"))
			production = tag.getDouble("production");
		if (tag.contains("active"))
			active = tag.getBoolean("active");
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putDouble("energy", energy);
		tag.putDouble("production", production);
		return tag;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		return writeProperties(super.write(tag));
	}

	@Override
	public void onLoad() {
		/*if (!addedToEnet && !world.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnet = true;
			updateActive();
		}*/
	}

	@Override
	public void remove() {
		onChunkUnloaded();
		super.remove();
	}

	@Override
	public void onChunkUnloaded() {
		/*if (addedToEnet && !world.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}*/
	}

	@Override
	public void tick() {
		if (!addedToEnet)
			onLoad();
		if (world.isRemote || !active)
			return;
		if (energy >= CONSUMPTION) {
			energy -= CONSUMPTION;
			production += 1;
			if (production >= PRODUCTION_TIME) {
				ItemStack stack1 = getStackInSlot(SLOT_CARD1);
				ItemStack stack2 = getStackInSlot(SLOT_CARD2);
				ItemStack kit = getStackInSlot(SLOT_KIT);
				if (!stack1.isEmpty() && stack1.getItem() instanceof ItemCardMain && !stack2.isEmpty()
						&& stack2.getItem() instanceof ItemCardMain
						&& ((ItemCardMain) stack1.getItem()).getKitFromCard() == ((ItemCardMain) stack2.getItem()).getKitFromCard()
						&& kit.isEmpty()) {
					Item kit_item = ((ItemCardMain)stack1.getItem()).getKitFromCard();
					removeStackFromSlot(SLOT_CARD1);
					removeStackFromSlot(SLOT_CARD2);
					if (kit != null)
						setInventorySlotContents(SLOT_KIT, new ItemStack(kit_item));
				}
				production = 0;
				updateState();
			}
		} else {
			energy = 0;
			production = 0;
			updateState();
		}
	}

	public void notifyBlockUpdate() {
		BlockState iblockstate = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (world == null || world.isRemote)
			return;
		updateState();
	}

	private void updateActive() {
		active = false;
		if (energy < CONSUMPTION)
			return;
		ItemStack stack1 = getStackInSlot(SLOT_CARD1);
		if (stack1.isEmpty() || !(stack1.getItem() instanceof ItemCardMain))
			return;
		ItemStack stack2 = getStackInSlot(SLOT_CARD2);
		if (stack2.isEmpty() || !(stack2.getItem() instanceof ItemCardMain)
				|| ((ItemCardMain)stack1.getItem()).getKitFromCard() != ((ItemCardMain)stack2.getItem()).getKitFromCard()
				|| ((ItemCardMain)stack1.getItem()).getKitFromCard() == null)
			return;
		ItemStack kit = getStackInSlot(SLOT_KIT);
		if (!kit.isEmpty())
			return;
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
				.with(KitAssembler.HORIZONTAL_FACING, blockstate.get(KitAssembler.HORIZONTAL_FACING))
				.with(KitAssembler.ACTIVE, active);
		world.setBlockState(pos, newState, 3);
	}

	// ------- Inventory -------
	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD1:
		case SLOT_CARD2:
			return stack.getItem() instanceof ItemCardMain && ((ItemCardMain)stack.getItem()).getKitFromCard() != null;
		case SLOT_INFO:
			return stack.getItem() instanceof ItemCardMain;
		case SLOT_ITEM:
		case SLOT_KIT:
		default:
			return false;
		}
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerKitAssembler(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.kit_assembler.get().getTranslationKey());
	}

	// IEnergySink
	/*@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return Math.max(0, maxStorage - energy);
	}

	@Override
	public int getSinkTier() {
		return 1;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		energy += amount;
		double left = 0.0;

		if (energy > maxStorage) {
			left = energy - maxStorage;
			energy = maxStorage;
		}
		return left;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}*/
}
