package com.zuxelus.energycontrol.blockentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class InfoPanelExtenderBlockEntity extends BlockEntity implements Tickable, ITilePacketHandler, IScreenPart, IFacingBlock {
	protected boolean init;
	private FacingBlockEntity facingBE = new FacingBlockEntity(false);

	protected Screen screen;
	private boolean partOfScreen;

	private int coreX;
	private int coreY;
	private int coreZ;

	public InfoPanelExtenderBlockEntity() {
		super(ModItems.INFO_PANEL_EXTENDER_BLOCK_ENTITY);

		init = false;
		screen = null;
		partOfScreen = false;
		coreX = 0;
		coreY = 0;
		coreZ = 0;
	}

	private void updateScreen() {
		if (partOfScreen && screen == null) {
			BlockEntity core = world.getBlockEntity(new BlockPos(coreX, coreY, coreZ));
			if (core != null && core instanceof InfoPanelBlockEntity) {
				screen = ((InfoPanelBlockEntity) core).getScreen();
				if (screen != null)
					screen.init(true, world);
			}
		}
		if (world.isClient && !partOfScreen && screen != null)
			setScreen(null);
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	public void onServerMessageReceived(CompoundTag tag) { }

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		tag = writeProperties(tag);
		return new BlockEntityUpdateS2CPacket(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(BlockEntityUpdateS2CPacket pkt) {
		readProperties(pkt.getCompoundTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag tag = super.toInitialChunkDataTag();
		tag = writeProperties(tag);
		return tag;
	}

	protected void readProperties(CompoundTag tag) {
		facingBE.readProperties(tag);
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
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		readProperties(tag);
	}

	protected CompoundTag writeProperties(CompoundTag tag) {
		tag = facingBE.writeProperties(tag);
		tag.putBoolean("partOfScreen", partOfScreen);
		tag.putInt("coreX", coreX);
		tag.putInt("coreY", coreY);
		tag.putInt("coreZ", coreZ);
		return tag;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		return writeProperties(super.toTag(tag));
	}

	@Override
	public void markRemoved() {
		if (!world.isClient)
			EnergyControl.screenManager.unregisterScreenPart(this);
		super.markRemoved();
	}

	@Override
	public void tick() {
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
		if (screen != null) {
			partOfScreen = true;
			InfoPanelBlockEntity core = screen.getCore(world);
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

	public InfoPanelBlockEntity getCore() {
		if (screen == null)
			return null;
		return screen.getCore(world);
	}

	@Override
	public void updateData() { }

	@Override
	public void notifyBlockUpdate() {
		BlockState iblockstate = world.getBlockState(pos);
		world.updateListeners(pos, iblockstate, iblockstate, 2);
	}

	public boolean getColored() {
		if (screen == null)
			return false;
		InfoPanelBlockEntity core = screen.getCore(world);
		if (core == null)
			return false;
		return core.getColored();
	}

	public boolean getPowered() {
		if (screen == null)
			return false;
		InfoPanelBlockEntity core = screen.getCore(world);
		if (core == null)
			return false;
		return core.powered;
	}

	public int getColorBackground() {
		if (screen == null)
			return 2;
		InfoPanelBlockEntity core = screen.getCore(world);
		if (core == null)
			return 2;
		return core.getColorBackground();
	}

	@Override
	public Direction getFacing() {
		return facingBE.getFacing();
	}

	@Override
	public void setFacing(Direction newFacing) {
		if (facingBE.getFacing() == newFacing)
			return;
		facingBE.setFacing(newFacing);
		if (init) {
			EnergyControl.screenManager.unregisterScreenPart(this);
			EnergyControl.screenManager.registerInfoPanelExtender(this);
		}
	}

	@Override
	public Direction getRotation() {
		return facingBE.getRotation();
	}

	@Override
	public void setRotation(Direction meta) {
		facingBE.setRotation(meta);
	}

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
