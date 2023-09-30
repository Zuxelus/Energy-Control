package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

public class CraftTweakerIntegration {

	public static void init() {
		MineTweakerAPI.registerClass(KitAssemblerHandler.class);
	}

	@ZenClass("mods.energycontrol.KitAssembler")
	public static class KitAssemblerHandler {

		@ZenMethod
		public static void addRecipe(IItemStack input1, IItemStack input2, IItemStack input3, IItemStack  output, int time) {
			KitAssemblerRecipe.addRecipe(new KitAssemblerRecipe(getStack(input1), getStack(input2), getStack(input3), getStack(output), time));
		}

		@ZenMethod
		public static void removeRecipe(IItemStack input1, IItemStack input2, IItemStack input3) {
			KitAssemblerRecipe.removeRecipe(getStack(input1), getStack(input2), getStack(input3), null);
		}

		private static ItemStack getStack(IItemStack stack) {
			return MineTweakerMC.getItemStack(stack);
		}
	}
}
