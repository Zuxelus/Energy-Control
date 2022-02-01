package com.zuxelus.energycontrol.tileentities;

import javax.annotation.Nonnull;

import com.zuxelus.energycontrol.containers.ContainerFluidControlValve;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileEntityFluidControlValve extends TileEntityFacing implements ITickableTileEntity, INamedContainerProvider, ITilePacketHandler {
	private FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 16);
	private FluidStack inputStack;
	private IFluidTank outputTank;
	public int total;
	private int speed = 20;
	private double counter;
	private boolean powered;
	public boolean updated;

	public TileEntityFluidControlValve(TileEntityType<?> type) {
		super(type);
	}

	public TileEntityFluidControlValve() {
		this(ModTileEntityTypes.fluid_control_valve.get());
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int newValue) {
		if (!level.isClientSide && speed != newValue)
			notifyBlockUpdate();
		speed = newValue;
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
			if (tag.contains("value"))
				speed = tag.getInt("value");
			break;
		case 2:
			if (tag.contains("value"))
				total = tag.getInt("total");
			counter = 0;
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
		if (tag.contains("speed"))
			speed = tag.getInt("speed");
		if (tag.contains("FluidName"))
			tank.readFromNBT(tag);
		if (tag.contains("total"))
			total = tag.getInt("total");
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putInt("speed", speed);
		tag.putInt("total", total);
		return tag;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tank.writeToNBT(tag);
		return writeProperties(tag);
	}

	@Override
	public void tick() {
		if (level.isClientSide || powered)
			return;

		if (!updated) {
			BlockPos pos = worldPosition.west();
			if (facing == Direction.NORTH)
				pos = worldPosition.east();
			if (facing == Direction.EAST)
				pos = worldPosition.south();
			if (facing == Direction.WEST)
				pos = worldPosition.north();
			TileEntity te = level.getBlockEntity(pos);
			if (te != null) {
				IFluidTank input = CrossModLoader.getPipeTank(te);
				if (input != null)
					inputStack = input.getFluid();
				/*Optional<IFluidHandler> fluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).resolve();
				if (fluid.isPresent()) {
					IFluidHandler handler = fluid.get();
					inputStack = handler.getFluidInTank(0);
				}*/
			}
			pos = worldPosition.east();
			if (facing == Direction.NORTH)
				pos = worldPosition.west();
			if (facing == Direction.EAST)
				pos = worldPosition.north();
			if (facing == Direction.WEST)
				pos = worldPosition.south();
			te = level.getBlockEntity(pos);
			if (te != null)
				outputTank = CrossModLoader.getPipeTank(te);
			updated = false;
		}

		if (inputStack == null || outputTank == null)
			return;

		int currentAmount = tank.getFluidAmount();
		if (currentAmount > 0) {
			fillOutput(currentAmount);
			return;
		}

		int inputAmount = inputStack.getAmount();
		if (inputAmount > 0) {
			counter += speed / 20.0D;
			if (counter >= 1) {
				int amount = Math.min((int) counter, inputAmount);
				FluidStack stack = inputStack.copy();
				stack.setAmount(amount);
				tank.fill(stack, FluidAction.EXECUTE);
				inputStack.shrink(amount);
				counter = counter - amount;
				fillOutput(amount);
			}
		}
	}

	private void fillOutput(int currentAmount) {
		int outputAmount = Math.min(currentAmount, outputTank.getCapacity() - outputTank.getFluidAmount());
		if (outputAmount > 0) {
			FluidStack stack = tank.drain(outputAmount, FluidAction.EXECUTE);
			total += outputTank.fill(stack, FluidAction.EXECUTE);
			if (tank.getFluidAmount() == 0)
				tank.setFluid(FluidStack.EMPTY);
		}
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerFluidControlValve(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.fluid_control_valve.get().getDescriptionId());
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return LazyOptional.of(() -> new FluidTank(0)).cast();
		return super.getCapability(cap, side);
	}
}
