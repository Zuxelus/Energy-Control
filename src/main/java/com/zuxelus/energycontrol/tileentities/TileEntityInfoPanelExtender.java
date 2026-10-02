package com.zuxelus.energycontrol.tileentities;

import net.minecraft.core.HolderLookup;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.blocks.FacingHorizontalActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileEntityInfoPanelExtender extends BlockEntityFacing implements IScreenPart {
	protected boolean init;

	protected Screen screen;
	private boolean partOfScreen;

	private int coreX;
	private int coreY;
	private int coreZ;

	public TileEntityInfoPanelExtender(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		init = false;
		screen = null;
		partOfScreen = false;
		coreX = 0;
		coreY = 0;
		coreZ = 0;
	}

	public TileEntityInfoPanelExtender(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.info_panel_extender.get(), pos, state);
	}

	@Override
	public void setFacing(int meta) {
		Direction newFacing = Direction.from3DDataValue(meta);
		if (facing == newFacing)
			return;
		facing = newFacing;
		if (init) {
			EnergyControl.INSTANCE.screenManager.unregisterScreenPart(this);
			EnergyControl.INSTANCE.screenManager.registerInfoPanelExtender(this);
		}
	}

	private void updateScreen() {
		if (partOfScreen && screen == null) {
			BlockEntity core = level.getBlockEntity(new BlockPos(coreX, coreY, coreZ));
			if (core != null && core instanceof TileEntityInfoPanel) {
				screen = ((TileEntityInfoPanel) core).getScreen();
				if (screen != null)
					screen.init(true, level);
			}
		}
		if (level.isClientSide && !partOfScreen && screen != null)
			setScreen(null);
	}

	@Override
	protected void readProperties(CompoundTag tag, HolderLookup.Provider registries) {
		super.readProperties(tag, registries);
		partOfScreen = (tag.contains("partOfScreen") ? tag.getBoolean("partOfScreen") : partOfScreen);
		coreX = (tag.contains("coreX") ? tag.getInt("coreX") : coreX);
		coreY = (tag.contains("coreY") ? tag.getInt("coreY") : coreY);
		coreZ = (tag.contains("coreZ") ? tag.getInt("coreZ") : coreZ);
		if (level != null) {
			updateScreen();
			if (level.isClientSide)
				level.getChunkSource().getLightEngine().checkBlock(worldPosition);
		}
	}

	@Override
	protected void writeProperties(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeProperties(tag, registries);
		tag.putBoolean("partOfScreen", partOfScreen);
		tag.putInt("coreX", coreX);
		tag.putInt("coreY", coreY);
		tag.putInt("coreZ", coreZ);
	}

	private boolean chunkUnloaded;

	@Override
	public void onChunkUnloaded() {
		// Unloading preserves the saved screen. Rebuilding it here would reload chunks
		// while ChunkMap is trying to unload them, including during server shutdown.
		chunkUnloaded = true;
		if (level != null && !level.isClientSide)
			EnergyControl.INSTANCE.screenManager.unloadScreenPart(this);
		super.onChunkUnloaded();
	}

	@Override
	public void setRemoved() {
		if (!chunkUnloaded && level != null && !level.isClientSide)
			EnergyControl.INSTANCE.screenManager.unregisterScreenPart(this);
		super.setRemoved();
	}

	public static void tickStatic(Level level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityInfoPanelExtender))
			return;
		TileEntityInfoPanelExtender te = (TileEntityInfoPanelExtender) be;
		te.tick();
	}

	protected void tick() {
		if (init)
			return;

		if (!level.isClientSide && !partOfScreen)
			EnergyControl.INSTANCE.screenManager.registerInfoPanelExtender(this);

		updateScreen();
		init = true;
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
		if (screen != null) {
			partOfScreen = true;
			TileEntityInfoPanel core = screen.getCore(level);
			if (core != null) {
				coreX = core.getBlockPos().getX();
				coreY = core.getBlockPos().getY();
				coreZ = core.getBlockPos().getZ();

				BlockState stateCore = level.getBlockState(core.getBlockPos());
				if (stateCore.getBlock() instanceof FacingBlockActive || stateCore.getBlock() instanceof FacingHorizontalActive) {
					BlockState state = level.getBlockState(worldPosition);
					if (state.getValue(FacingBlockActive.ACTIVE) != stateCore.getValue(FacingBlockActive.ACTIVE))
						level.setBlock(worldPosition, state.cycle(FacingBlockActive.ACTIVE), 2);
					return;
				}
			}
		} else {
			BlockState state = level.getBlockState(worldPosition);
			if (state.getValue(FacingBlockActive.ACTIVE))
				level.setBlock(worldPosition, state.setValue(FacingBlockActive.ACTIVE, false), 2);
		}
		partOfScreen = false;
		coreX = 0;
		coreY = 0;
		coreZ = 0;
	}

	@Override
	public Screen getScreen() {
		return screen;
	}

	public TileEntityInfoPanel getCore() {
		if (screen == null)
			return null;
		return screen.getCore(level);
	}

	@Override
	public void updateData() { }

	@Override
	public void updateTileEntity() {
		notifyBlockUpdate();
	}

	public boolean getColored() {
		if (screen == null)
			return false;
		TileEntityInfoPanel core = screen.getCore(level);
		if (core == null)
			return false;
		return core.getColored();
	}

	public boolean getPowered() {
		if (screen == null)
			return false;
		TileEntityInfoPanel core = screen.getCore(level);
		if (core == null)
			return false;
		return core.getPowered();
	}

	public int getColorBackground() {
		if (screen == null)
			return 2;
		TileEntityInfoPanel core = screen.getCore(level);
		if (core == null)
			return 2;
		return core.getColorBackground();
	}

	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition);
	}

	public int findTexture() {
		Screen scr = getScreen();
		if (scr != null) {
			BlockPos pos = getBlockPos();
			switch (getFacing()) {
			case UP:
				switch (getRotation()) {
				case NORTH:
					return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ);
				case SOUTH:
					return 2 * boolToInt(pos.getX() == scr.minX) + 1 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getZ() == scr.minZ) + 8 * boolToInt(pos.getZ() == scr.maxZ);
				case WEST:
					return 8 * boolToInt(pos.getX() == scr.minX) + 4 * boolToInt(pos.getX() == scr.maxX) + 2 * boolToInt(pos.getZ() == scr.minZ) + 1 * boolToInt(pos.getZ() == scr.maxZ);
				case EAST:
					return 4 * boolToInt(pos.getX() == scr.minX) + 8 * boolToInt(pos.getX() == scr.maxX) + 1 * boolToInt(pos.getZ() == scr.minZ) + 2 * boolToInt(pos.getZ() == scr.maxZ);
				default:
					break;
				}
				break;
			case DOWN:
				switch (getRotation()) {
				case NORTH:
					return 2 * boolToInt(pos.getX() == scr.minX) + 1 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ);
				case SOUTH:
					return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getZ() == scr.minZ) + 8 * boolToInt(pos.getZ() == scr.maxZ);
				case WEST:
					return 8 * boolToInt(pos.getX() == scr.minX) + 4 * boolToInt(pos.getX() == scr.maxX) + 1 * boolToInt(pos.getZ() == scr.minZ) + 2 * boolToInt(pos.getZ() == scr.maxZ);
				case EAST:
					return 4 * boolToInt(pos.getX() == scr.minX) + 8 * boolToInt(pos.getX() == scr.maxX) + 2 * boolToInt(pos.getZ() == scr.minZ) + 1 * boolToInt(pos.getZ() == scr.maxZ);
				default:
					break;
				}
				break;
			case SOUTH:
				return 2 * boolToInt(pos.getX() == scr.minX) + 1 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case WEST:
				return 2 * boolToInt(pos.getZ() == scr.minZ) + 1 * boolToInt(pos.getZ() == scr.maxZ) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case EAST:
				return 1 * boolToInt(pos.getZ() == scr.minZ) + 2 * boolToInt(pos.getZ() == scr.maxZ) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case NORTH:
				return boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			}
		}
		return 15;
	}

	private int boolToInt(boolean b) {
		return b ? 1 : 0;
	}
}