package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.energycontrol.KitAssembler")
@ZenRegister
public class CraftTweakerIntegration {

	@ZenMethod
	public static void addRecipe(IItemStack input1, IItemStack input2, IItemStack input3, IItemStack  output, int time) {
		KitAssemblerRecipe.addRecipe(new KitAssemblerRecipe(getStack(input1), getStack(input2), getStack(input3), getStack(output), time));
	}

	@ZenMethod
	public static void removeRecipe(IItemStack input1, IItemStack input2, IItemStack input3) {
		KitAssemblerRecipe.removeRecipe(getStack(input1), getStack(input2), getStack(input3), ItemStack.EMPTY);
	}

	private static ItemStack getStack(IItemStack stack) {
		return CraftTweakerMC.getItemStack(stack);
	}
}
