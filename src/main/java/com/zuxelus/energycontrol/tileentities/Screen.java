package com.zuxelus.energycontrol.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Screen {
	public int minX;
	public int minY;
	public int minZ;
	public int maxX;
	public int maxY;
	public int maxZ;
	private BlockPos corePos;
	private boolean powered;

	public Screen(TileEntityInfoPanel panel) {
		BlockPos pos = panel.getPos();
		maxX = minX = pos.getX();
		maxY = minY = pos.getY();
		maxZ = minZ = pos.getZ();

		corePos = pos;
		powered = panel.getPowered();
	}

	public Screen(TileEntityInfoPanel panel, NBTTagCompound tag) {
		minX = tag.getInteger("minX");
		minY = tag.getInteger("minY");
		minZ = tag.getInteger("minZ");

		maxX = tag.getInteger("maxX");
		maxY = tag.getInteger("maxY");
		maxZ = tag.getInteger("maxZ");

		corePos = panel.getPos();
		powered = panel.getPowered();
	}

	public TileEntityInfoPanel getCore(IBlockAccess world) {
		TileEntity te = world.getTileEntity(corePos);
		if (!(te instanceof TileEntityInfoPanel))
			return null;
		return (TileEntityInfoPanel) te;
	}

	public boolean isBlockNearby(TileEntity tileEntity) {
		BlockPos pos = tileEntity.getPos();
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

	public boolean isBlockPartOf(TileEntity tileEntity) {
		BlockPos pos = tileEntity.getPos();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
	}

	public void init(boolean force, World world) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
					if (te == null || !(te instanceof IScreenPart))
						continue;
					((IScreenPart) te).setScreen(this);
					if (powered || force)
						((IScreenPart) te).updateTileEntity();
				}
			}
		}
	}

	public void destroy(boolean force, World world) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
					if (!(te instanceof IScreenPart))
						continue;
					IScreenPart part = (IScreenPart) te;
					Screen targetScreen = part.getScreen();
					if (targetScreen != null && targetScreen.equals(this)) {
						part.setScreen(null);
						part.updateData();
					}
					if (powered || force)
						part.updateTileEntity();
				}
			}
		}
	}

	private void markUpdate(World world) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
					if (te instanceof IScreenPart)
						((IScreenPart) te).updateTileEntity();
				}
			}
		}
	}	

	public NBTTagCompound toTag() {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("minX", minX);
		tag.setInteger("minY", minY);
		tag.setInteger("minZ", minZ);

		tag.setInteger("maxX", maxX);
		tag.setInteger("maxY", maxY);
		tag.setInteger("maxZ", maxZ);

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
		return minZ == other.minZ;
	}
}
