package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.IItemKit;

public abstract class ItemKitBase implements IItemKit {
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
}
