package com.zuxelus.energycontrol.blockentities;

import net.minecraft.util.math.Direction;

public interface IFacingBlock {

	public Direction getFacing();

	void setFacing(Direction meta);

	public Direction getRotation();

	public void setRotation(Direction meta);
}
