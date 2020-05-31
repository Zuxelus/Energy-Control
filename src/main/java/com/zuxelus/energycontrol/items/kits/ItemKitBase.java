package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemKit;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public abstract class ItemKitBase implements IItemKit {
	private IIcon icon;
	protected String name;
	protected int damage;
	private Object[] recipe;

	public ItemKitBase(int damage, String name) {
		this.damage = damage;
		this.name = name;
	}

	@Override
	public final int getDamage() {
		return damage;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getUnlocalizedName() {
		return "item." + name;
	}

	@Override
	public Object[] getRecipe() {
		return recipe;
	}

	protected final void addRecipe(Object[] recipe) {
		this.recipe = recipe;
	}

	public void registerIcon(IIconRegister iconRegister) {
		icon = iconRegister.registerIcon(EnergyControl.MODID + ":" + name);
	}

	public IIcon getIcon() {
		return icon;
	}
}
