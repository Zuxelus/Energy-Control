package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public abstract class BlockBase {
	protected int damage;
	protected String name;

	public BlockBase(int damage, String name) {
		this.damage = damage;
		this.name = name;
	}

	public int getDamage() {
		return damage;
	}

	public String getName() {
		return "tile." + name;
	}

	public abstract IIcon getIconFromSide(int side);

	public abstract void registerIcons(IIconRegister iconRegister);

	public abstract TileEntity createNewTileEntity();

	public boolean isSolidBlockRequired() {
		return false;
	}

	public float[] getBlockBounds(TileEntity te) {
		float[] bounds = { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
		return bounds;
	}

	public IIcon registerIcon(IIconRegister ir, String name) {
		return ir.registerIcon(EnergyControl.MODID + ":" + name);
	}
}