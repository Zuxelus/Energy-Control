package com.zuxelus.energycontroladdon;

import com.zuxelus.energycontrol.api.IItemKit;

public abstract class KitBase implements IItemKit {
	protected String name;
	protected int id;

	public KitBase(String name, int id) {
		this.name = name;
		this.id = id;
	}

	@Override
	public int getDamage() {
		return id;
	}

	@Override
	public String getName() {
		return EnergyControlAddon.MODID + ':' + name;
	}

	@Override
	public Object[] getRecipe() {
		return null;
	}

	@Override
	public String getUnlocalizedName() {
		return "item." + name;
	}
}
