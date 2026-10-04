package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.renderers.RotationOffset;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TileEntityInfoPanelExtender extends BlockEntityFacing implements IScreenPart, ITilePacketHandler, RenderAttachmentBlockEntity {
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
			EnergyControl.screenManager.unregisterScreenPart(this);
			EnergyControl.screenManager.registerInfoPanelExtender(this);
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
		readProperties(pkt.getNbt(), world.getRegistryManager());
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
		NbtCompound tag = super.toInitialChunkDataNbt(registries);
		tag = writeProperties(tag, registries);
		return tag;
	}

	@Override
	protected void readProperties(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
		super.readProperties(tag, registries);
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
			TileEntityInfoPanel.refreshModel(world, pos);
		}
	}

	@Override
	protected void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(tag, registries);
		readProperties(tag, registries);
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
		tag = super.writeProperties(tag, registries);
		tag.putBoolean("partOfScreen", partOfScreen);
		tag.putInt("coreX", coreX);
		tag.putInt("coreY", coreY);
		tag.putInt("coreZ", coreZ);
		return tag;
	}

	@Override
	protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(tag, registries);
		writeProperties(tag, registries);
	}

	@Override
	public void markRemoved() {
		if (!world.isClient)
			EnergyControl.screenManager.unregisterScreenPart(this);
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
			EnergyControl.screenManager.registerInfoPanelExtender(this);

		updateScreen();
		init = true;
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
		TileEntityInfoPanel.refreshModel(world, pos);
		if (screen != null) {
			partOfScreen = true;
			TileEntityInfoPanel core = screen.getCore(world);
			if (core != null) {
				coreX = core.getPos().getX();
				coreY = core.getPos().getY();
				coreZ = core.getPos().getZ();

				BlockState stateCore = world.getBlockState(core.getPos());
				BlockState state = world.getBlockState(pos);
				// block states change on the server only; the client rebuilds the screen on every panel update
				if (!world.isClient && state.get(FacingBlockActive.getActive(state)) != stateCore.get(FacingBlockActive.getActive(stateCore)))
					world.setBlockState(pos, state.cycle(FacingBlockActive.getActive(state)), 2);
				return;
			}
		} else {
			BlockState state = world.getBlockState(pos);
			if (!world.isClient && state.get(FacingBlockActive.getActive(state)))
				world.setBlockState(pos, state.with(FacingBlockActive.getActive(state), false), 2);
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
			return TileEntityInfoPanel.GREEN;
		TileEntityInfoPanel core = screen.getCore(world);
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
	public Object getRenderAttachmentData() {
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