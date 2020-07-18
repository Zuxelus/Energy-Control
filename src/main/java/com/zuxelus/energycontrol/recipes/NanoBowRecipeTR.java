package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.ItemHelper;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;

public class NanoBowRecipeTR extends ShapedRecipes {

	public NanoBowRecipeTR() {
		super(3, 3, new ItemStack[] { null, CrossModLoader.techReborn.getItemStack("carbon_plate"),
						CrossModLoader.techReborn.getItemStack("glassfiber"),
						new ItemStack(CrossModLoader.techReborn.getItemStack("energy_crystal").getItem(), 1, Short.MAX_VALUE),
						null, CrossModLoader.techReborn.getItemStack("glassfiber"), null,
						CrossModLoader.techReborn.getItemStack("carbon_plate"),
						CrossModLoader.techReborn.getItemStack("glassfiber") },
				new ItemStack(ItemHelper.itemNanoBow));
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stack = inv.getStackInSlot(3);
		if (stack == null || stack.getItem() != CrossModLoader.techReborn.getItemStack("energy_crystal").getItem())
			return null;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null || !tag.hasKey("energy"))
			return new ItemStack(ItemHelper.itemNanoBow);
		double energy = tag.getDouble("energy");
		return ItemStackHelper.getStackWithEnergy(ItemHelper.itemNanoBow, "energy", Math.min(energy, 40000.0D));
	}
}
