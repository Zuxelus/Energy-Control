package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.api.IItemCard;
import com.zuxelus.energycontrol.blocks.KitAssembler;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid = ModIDs.IC2, iface = "ic2.api.energy.tile.IEnergySink")
public class TileEntityKitAssembler extends TileEntityInventory implements ITickable, ITilePacketHandler, ISlotItemFilter, IEnergySink {
	public static final byte SLOT_INFO = 0;
	public static final byte SLOT_CARD1 = 1;
	public static final byte SLOT_ITEM = 2;
	public static final byte SLOT_CARD2 = 3;
	public static final byte SLOT_KIT = 4;
	private static final double CONSUMPTION = 1;
	private static final double PRODUCTION_TIME = 6000;
	private final double maxStorage;
	private double energy;
	private double production;
	private boolean addedToEnet;
	private boolean active;

	public TileEntityKitAssembler() {
		super("tile.kit_assembler.name");
		addedToEnet = false;
		active = false;
		maxStorage = 1000;
		energy = 0;
		production = 0;
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
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
        if (tag.getInteger("type") == 4) {
            if (tag.hasKey("slot") && tag.hasKey("title")) {
                ItemStack stack = getStackInSlot(tag.getInteger("slot"));
                if (ItemCardMain.isCard(stack)) {
                    new ItemCardReader(stack).setTitle(tag.getString("title"));
                }
            }
        }
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
        if (tag.getInteger("type") == 1) {
            if (tag.hasKey("energy") && tag.hasKey("production")) {
                energy = tag.getDouble("energy");
                production = tag.getDouble("production");
            }
        }
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setBoolean("active", active);
		return new SPacketUpdateTileEntity(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag = writeProperties(tag);
		updateActive();
		tag.setBoolean("active", active);
		return tag;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("energy"))
			energy = tag.getDouble("energy");
		if (tag.hasKey("production"))
			production = tag.getDouble("production");
		if (tag.hasKey("active"))
			active = tag.getBoolean("active");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setDouble("energy", energy);
		tag.setDouble("production", production);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	@Optional.Method(modid = ModIDs.IC2)
	public void onLoad() {
		if (!addedToEnet && !world.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnet = true;
			updateActive();
		}
	}

	@Override
	public void invalidate() {
		onChunkUnload();
		super.invalidate();
	}

	@Override
	@Optional.Method(modid = ModIDs.IC2)
	public void onChunkUnload() {
		if (addedToEnet && !world.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	@Override
	public void update() {
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
				if (ItemCardMain.isCard(stack1) && ItemCardMain.isCard(stack2) && stack1.isItemEqual(stack2) && getStackInSlot(SLOT_KIT).isEmpty()) {
					ItemStack kit = ((IItemCard) stack1.getItem()).getKitFromCard(stack1).copy();
					kit.setCount(2);
					removeStackFromSlot(SLOT_CARD1);
					removeStackFromSlot(SLOT_CARD2);
					setInventorySlotContents(SLOT_KIT, kit);
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
		IBlockState iblockstate = world.getBlockState(pos);
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
		
		ItemStack stack1 = getStackInSlot(SLOT_CARD1);
		ItemStack stack2 = getStackInSlot(SLOT_CARD2);
		if (energy >= CONSUMPTION && ItemCardMain.isCardWithKit(stack1) && ItemCardMain.isCard(stack2) && stack1.isItemEqual(stack2) && getStackInSlot(SLOT_KIT).isEmpty()) 
			active = true;
	}

	private void updateState() {
		boolean old = active;
		updateActive();
		if (active == old)
			return;

		production = 0;

		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (!(block instanceof KitAssembler) || iblockstate.getValue(KitAssembler.ACTIVE) == active)
			return;
		IBlockState newState = block.getDefaultState()
				.withProperty(KitAssembler.FACING, iblockstate.getValue(KitAssembler.FACING))
				.withProperty(KitAssembler.ACTIVE, active);
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
			return ItemCardMain.isCardWithKit(stack);
		case SLOT_INFO:
			return ItemCardMain.isCard(stack);
		case SLOT_ITEM:
		case SLOT_KIT:
		default:
			return false;
		}
	}

	// IEnergySink
	@Override
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
	}
}
