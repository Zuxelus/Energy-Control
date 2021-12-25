package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.ThermalMonitor;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityThermalMonitor extends TileEntityInventory implements ITilePacketHandler {
	private int heatLevel;
	private boolean invertRedstone;
	protected int status;
	private boolean poweredBlock;

	protected int updateTicker;
	protected int tickRate;

	public TileEntityThermalMonitor(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		invertRedstone = false;
		heatLevel = 500;
		updateTicker = 0;
		tickRate = -1;
		status = -1;
	}

	public TileEntityThermalMonitor(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.thermal_monitor, pos, state);
	}

	public int getHeatLevel() {
		return heatLevel;
	}

	public void setHeatLevel(int value) {
		int old = heatLevel;
		heatLevel = value;
		if (!world.isClient && heatLevel != old)
			notifyBlockUpdate();
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int newStatus) {
		status = newStatus;
	}

	public boolean getPowered() {
		return poweredBlock;
	}

	@Override
	public void onServerMessageReceived(NbtCompound tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("value"))
				setHeatLevel(tag.getInt("value"));
			break;
		case 2:
			if (tag.contains("value"))
				setInvertRedstone(tag.getInt("value") == 1);
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
		tag.putInt("status", status);
		tag.putBoolean("poweredBlock", poweredBlock);
		return tag;
	}

	@Override
	protected void readProperties(NbtCompound tag) {
		super.readProperties(tag);
		if (tag.contains("heatLevel"))
			heatLevel = tag.getInt("heatLevel");
		if (tag.contains("invert"))
			invertRedstone = tag.getBoolean("invert");
		if (tag.contains("status"))
			setStatus(tag.getInt("status"));
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
		tag.putInt("heatLevel", heatLevel);
		tag.putBoolean("invert", invertRedstone);
		return tag;
	}

	@Override
	protected void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		writeProperties(tag);
	}

	@Override
	public void markRemoved() {
		world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock());
		super.markRemoved();
	}

	public static void tickStatic(World level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityThermalMonitor))
			return;
		TileEntityThermalMonitor te = (TileEntityThermalMonitor) be;
		te.tick();
	}

	protected void tick() {
		if (world.isClient)
			return;
	
		if (updateTicker-- > 0)
				return;
		updateTicker = tickRate;
		checkStatus();
	}

	protected void checkStatus() {
		int heat = CrossModLoader.getReactorHeat(world, pos);
		int newStatus = heat == -1 ? -2 : heat >= heatLevel ? 1 : 0;

		if (newStatus != status) {
			status = newStatus;
			notifyBlockUpdate();
			world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock());
		}
	}

	@Override
	protected void notifyBlockUpdate() {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof ThermalMonitor /*|| block instanceof RemoteThermo*/) { // TODO
			boolean newValue = status < 0 ? false : status == 1 ? !invertRedstone : invertRedstone;
			if (poweredBlock != newValue) {
				poweredBlock = newValue;
				world.updateNeighborsAlways(pos, block);
			}
			world.updateListeners(pos, state, state, 2);
		}
	}

	@Override
	protected boolean hasRotation() {
		return true;
	}

	// ------- Inventory ------- 
	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
		return false;
	}
}
