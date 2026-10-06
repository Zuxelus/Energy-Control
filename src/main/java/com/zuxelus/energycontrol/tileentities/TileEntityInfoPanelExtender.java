package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.renderers.RotationOffset;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileEntityInfoPanelExtender extends BlockEntityFacing implements IScreenPart, ITilePacketHandler {
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
		this(ModTileEntityTypes.info_panel_extender, pos, state);
	}

	@Override
	public void setFacing(int meta) {
		Direction newFacing = Direction.from3DDataValue(meta);
		if (facing == newFacing)
			return;
		facing = newFacing;
		if (init) {
			EnergyControl.screenManager.unregisterScreenPart(this);
			EnergyControl.screenManager.registerInfoPanelExtender(this);
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
		if (level.isClientSide() && !partOfScreen && screen != null)
			setScreen(null);
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	public void onServerMessageReceived(CompoundTag tag) {}

	@Override
	protected void readProperties(ValueInput tag) {
		super.readProperties(tag);
		partOfScreen = tag.getBooleanOr("partOfScreen", partOfScreen);
		coreX = tag.getIntOr("coreX", coreX);
		coreY = tag.getIntOr("coreY", coreY);
		coreZ = tag.getIntOr("coreZ", coreZ);
		if (level != null) {
			updateScreen();
			if (level.isClientSide())
				level.getChunkSource().getLightEngine().checkBlock(worldPosition);
			TileEntityInfoPanel.refreshModel(level, worldPosition);
		}
	}

	@Override
	protected void writeProperties(ValueOutput tag) {
		super.writeProperties(tag);
		tag.putBoolean("partOfScreen", partOfScreen);
		tag.putInt("coreX", coreX);
		tag.putInt("coreY", coreY);
		tag.putInt("coreZ", coreZ);
	}

	@Override
	public void setRemoved() {
		if (!level.isClientSide())
			EnergyControl.screenManager.unregisterScreenPart(this);
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

		if (!level.isClientSide() && !partOfScreen)
			EnergyControl.screenManager.registerInfoPanelExtender(this);

		updateScreen();
		init = true;
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
		TileEntityInfoPanel.refreshModel(level, worldPosition);
		if (screen != null) {
			partOfScreen = true;
			TileEntityInfoPanel core = screen.getCore(level);
			if (core != null) {
				coreX = core.getBlockPos().getX();
				coreY = core.getBlockPos().getY();
				coreZ = core.getBlockPos().getZ();

				BlockState stateCore = level.getBlockState(core.getBlockPos());
				BlockState state = level.getBlockState(worldPosition);
				// block states change on the server only; the client rebuilds the screen on every panel update
				if (!level.isClientSide() && state.getValue(FacingBlockActive.getActive(state)) != stateCore.getValue(FacingBlockActive.getActive(stateCore)))
					level.setBlock(worldPosition, state.cycle(FacingBlockActive.getActive(state)), 2);
				return;
			}
		} else {
			BlockState state = level.getBlockState(worldPosition);
			if (!level.isClientSide() && state.getValue(FacingBlockActive.getActive(state)))
				level.setBlock(worldPosition, state.setValue(FacingBlockActive.getActive(state), false), 2);
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
		return core.powered;
	}

	public int getColorBackground() {
		if (screen == null)
			return TileEntityInfoPanel.GREEN;
		TileEntityInfoPanel core = screen.getCore(level);
		if (core == null)
			return TileEntityInfoPanel.GREEN;
		return core.getColorBackground();
	}

	/*@Override
	@Environment(EnvType.CLIENT)
	public AABB getRenderBoundingBox() {
		return new AABB(pos.offset(0, 0, 0), pos.offset(1, 1, 1));
	}*/

	/*@Override // TODO
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}*/

	@Override
	public Object getRenderData() {
		return new PanelRenderData(findTexture(), getColored() ? getColorBackground() : getDefaultBackground(), getPowered(), getRenderOffset());
	}

	protected int getDefaultBackground() {
		return TileEntityInfoPanel.GREEN;
	}

	protected RotationOffset getRenderOffset() {
		return null;
	}

	public int findTexture() {
		Screen scr = getScreen();
		if (scr != null) {
			BlockPos pos = getBlockPos();
			switch (getFacing()) {
			case SOUTH:
				return 1 * boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getY() == scr.minY) + 8 * boolToInt(pos.getY() == scr.maxY);
			case WEST:
				return 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ) + 1 * boolToInt(pos.getY() == scr.minY) + 2 * boolToInt(pos.getY() == scr.maxY);
			case EAST:
				return 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ) + 2 * boolToInt(pos.getY() == scr.minY) + 1 * boolToInt(pos.getY() == scr.maxY);
			case NORTH:
				return 1 * boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getY() == scr.minY) + 4 * boolToInt(pos.getY() == scr.maxY);
			case UP:
				return 1 * boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 8 * boolToInt(pos.getZ() == scr.minZ) + 4 * boolToInt(pos.getZ() == scr.maxZ);
			case DOWN:
				return 1 * boolToInt(pos.getX() == scr.minX) + 2 * boolToInt(pos.getX() == scr.maxX) + 4 * boolToInt(pos.getZ() == scr.minZ) + 8 * boolToInt(pos.getZ() == scr.maxZ);
			}
		}
		return 15;
	}

	private int boolToInt(boolean b) {
		return b ? 1 : 0;
	}
}