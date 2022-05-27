package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.containers.ContainerEnergyCounter;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.containers.EnergyStorage;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityEnergyCounter extends TileEntityInventory implements ITickableTileEntity, INamedContainerProvider, ITilePacketHandler {
	private EnergyStorage storage = new EnergyStorage(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
	public int total;
	private int rate = -1;
	private boolean powered;

	public TileEntityEnergyCounter(TileEntityType<?> type) {
		super(type);
	}

	public TileEntityEnergyCounter() {
		this(ModTileEntityTypes.energy_counter.get());
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int newValue) {
		if (!level.isClientSide && rate != newValue)
			notifyBlockUpdate();
		rate = newValue;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int newValue) {
		if (!level.isClientSide && total != newValue)
			notifyBlockUpdate();
		total = newValue;
	}

	@Override
	public void onClientMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("value"))
				total = tag.getInt("value");
			break;
		}
	}

	@Override
	public void onServerMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("rate"))
				rate = tag.getInt("rate");
			break;
		case 2:
			if (tag.contains("value"))
				total = tag.getInt("total");
			break;
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		tag = writeProperties(tag);
		calcPowered();
		tag.putBoolean("powered", powered);
		return new SUpdateTileEntityPacket(getBlockPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readProperties(pkt.getTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag = writeProperties(tag);
		calcPowered();
		tag.putBoolean("powered", powered);
		return tag;
	}

	private void calcPowered() { // server
		if (level.hasNeighborSignal(worldPosition) != powered)
			powered = !powered;
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		if (tag.contains("powered") && level.isClientSide)
			powered = tag.getBoolean("powered");
		if (tag.contains("rate"))
			rate = tag.getInt("rate");
		if (tag.contains("total"))
			total = tag.getInt("total");
		if (tag.contains("energy"))
			storage.setEnergy(tag.getInt("energy"));
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putInt("rate", rate);
		tag.putInt("energy", storage.getEnergyStored());
		tag.putInt("total", total);
		return tag;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		return writeProperties(super.save(tag));
	}

	@Override
	public void tick() {
		if (level.isClientSide || powered)
			return;

		if (storage.getEnergyStored() > 0) {
			fillOutput();
			return;
		}

		int amount = rate == -1 ? Integer.MAX_VALUE : rate;
		for (Direction dir : Direction.values())
			if (amount > 0 && dir != getFacing()) {
				IEnergyStorage inputStorage = getSource(dir);
				if (inputStorage != null) {
					int extracted = inputStorage.extractEnergy(amount, false);
					storage.receiveEnergy(extracted, false);
					amount -= extracted;
				}
			}

		if (storage.getEnergyStored() > 0)
			fillOutput();
	}

	private IEnergyStorage getSource(Direction dir) {
		TileEntity te = level.getBlockEntity(worldPosition.relative(dir));
		if (te == null)
			return null;
		return CrossModLoader.getEnergyStorage(te);
	}

	private void fillOutput() {
		IEnergyStorage inputStorage = getSource(getFacing());
		int output = inputStorage.receiveEnergy(storage.getEnergyStored(), false);
		total += output;
		storage.extractEnergy(output, false);
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerEnergyCounter(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.energy_counter.get().getDescriptionId());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY)
			return LazyOptional.of(() -> new EnergyStorage(0, 0, 0, 0)).cast();
		return super.getCapability(cap, side);
	}
}
