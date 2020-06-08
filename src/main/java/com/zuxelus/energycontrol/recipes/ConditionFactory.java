package com.zuxelus.energycontrol.recipes;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ConditionFactory implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String key = JsonUtils.getString(json , "config");
		if (key.equals("classic"))
			return () -> (CrossModLoader.ic2.getProfile() == 1);
		return () -> (CrossModLoader.ic2.getProfile() != 1);
	}

}
