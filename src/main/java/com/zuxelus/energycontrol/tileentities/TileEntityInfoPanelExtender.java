package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityInfoPanelExtender extends TileEntityFacing implements ITickableTileEntity, IScreenPart {
	protected boolean init;

	protected Screen screen;
	private boolean partOfScreen;

	private int coreX;
	private int coreY;
	private int coreZ;

	public TileEntityInfoPanelExtender(TileEntityType<?> type) {
		super(type);
		init = false;
		screen = null;
		partOfScreen = false;
		coreX = 0;
		coreY = 0;
		coreZ = 0;
	}

	public TileEntityInfoPanelExtender() {
		this(ModTileEntityTypes.info_panel_extender.get());
	}

	@Override
	public void setFacing(int meta) {
		Direction newFacing = Direction.byIndex(meta);
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
			TileEntity core = world.getTileEntity(new BlockPos(coreX, coreY, coreZ));
			if (core != null && core instanceof TileEntityInfoPanel) {
				screen = ((TileEntityInfoPanel) core).getScreen();
				if (screen != null)
					screen.init(true, world);
			}
		}
		if (world.isRemote && !partOfScreen && screen != null)
			setScreen(null);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		tag = writeProperties(tag);
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
		return tag;
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		if (tag.contains("partOfScreen"))
			partOfScreen = tag.getBoolean("partOfScreen");
		if (tag.contains("coreX")) {
			coreX = tag.getInt("coreX");
			coreY = tag.getInt("coreY");
			coreZ = tag.getInt("coreZ");
		}
		if (world != null) {
			updateScreen();
			if (world.isRemote)
				world.getChunkProvider().getLightManager().checkBlock(pos);
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putBoolean("partOfScreen", partOfScreen);
		tag.putInt("coreX", coreX);
		tag.putInt("coreY", coreY);
		tag.putInt("coreZ", coreZ);
		return tag;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		return writeProperties(super.write(tag));
	}

	@Override
	public void remove() {
		if (!world.isRemote)
			EnergyControl.INSTANCE.screenManager.unregisterScreenPart(this);
		super.remove();
	}

	@Override
	public void tick() {
		if (init)
			return;
		
		if (!world.isRemote && !partOfScreen)
			EnergyControl.INSTANCE.screenManager.registerInfoPanelExtender(this);
		
		updateScreen();
		init = true;
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
		if (screen != null) {
			partOfScreen = true;
			TileEntityInfoPanel core = screen.getCore(world);
			if (core != null) {
				coreX = core.getPos().getX();
				coreY = core.getPos().getY();
				coreZ = core.getPos().getZ();
				return;
			}
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
		return screen.getCore(world);
	}

	@Override
	public void updateData() { }

	@Override
	public void notifyBlockUpdate() {
		BlockState iblockstate = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}

	public boolean getColored() {
		if (screen == null)
			return false;
		TileEntityInfoPanel core = screen.getCore(world);
		if (core == null)
			return false;
		return core.getColored();
	}

	public boolean getPowered() {
		if (screen == null)
			return false;
		TileEntityInfoPanel core = screen.getCore(world);
		if (core == null)
			return false;
		return core.powered;
	}

	public int getColorBackground() {
		if (screen == null)
			return 2;
		TileEntityInfoPanel core = screen.getCore(world);
		if (core == null)
			return 2;
		return core.getColorBackground();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(0, 0, 0), pos.add(1, 1, 1));
	}

	/*@Override // TODO
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}*/

	@OnlyIn(Dist.CLIENT)
	public int findTexture() {
		Screen scr = getScreen();
		if (scr != null) {
			BlockPos pos = getPos();
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