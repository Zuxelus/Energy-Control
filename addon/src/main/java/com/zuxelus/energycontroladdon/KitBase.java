package com.zuxelus.energycontroladdon;

import com.zuxelus.energycontrol.api.IItemKit;
import net.minecraft.item.Item;

public abstract class KitBase extends Item implements IItemKit {
	protected String name;

	public KitBase(String name) {
		this.name = name;
	}

	@Override
	public String getUnlocalizedName() {
		return "item." + name;
	}
}
