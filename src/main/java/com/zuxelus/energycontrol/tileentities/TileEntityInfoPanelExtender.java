package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
		Direction newFacing = Direction.byId(meta);
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
			BlockEntity core = world.getBlockEntity(new BlockPos(coreX, coreY, coreZ));
			if (core != null && core instanceof TileEntityInfoPanel) {
				screen = ((TileEntityInfoPanel) core).getScreen();
				if (screen != null)
					screen.init(true, world);
			}
		}
		if (world.isClient && !partOfScreen && screen != null)
			setScreen(null);
	}

	@Override
	public void onClientMessageReceived(NbtCompound tag) { }

	@Override
	public void onServerMessageReceived(NbtCompound tag) {}

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
		return tag;
	}

	@Override
	protected void readProperties(NbtCompound tag) {
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
			if (world.isClient)
				world.getChunkManager().getLightingProvider().checkBlock(pos);
		}
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		readProperties(tag);
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag) {
		tag = super.writeProperties(tag);
		tag.putBoolean("partOfScreen", partOfScreen);
		tag.putInt("coreX", coreX);
		tag.putInt("coreY", coreY);
		tag.putInt("coreZ", coreZ);
		return tag;
	}

	@Override
	protected void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		writeProperties(tag);
	}

	@Override
	public void markRemoved() {
		if (!world.isClient)
			EnergyControl.INSTANCE.screenManager.unregisterScreenPart(this);
		super.markRemoved();
	}

	public static void tickStatic(World level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityInfoPanelExtender))
			return;
		TileEntityInfoPanelExtender te = (TileEntityInfoPanelExtender) be;
		te.tick();
	}

	protected void tick() {
		if (init)
			return;

		if (!world.isClient && !partOfScreen)
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

				BlockState stateCore = world.getBlockState(core.getPos());
				BlockState state = world.getBlockState(pos);
				if (state.get(FacingBlockActive.ACTIVE) != stateCore.get(FacingBlockActive.ACTIVE))
					world.setBlockState(pos, state.cycle(FacingBlockActive.ACTIVE), 2);
				return;
			}
		} else {
			BlockState state = world.getBlockState(pos);
			if (state.get(FacingBlockActive.ACTIVE))
				world.setBlockState(pos, state.with(FacingBlockActive.ACTIVE, false), 2);
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
	public void updateTileEntity() {
		notifyBlockUpdate();
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

	/*@Override
	@Environment(EnvType.CLIENT)
	public AABB getRenderBoundingBox() {
		return new AABB(pos.offset(0, 0, 0), pos.offset(1, 1, 1));
	}*/

	/*@Override // TODO
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}*/

	@Environment(EnvType.CLIENT)
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