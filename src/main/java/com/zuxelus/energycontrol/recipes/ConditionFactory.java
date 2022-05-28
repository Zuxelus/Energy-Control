package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

import java.util.function.BooleanSupplier;

public class ConditionFactory implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String key = JsonUtils.getString(json , "config");
		if (key.equals("classic"))
			return () -> (CrossModLoader.getCrossMod(ModIDs.IC2).getProfile() == 1);
		return () -> (CrossModLoader.getCrossMod(ModIDs.IC2).getProfile() != 1);
	}

}
