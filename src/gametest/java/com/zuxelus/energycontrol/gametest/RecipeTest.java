package com.zuxelus.energycontrol.gametest;

import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public class RecipeTest implements FabricGameTest {

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void modRecipesLoad(TestContext ctx) {
		var manager = ctx.getWorld().getRecipeManager();
		if (manager.get(new Identifier("energycontrol", "card_array")).isEmpty())
			throw new GameTestException("recipe energycontrol:card_array did not load");
		int kits = manager.listAllOfType(KitAssemblerRecipeType.TYPE).size();
		if (kits == 0)
			throw new GameTestException("no kit assembler recipes loaded");
		ctx.complete();
	}
}
