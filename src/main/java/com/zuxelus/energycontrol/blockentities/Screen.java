package com.zuxelus.energycontrol.blockentities;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Screen {
	public int minX;
	public int minY;
	public int minZ;
	public int maxX;
	public int maxY;
	public int maxZ;
	private BlockPos corePos;
	private boolean powered = false;

	public Screen(InfoPanelBlockEntity panel) {
		BlockPos pos = panel.getPos();
		maxX = minX = pos.getX();
		maxY = minY = pos.getY();
		maxZ = minZ = pos.getZ();

		corePos = pos;
		powered = panel.getPowered();
	}

	public Screen(InfoPanelBlockEntity panel, CompoundTag tag) {
		minX = tag.getInt("minX");
		minY = tag.getInt("minY");
		minZ = tag.getInt("minZ");

		maxX = tag.getInt("maxX");
		maxY = tag.getInt("maxY");
		maxZ = tag.getInt("maxZ");

		corePos = panel.getPos();
		powered = panel.getPowered();
	}

	public InfoPanelBlockEntity getCore(World world) {
		BlockEntity be = world.getBlockEntity(corePos);
		if (be == null || !(be instanceof InfoPanelBlockEntity))
			return null;
		return (InfoPanelBlockEntity) be;
	}

	public boolean isBlockNearby(BlockEntity be) {
		BlockPos pos = be.getPos();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return (x == minX - 1 && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
				|| (x == maxX + 1 && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
				|| (x >= minX && x <= maxX && y == minY - 1 && z >= minZ && z <= maxZ)
				|| (x >= minX && x <= maxX && y == maxY + 1 && z >= minZ && z <= maxZ)
				|| (x >= minX && x <= maxX && y >= minY && y <= maxY && z == minZ - 1)
				|| (x >= minX && x <= maxX && y >= minY && y <= maxY && z == maxZ + 1);
	}

	public boolean isBlockPartOf(BlockEntity be) {
		BlockPos pos = be.getPos();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
	}

	public void init(boolean force, World world) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockEntity be = world.getBlockEntity(new BlockPos(x, y, z));
					if (be == null || !(be instanceof IScreenPart))
						continue;
					((IScreenPart) be).setScreen(this);
					if (powered || force) {
						((IScreenPart) be).notifyBlockUpdate();
						//world.checkLight(new BlockPos(x , y, z));
					}
				}
			}
		}
	}

	public void destroy(boolean force, World world) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockEntity be = world.getBlockEntity(new BlockPos(x, y, z));
					if (be == null || !(be instanceof IScreenPart))
						continue;
					IScreenPart part = (IScreenPart) be;
					Screen targetScreen = part.getScreen();
					if (targetScreen != null && targetScreen.equals(this)) {
						part.setScreen(null);
						part.updateData();
					}
					if (powered || force) {
						part.notifyBlockUpdate();
						//world.checkLight(new BlockPos(x , y, z));
					}
				}
			}
		}
	}

	public void turnPower(boolean on, World world) {
		if (powered == on)
			return;
		powered = on;
		markUpdate(world);
	}

	private void markUpdate(World world) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockEntity be = world.getBlockEntity(new BlockPos(x, y, z));
					if (be instanceof IScreenPart)
						((IScreenPart) be).notifyBlockUpdate();
						//world.checkLight(new BlockPos(x, y, z));
				}
			}
		}
	}	

	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();

		tag.putInt("minX", minX);
		tag.putInt("minY", minY);
		tag.putInt("minZ", minZ);

		tag.putInt("maxX", maxX);
		tag.putInt("maxY", maxY);
		tag.putInt("maxZ", maxZ);

		return tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + corePos.getX();
		result = prime * result + corePos.getY();
		result = prime * result + corePos.getZ();
		result = prime * result + maxX;
		result = prime * result + maxY;
		result = prime * result + maxZ;
		result = prime * result + minX;
		result = prime * result + minY;
		result = prime * result + minZ;
		return result;
	}

	public boolean isCore(int x, int y, int z) {
		return x == corePos.getX() && y == corePos.getY() && z == corePos.getZ();
	}

	public int getDx() {
		return maxX - minX;
	}

	public int getDy() {
		return maxY - minY;
	}

	public int getDz() {
		return maxZ - minZ;
	}

	public boolean isOneBlock() {
		return minX == maxX && minY == maxY && minZ == maxZ;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Screen other = (Screen) obj;
		if (corePos.getX() != other.corePos.getX())
			return false;
		if (corePos.getY() != other.corePos.getY())
			return false;
		if (corePos.getZ() != other.corePos.getZ())
			return false;
		if (maxX != other.maxX)
			return false;
		if (maxY != other.maxY)
			return false;
		if (maxZ != other.maxZ)
			return false;
		if (minX != other.minX)
			return false;
		if (minY != other.minY)
			return false;
		if (minZ != other.minZ)
			return false;
		return true;
	}
}
