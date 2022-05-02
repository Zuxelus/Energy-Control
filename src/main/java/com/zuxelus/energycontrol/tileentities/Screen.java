package com.zuxelus.energycontrol.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Screen {
	public int minX;
	public int minY;
	public int minZ;
	public int maxX;
	public int maxY;
	public int maxZ;
	private int coreX;
	private int coreY;
	private int coreZ;
	private boolean powered;

	public Screen(TileEntityInfoPanel panel) {
		maxX = minX = coreX = panel.xCoord;
		maxY = minY = coreY = panel.yCoord;
		maxZ = minZ = coreZ = panel.zCoord;

		powered = panel.getPowered();
	}

	public Screen(TileEntityInfoPanel panel, NBTTagCompound tag) {
		minX = tag.getInteger("minX");
		minY = tag.getInteger("minY");
		minZ = tag.getInteger("minZ");

		maxX = tag.getInteger("maxX");
		maxY = tag.getInteger("maxY");
		maxZ = tag.getInteger("maxZ");

		coreX = panel.xCoord;
		coreY = panel.yCoord;
		coreZ = panel.zCoord;
		powered = panel.getPowered();
	}

	public TileEntityInfoPanel getCore(IBlockAccess world) {
		TileEntity te = world.getTileEntity(coreX, coreY, coreZ);
		if (!(te instanceof TileEntityInfoPanel))
			return null;
		return (TileEntityInfoPanel) te;
	}

	public boolean isBlockNearby(TileEntity tileEntity) {
		int x = tileEntity.xCoord;
		int y = tileEntity.yCoord;
		int z = tileEntity.zCoord;
		return (x == minX - 1 && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
				|| (x == maxX + 1 && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
				|| (x >= minX && x <= maxX && y == minY - 1 && z >= minZ && z <= maxZ)
				|| (x >= minX && x <= maxX && y == maxY + 1 && z >= minZ && z <= maxZ)
				|| (x >= minX && x <= maxX && y >= minY && y <= maxY && z == minZ - 1)
				|| (x >= minX && x <= maxX && y >= minY && y <= maxY && z == maxZ + 1);
	}

	public boolean isBlockPartOf(TileEntity tileEntity) {
		int x = tileEntity.xCoord;
		int y = tileEntity.yCoord;
		int z = tileEntity.zCoord;
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
	}

	public void init(boolean force, World world) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					TileEntity te = world.getTileEntity(x, y, z);
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
					TileEntity te = world.getTileEntity(x, y, z);
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
					TileEntity te = world.getTileEntity(x, y, z);
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
		result = prime * result + coreX;
		result = prime * result + coreY;
		result = prime * result + coreZ;
		result = prime * result + maxX;
		result = prime * result + maxY;
		result = prime * result + maxZ;
		result = prime * result + minX;
		result = prime * result + minY;
		result = prime * result + minZ;
		return result;
	}

	public boolean isCore(int x, int y, int z) {
		return x == coreX && y == coreY && z == coreZ;
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
		if (coreX != other.coreX)
			return false;
		if (coreY != other.coreY)
			return false;
		if (coreZ != other.coreZ)
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
