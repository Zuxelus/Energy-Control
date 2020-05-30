package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.tileentities.Screen;

import net.minecraftforge.common.util.ForgeDirection;

public class RotationOffset {
	public float leftTop;
	public float leftBottom;
	public float rightTop;
	public float rightBottom;
	public int thickness;
	public int rotateHor;
	public int rotateVert;

	public RotationOffset() {
		leftTop = 0.0F;
		leftBottom = 0.0F;
		rightTop = 0.0F;
		rightBottom = 0.0F;
	}

	public RotationOffset(int thickness, int rotateHor, int rotateVert) {
		this.thickness = thickness;
		this.rotateHor = rotateHor;
		this.rotateVert = rotateVert;

		if (rotateVert == 0) {
			if (rotateHor < 0) {
				leftTop -= rotateHor / 4.0F * 16;
				leftBottom -= rotateHor / 4.0F * 16;
			} else {
				rightTop += rotateHor / 4.0F * 16;
				rightBottom += rotateHor / 4.0F * 16;
			}
		} else if (rotateHor < 0) {
			leftTop -= rotateHor / 4.0F * 16 / 2;
			leftBottom -= rotateHor / 4.0F * 16 / 2;
		} else {
			rightTop += rotateHor / 4.0F * 16 / 2;
			rightBottom += rotateHor / 4.0F * 16 / 2;
		}

		if (rotateHor == 0) {
			if (rotateVert < 0) {
				leftTop -= rotateVert / 4.0F * 16;
				rightTop -= rotateVert / 4.0F * 16;
			} else {
				leftBottom += rotateVert / 4.0F * 16;
				rightBottom += rotateVert / 4.0F * 16;
			}
		} else if (rotateVert < 0) {
			leftTop -= rotateVert / 4.0F * 16 / 2;
			rightTop -= rotateVert / 4.0F * 16 / 2;
		} else {
			leftBottom += rotateVert / 4.0F * 16 / 2;
			rightBottom += rotateVert / 4.0F * 16 / 2;
		}

		leftTop = 32 - (32 - leftTop) / 32 * thickness;
		leftBottom = 32 - (32 - leftBottom) / 32 * thickness;
		rightTop = 32 - (32 - rightTop) / 32 * thickness;
		rightBottom = 32 - (32 - rightBottom) / 32 * thickness;
	}

	public RotationOffset addOffset(Screen screen, int x, int y, int z, ForgeDirection facing, ForgeDirection rotation) {
		if (rotateHor == 0 && rotateVert == 0)
			return this;

		float leftTop = 0;
		float leftBottom = 0;
		float rightTop = 0;
		float rightBottom = 0;
		int lengthX = screen.maxX - screen.minX + 1;
		int lengthY = screen.maxY - screen.minY + 1;
		int lengthZ = screen.maxZ - screen.minZ + 1;
		switch (facing) {
		case NORTH:
			if (rotateVert > 0) {
				int posY = screen.maxY - y;
				leftTop += (this.leftBottom - this.leftTop) / lengthY * (posY + 1);
				rightTop += (this.leftBottom - this.leftTop) / lengthY * posY;
				leftBottom += (this.leftBottom - this.leftTop) / lengthY * (posY + 1);
				rightBottom += (this.leftBottom - this.leftTop) / lengthY * posY;
			}
			if (rotateVert < 0) {
				int posY = y - screen.minY;
				leftTop += (this.leftTop - this.leftBottom) / lengthY * posY;
				rightTop += (this.leftTop - this.leftBottom) / lengthY * (posY + 1);
				leftBottom += (this.leftTop - this.leftBottom) / lengthY * posY;
				rightBottom += (this.leftTop - this.leftBottom) / lengthY * (posY + 1);
			}
			if (rotateHor > 0) {
				int posX = screen.maxX - x;
				leftTop += (this.rightTop - this.leftTop) / lengthX * posX;
				rightTop += (this.rightTop - this.leftTop) / lengthX * posX;
				leftBottom += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
				rightBottom += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
			}
			if (rotateHor < 0) {
				int posX = x - screen.minX;
				leftTop += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
				rightTop += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
				leftBottom += (this.leftTop - this.rightTop) / lengthX * posX;
				rightBottom += (this.leftTop - this.rightTop) / lengthX * posX;
			}
			break;
		case SOUTH:
			if (rotateVert > 0) {
				int posY = screen.maxY - y;
				leftTop += (this.leftBottom - this.leftTop) / lengthY * posY;
				rightTop += (this.leftBottom - this.leftTop) / lengthY * (posY + 1);
				leftBottom += (this.leftBottom - this.leftTop) / lengthY * posY;
				rightBottom += (this.leftBottom - this.leftTop) / lengthY * (posY + 1);
			}
			if (rotateVert < 0) {
				int posY = y - screen.minY;
				leftTop += (this.leftTop - this.leftBottom) / lengthY * (posY + 1);
				rightTop += (this.leftTop - this.leftBottom) / lengthY * posY;
				leftBottom += (this.leftTop - this.leftBottom) / lengthY * (posY + 1);
				rightBottom += (this.leftTop - this.leftBottom) / lengthY * posY;
			}
			if (rotateHor > 0) {
				int posX = x - screen.minX;
				leftTop += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
				rightTop += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
				leftBottom += (this.rightTop - this.leftTop) / lengthX * posX;
				rightBottom += (this.rightTop - this.leftTop) / lengthX * posX;
			}
			if (rotateHor < 0) {
				int posX = screen.maxX - x;
				leftTop += (this.leftTop - this.rightTop) / lengthX * posX;
				rightTop += (this.leftTop - this.rightTop) / lengthX * posX;
				leftBottom += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
				rightBottom += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
			}
			break;
		case EAST:
			if (rotateVert > 0) {
				int posY = screen.maxY - y;
				leftTop += (this.leftBottom - this.leftTop) / lengthY * (posY + 1);
				rightTop += (this.leftBottom - this.leftTop) / lengthY * (posY + 1);
				leftBottom += (this.leftBottom - this.leftTop) / lengthY * posY;
				rightBottom += (this.leftBottom - this.leftTop) / lengthY * posY;
			}
			if (rotateVert < 0) {
				int posY = y - screen.minY;
				leftTop += (this.leftTop - this.leftBottom) / lengthY * posY;
				rightTop += (this.leftTop - this.leftBottom) / lengthY * posY;
				leftBottom += (this.leftTop - this.leftBottom) / lengthY * (posY + 1);
				rightBottom += (this.leftTop - this.leftBottom) / lengthY * (posY + 1);
			}
			if (rotateHor > 0) {
				int posZ = screen.maxZ - z;
				leftTop += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
				rightTop += (this.rightTop - this.leftTop) / lengthZ * posZ;
				leftBottom += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
				rightBottom += (this.rightTop - this.leftTop) / lengthZ * posZ;
			}
			if (rotateHor < 0) {
				int posZ = z - screen.minZ;
				leftTop += (this.leftTop - this.rightTop) / lengthZ * posZ;
				rightTop += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
				leftBottom += (this.leftTop - this.rightTop) / lengthZ * posZ;
				rightBottom += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
			}
			break;
		case WEST:
			if (rotateVert > 0) {
				int posY = screen.maxY - y;
				leftTop += (this.leftBottom - this.leftTop) / lengthY * posY;
				rightTop += (this.leftBottom - this.leftTop) / lengthY * posY;
				leftBottom += (this.leftBottom - this.leftTop) / lengthY * (posY + 1);
				rightBottom += (this.leftBottom - this.leftTop) / lengthY * (posY + 1);
			}
			if (rotateVert < 0) {
				int posY = y - screen.minY;
				leftTop += (this.leftTop - this.leftBottom) / lengthY * (posY + 1);
				rightTop += (this.leftTop - this.leftBottom) / lengthY * (posY + 1);
				leftBottom += (this.leftTop - this.leftBottom) / lengthY * posY;
				rightBottom += (this.leftTop - this.leftBottom) / lengthY * posY;
			}
			if (rotateHor > 0) {
				int posZ = z - screen.minZ;
				leftTop += (this.rightTop - this.leftTop) / lengthZ * posZ;
				rightTop += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
				leftBottom += (this.rightTop - this.leftTop) / lengthZ * posZ;
				rightBottom += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
			}
			if (rotateHor < 0) {
				int posZ = screen.maxZ - z;
				leftTop += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
				rightTop += (this.leftTop - this.rightTop) / lengthZ * posZ;
				leftBottom += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
				rightBottom += (this.leftTop - this.rightTop) / lengthZ * posZ;
			}
			break;
		case UP:
			switch (rotation) {
			case NORTH:
				if (rotateVert > 0) {
					int posZ = screen.maxZ - z;
					leftTop += (this.leftBottom - this.leftTop) / lengthZ * (posZ + 1);
					rightTop += (this.leftBottom - this.leftTop) / lengthZ * posZ;
					leftBottom += (this.leftBottom - this.leftTop) / lengthZ * (posZ + 1);
					rightBottom += (this.leftBottom - this.leftTop) / lengthZ * posZ;
				}
				if (rotateVert < 0) {
					int posZ = z - screen.minZ;
					leftTop += (this.leftTop - this.leftBottom) / lengthZ * posZ;
					rightTop += (this.leftTop - this.leftBottom) / lengthZ * (posZ + 1);
					leftBottom += (this.leftTop - this.leftBottom) / lengthZ * posZ;
					rightBottom += (this.leftTop - this.leftBottom) / lengthZ * (posZ + 1);
				}
				if (rotateHor > 0) {
					int posX = x - screen.minX;
					leftTop += (this.rightTop - this.leftTop) / lengthX * posX;
					rightTop += (this.rightTop - this.leftTop) / lengthX * posX;
					leftBottom += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
					rightBottom += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
				}
				if (rotateHor < 0) {
					int posX = screen.maxX - x;
					leftTop += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
					rightTop += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
					leftBottom += (this.leftTop - this.rightTop) / lengthX * posX;
					rightBottom += (this.leftTop - this.rightTop) / lengthX * posX;
				}
				break;
			case SOUTH:
				if (rotateVert > 0) {
					int posZ = z - screen.minZ;
					leftTop += (this.leftBottom - this.leftTop) / lengthZ * posZ;
					rightTop += (this.leftBottom - this.leftTop) / lengthZ * (posZ + 1);
					leftBottom += (this.leftBottom - this.leftTop) / lengthZ * posZ;
					rightBottom += (this.leftBottom - this.leftTop) / lengthZ * (posZ + 1);
				}
				if (rotateVert < 0) {
					int posZ = screen.maxZ - z;
					leftTop += (this.leftTop - this.leftBottom) / lengthZ * (posZ + 1);
					rightTop += (this.leftTop - this.leftBottom) / lengthZ * posZ;
					leftBottom += (this.leftTop - this.leftBottom) / lengthZ * (posZ + 1);
					rightBottom += (this.leftTop - this.leftBottom) / lengthZ * posZ;
				}
				if (rotateHor > 0) {
					int posX = x - screen.minX;
					leftTop += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
					rightTop += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
					leftBottom += (this.rightTop - this.leftTop) / lengthX * posX;
					rightBottom += (this.rightTop - this.leftTop) / lengthX * posX;
				}
				if (rotateHor < 0) {
					int posX = screen.maxX - x;
					leftTop += (this.leftTop - this.rightTop) / lengthX * posX;
					rightTop += (this.leftTop - this.rightTop) / lengthX * posX;
					leftBottom += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
					rightBottom += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
				}
				break;
			case EAST:
				if (rotateVert > 0) {
					int posX = x - screen.minX;
					leftTop += (this.leftBottom - this.leftTop) / lengthX * (posX + 1);
					rightTop += (this.leftBottom - this.leftTop) / lengthX * (posX + 1);
					leftBottom += (this.leftBottom - this.leftTop) / lengthX * posX;
					rightBottom += (this.leftBottom - this.leftTop) / lengthX * posX;
				}
				if (rotateVert < 0) {
					int posX = screen.maxX - x;
					leftTop += (this.leftTop - this.leftBottom) / lengthX * posX;
					rightTop += (this.leftTop - this.leftBottom) / lengthX * posX;
					leftBottom += (this.leftTop - this.leftBottom) / lengthX * (posX + 1);
					rightBottom += (this.leftTop - this.leftBottom) / lengthX * (posX + 1);
				}
				if (rotateHor > 0) {
					int posZ = screen.maxZ - z;
					leftTop += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
					rightTop += (this.rightTop - this.leftTop) / lengthZ * posZ;
					leftBottom += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
					rightBottom += (this.rightTop - this.leftTop) / lengthZ * posZ;
				}
				if (rotateHor < 0) {
					int posZ = z - screen.minZ;
					leftTop += (this.leftTop - this.rightTop) / lengthZ * posZ;
					rightTop += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
					leftBottom += (this.leftTop - this.rightTop) / lengthZ * posZ;
					rightBottom += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
				}
				break;
			case WEST:
				if (rotateVert > 0) {
					int posX = screen.maxX - x;
					leftTop += (this.leftBottom - this.leftTop) / lengthX * posX;
					rightTop += (this.leftBottom - this.leftTop) / lengthX * posX;
					leftBottom += (this.leftBottom - this.leftTop) / lengthX * (posX + 1);
					rightBottom += (this.leftBottom - this.leftTop) / lengthX * (posX + 1);
				}
				if (rotateVert < 0) {
					int posX = x - screen.minX;
					leftTop += (this.leftTop - this.leftBottom) / lengthX * (posX + 1);
					rightTop += (this.leftTop - this.leftBottom) / lengthX * (posX + 1);
					leftBottom += (this.leftTop - this.leftBottom) / lengthX * posX;
					rightBottom += (this.leftTop - this.leftBottom) / lengthX * posX;
				}
				if (rotateHor > 0) {
					int posZ = z - screen.minZ;
					leftTop += (this.rightTop - this.leftTop) / lengthZ * posZ;
					rightTop += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
					leftBottom += (this.rightTop - this.leftTop) / lengthZ * posZ;
					rightBottom += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
				}
				if (rotateHor < 0) {
					int posZ = screen.maxZ - z;
					leftTop += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
					rightTop += (this.leftTop - this.rightTop) / lengthZ * posZ;
					leftBottom += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
					rightBottom += (this.leftTop - this.rightTop) / lengthZ * posZ;
				}
			}
			break;
		case DOWN:
			switch (rotation) {
			case SOUTH:
				if (rotateVert > 0) {
					int posZ = screen.maxZ - z;
					leftTop += (this.leftBottom - this.leftTop) / lengthZ * posZ;
					rightTop += (this.leftBottom - this.leftTop) / lengthZ * (posZ + 1);
					leftBottom += (this.leftBottom - this.leftTop) / lengthZ * posZ;
					rightBottom += (this.leftBottom - this.leftTop) / lengthZ * (posZ + 1);
				}
				if (rotateVert < 0) {
					int posZ = z - screen.minZ;
					leftTop += (this.leftTop - this.leftBottom) / lengthZ * (posZ + 1);
					rightTop += (this.leftTop - this.leftBottom) / lengthZ * posZ;
					leftBottom += (this.leftTop - this.leftBottom) / lengthZ * (posZ + 1);
					rightBottom += (this.leftTop - this.leftBottom) / lengthZ * posZ;
				}
				if (rotateHor > 0) {
					int posX = x - screen.minX;
					leftTop += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
					rightTop += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
					leftBottom += (this.rightTop - this.leftTop) / lengthX * posX;
					rightBottom += (this.rightTop - this.leftTop) / lengthX * posX;
				}
				if (rotateHor < 0) {
					int posX = screen.maxX - x;
					leftTop += (this.leftTop - this.rightTop) / lengthX * posX;
					rightTop += (this.leftTop - this.rightTop) / lengthX * posX;
					leftBottom += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
					rightBottom += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
				}
				break;
			case NORTH:
				if (rotateVert > 0) {
					int posZ = z - screen.minZ;
					leftTop += (this.leftBottom - this.leftTop) / lengthZ * (posZ + 1);
					rightTop += (this.leftBottom - this.leftTop) / lengthZ * posZ;
					leftBottom += (this.leftBottom - this.leftTop) / lengthZ * (posZ + 1);
					rightBottom += (this.leftBottom - this.leftTop) / lengthZ * posZ;
				}
				if (rotateVert < 0) {
					int posZ = screen.maxZ - z;
					leftTop += (this.leftTop - this.leftBottom) / lengthZ * posZ;
					rightTop += (this.leftTop - this.leftBottom) / lengthZ * (posZ + 1);
					leftBottom += (this.leftTop - this.leftBottom) / lengthZ * posZ;
					rightBottom += (this.leftTop - this.leftBottom) / lengthZ * (posZ + 1);
				}
				if (rotateHor > 0) {
					int posX = screen.maxX - x;
					leftTop += (this.rightTop - this.leftTop) / lengthX * posX;
					rightTop += (this.rightTop - this.leftTop) / lengthX * posX;
					leftBottom += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
					rightBottom += (this.rightTop - this.leftTop) / lengthX * (posX + 1);
				}
				if (rotateHor < 0) {
					int posX = x - screen.minX;
					leftTop += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
					rightTop += (this.leftTop - this.rightTop) / lengthX * (posX + 1);
					leftBottom += (this.leftTop - this.rightTop) / lengthX * posX;
					rightBottom += (this.leftTop - this.rightTop) / lengthX * posX;
				}
				break;
			case WEST:
				if (rotateVert > 0) {
					int posX = screen.maxX - x;
					leftTop += (this.leftBottom - this.leftTop) / lengthX * posX;
					rightTop += (this.leftBottom - this.leftTop) / lengthX * posX;
					leftBottom += (this.leftBottom - this.leftTop) / lengthX * (posX + 1);
					rightBottom += (this.leftBottom - this.leftTop) / lengthX * (posX + 1);
				}
				if (rotateVert < 0) {
					int posX = x - screen.minX;
					leftTop += (this.leftTop - this.leftBottom) / lengthX * (posX + 1);
					rightTop += (this.leftTop - this.leftBottom) / lengthX * (posX + 1);
					leftBottom += (this.leftTop - this.leftBottom) / lengthX * posX;
					rightBottom += (this.leftTop - this.leftBottom) / lengthX * posX;
				}
				if (rotateHor > 0) {
					int posZ = screen.maxZ - z;
					leftTop += (this.rightTop - this.leftTop) / lengthZ * posZ;
					rightTop += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
					leftBottom += (this.rightTop - this.leftTop) / lengthZ * posZ;
					rightBottom += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
				}
				if (rotateHor < 0) {
					int posZ = z - screen.minZ;
					leftTop += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
					rightTop += (this.leftTop - this.rightTop) / lengthZ * posZ;
					leftBottom += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
					rightBottom += (this.leftTop - this.rightTop) / lengthZ * posZ;
				}
				break;
			case EAST:
				if (rotateVert > 0) {
					int posX = screen.maxX - x;
					leftTop += (this.leftBottom - this.leftTop) / lengthX * (posX + 1);
					rightTop += (this.leftBottom - this.leftTop) / lengthX * (posX + 1);
					leftBottom += (this.leftBottom - this.leftTop) / lengthX * posX;
					rightBottom += (this.leftBottom - this.leftTop) / lengthX * posX;
				}
				if (rotateVert < 0) {
					int posX = x - screen.minX;
					leftTop += (this.leftTop - this.leftBottom) / lengthX * posX;
					rightTop += (this.leftTop - this.leftBottom) / lengthX * posX;
					leftBottom += (this.leftTop - this.leftBottom) / lengthX * (posX + 1);
					rightBottom += (this.leftTop - this.leftBottom) / lengthX * (posX + 1);
				}
				if (rotateHor > 0) {
					int posZ = z - screen.minZ;
					leftTop += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
					rightTop += (this.rightTop - this.leftTop) / lengthZ * posZ;
					leftBottom += (this.rightTop - this.leftTop) / lengthZ * (posZ + 1);
					rightBottom += (this.rightTop - this.leftTop) / lengthZ * posZ;
				}
				if (rotateHor < 0) {
					int posZ = screen.maxZ - z;
					leftTop += (this.leftTop - this.rightTop) / lengthZ * posZ;
					rightTop += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
					leftBottom += (this.leftTop - this.rightTop) / lengthZ * posZ;
					rightBottom += (this.leftTop - this.rightTop) / lengthZ * (posZ + 1);
				}
			}
			break;
		default:
			break;
		}
		RotationOffset offset = new RotationOffset();
		offset.leftTop = leftTop + 32 - thickness;
		offset.rightTop = rightTop + 32 - thickness;
		offset.leftBottom = leftBottom + 32 - thickness;
		offset.rightBottom = rightBottom + 32 - thickness;
		return offset;
	}
}
