package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModBase;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class NanoBowRecipeTR extends ShapedRecipes {

	public NanoBowRecipeTR() {
		super("", 3, 3, NonNullList.withSize(9, Ingredient.EMPTY), new ItemStack(ModItems.itemNanoBow));
		CrossModBase crossMod = CrossModLoader.getCrossMod(ModIDs.TECH_REBORN);
		recipeItems.set(1, Ingredient.fromStacks(crossMod.getItemStack("carbon_plate")));
		recipeItems.set(2, Ingredient.fromStacks(crossMod.getItemStack("glassfiber")));
		ItemStack stack = new ItemStack(crossMod.getItemStack("energy_crystal").getItem(), 1, Short.MAX_VALUE);
		recipeItems.set(3, Ingredient.fromStacks(stack));
		recipeItems.set(4, Ingredient.EMPTY);
		recipeItems.set(5, Ingredient.fromStacks(crossMod.getItemStack("glassfiber")));
		recipeItems.set(6, Ingredient.EMPTY);
		recipeItems.set(7, Ingredient.fromStacks(crossMod.getItemStack("carbon_plate")));
		recipeItems.set(8, Ingredient.fromStacks(crossMod.getItemStack("glassfiber")));
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stack = inv.getStackInSlot(3);
		if (stack.isEmpty() || stack.getItem() != CrossModLoader.getCrossMod(ModIDs.TECH_REBORN).getItemStack("energy_crystal").getItem())
			return ItemStack.EMPTY;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null || !tag.hasKey("energy"))
			return new ItemStack(ModItems.itemNanoBow);
		double energy = tag.getDouble("energy");
		return ItemStackHelper.getStackWithEnergy(ModItems.itemNanoBow, "energy", Math.min(energy, 40000.0D));
	}
}
