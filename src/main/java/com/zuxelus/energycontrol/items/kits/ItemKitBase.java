package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.IItemKit;

public abstract class ItemKitBase implements IItemKit {
	protected String name;
	protected int damage;

	public ItemKitBase(int damage, String name) {
		this.damage = damage;
		this.name = name;
	}

	public final int getDamage() {
		return damage;
	}

	public final String getName() {
		return name;
	}

	public final String getUnlocalizedName() {
		return "item." + name;
	}
}
