package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.tileentities.Screen;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

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

	private void updateOffset(RotationOffset offset, float length, int pos, int state) {
		offset.leftTop += length * (pos + ((state >> 3) & 1));
		offset.rightTop += length * (pos + ((state >> 2) & 1));
		offset.leftBottom += length * (pos + ((state >> 1) & 1));
		offset.rightBottom += length * (pos + ((state) & 1));
	}

	public RotationOffset addOffset(Screen screen, BlockPos pos, EnumFacing facing, EnumFacing rotation) {
		if (rotateHor == 0 && rotateVert == 0)
			return this;

		RotationOffset offset = new RotationOffset();
		float lengthX = (this.leftTop - this.rightTop) / (screen.maxX - screen.minX + 1);
		float lengthYX = (this.leftTop - this.leftBottom) / (screen.maxX - screen.minX + 1);
		float lengthY = (this.leftTop - this.leftBottom) / (screen.maxY - screen.minY + 1);
		float lengthZ = (this.leftTop - this.rightTop) / (screen.maxZ - screen.minZ + 1);
		float lengthYZ = (this.leftTop - this.leftBottom) / (screen.maxZ - screen.minZ + 1);
		switch (facing) {
		case NORTH:
			if (rotateVert > 0)
				updateOffset(offset, -lengthY, screen.maxY - pos.getY(), 10);
			if (rotateVert < 0)
				updateOffset(offset, lengthY, pos.getY() - screen.minY, 5);
			if (rotateHor > 0)
				updateOffset(offset, -lengthX, screen.maxX - pos.getX(), 3);
			if (rotateHor < 0)
				updateOffset(offset, lengthX, pos.getX() - screen.minX, 12);
			break;
		case SOUTH:
			if (rotateVert > 0)
				updateOffset(offset, -lengthY, screen.maxY - pos.getY(), 5);
			if (rotateVert < 0)
				updateOffset(offset, lengthY, pos.getY() - screen.minY, 10);
			if (rotateHor > 0)
				updateOffset(offset, -lengthX, pos.getX() - screen.minX, 12);
			if (rotateHor < 0)
				updateOffset(offset, lengthX, screen.maxX - pos.getX(), 3);
			break;
		case EAST:
			if (rotateVert > 0)
				updateOffset(offset, -lengthY, screen.maxY - pos.getY(), 12);
			if (rotateVert < 0)
				updateOffset(offset, lengthY, pos.getY() - screen.minY, 3);
			if (rotateHor > 0)
				updateOffset(offset, -lengthZ, screen.maxZ - pos.getZ(), 10);
			if (rotateHor < 0)
				updateOffset(offset, lengthZ, pos.getZ() - screen.minZ, 5);
			break;
		case WEST:
			if (rotateVert > 0)
				updateOffset(offset, -lengthY, screen.maxY - pos.getY(), 3);
			if (rotateVert < 0)
				updateOffset(offset, lengthY, pos.getY() - screen.minY, 12);
			if (rotateHor > 0)
				updateOffset(offset, -lengthZ, pos.getZ() - screen.minZ, 5);
			if (rotateHor < 0)
				updateOffset(offset, lengthZ, screen.maxZ - pos.getZ(), 10);
			break;
		case UP:
			switch (rotation) {
			case NORTH:
				if (rotateVert > 0)
					updateOffset(offset, -lengthYZ, screen.maxZ - pos.getZ(), 10);
				if (rotateVert < 0)
					updateOffset(offset, lengthYZ, pos.getZ() - screen.minZ, 5);
				if (rotateHor > 0)
					updateOffset(offset, -lengthX, pos.getX() - screen.minX, 3);
				if (rotateHor < 0)
					updateOffset(offset, lengthX, screen.maxX - pos.getX(), 12);
				break;
			case SOUTH:
				if (rotateVert > 0)
					updateOffset(offset, -lengthYZ, pos.getZ() - screen.minZ, 5);
				if (rotateVert < 0)
					updateOffset(offset, lengthYZ, screen.maxZ - pos.getZ(), 10);
				if (rotateHor > 0)
					updateOffset(offset, -lengthX, pos.getX() - screen.minX, 12);
				if (rotateHor < 0)
					updateOffset(offset, lengthX, screen.maxX - pos.getX(), 3);
				break;
			case EAST:
				if (rotateVert > 0)
					updateOffset(offset, -lengthX, pos.getX() - screen.minX, 12);
				if (rotateVert < 0)
					updateOffset(offset, lengthX, screen.maxX - pos.getX(), 3);
				if (rotateHor > 0)
					updateOffset(offset, -lengthZ, screen.maxZ - pos.getZ(), 10);
				if (rotateHor < 0)
					updateOffset(offset, lengthZ, pos.getZ() - screen.minZ, 5);
				break;
			case WEST:
				if (rotateVert > 0)
					updateOffset(offset, -lengthX, screen.maxX - pos.getX(), 3);
				if (rotateVert < 0)
					updateOffset(offset, lengthX, pos.getX() - screen.minX, 12);
				if (rotateHor > 0)
					updateOffset(offset, -lengthZ, pos.getZ() - screen.minZ, 5);
				if (rotateHor < 0)
					updateOffset(offset, lengthZ, screen.maxZ - pos.getZ(), 10);
			}
			break;
		case DOWN:
			switch (rotation) {
			case SOUTH:
				if (rotateVert > 0)
					updateOffset(offset, -lengthYZ, screen.maxZ - pos.getZ(), 5);
				if (rotateVert < 0)
					updateOffset(offset, lengthYZ, pos.getZ() - screen.minZ, 10);
				if (rotateHor > 0)
					updateOffset(offset, -lengthX, pos.getX() - screen.minX, 12);
				if (rotateHor < 0)
					updateOffset(offset, lengthX, screen.maxX - pos.getX(), 3);
				break;
			case NORTH:
				if (rotateVert > 0)
					updateOffset(offset, -lengthYZ, pos.getZ() - screen.minZ, 10);
				if (rotateVert < 0)
					updateOffset(offset, lengthYZ, screen.maxZ - pos.getZ(), 5);
				if (rotateHor > 0)
					updateOffset(offset, -lengthX, screen.maxX - pos.getX(), 3);
				if (rotateHor < 0)
					updateOffset(offset, lengthX, pos.getX() - screen.minX, 12);
				break;
			case WEST:
				if (rotateVert > 0)
					updateOffset(offset, -lengthYX, screen.maxX - pos.getX(), 3);
				if (rotateVert < 0)
					updateOffset(offset, lengthYX, pos.getX() - screen.minX, 12);
				if (rotateHor > 0)
					updateOffset(offset, -lengthZ, screen.maxZ - pos.getZ(), 5);
				if (rotateHor < 0)
					updateOffset(offset, lengthZ, pos.getZ() - screen.minZ, 10);
				break;
			case EAST:
				if (rotateVert > 0)
					updateOffset(offset, -lengthYX, screen.maxX - pos.getX(), 12);
				if (rotateVert < 0)
					updateOffset(offset, lengthYX, pos.getX() - screen.minX, 3);
				if (rotateHor > 0)
					updateOffset(offset, -lengthZ, pos.getZ() - screen.minZ, 10);
				if (rotateHor < 0)
					updateOffset(offset, lengthZ, screen.maxZ - pos.getZ(), 5);
			}
			break;
		default:
			break;
		}
		offset.leftTop += 32 - thickness;
		offset.rightTop += 32 - thickness;
		offset.leftBottom += 32 - thickness;
		offset.rightBottom += 32 - thickness;
		return offset;
	}
}
