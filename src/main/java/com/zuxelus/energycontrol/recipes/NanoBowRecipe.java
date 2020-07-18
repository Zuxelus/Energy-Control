package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;

import ic2.api.item.IC2Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;

public class NanoBowRecipe extends ShapedRecipes {

	public NanoBowRecipe() {
		super(3, 3, new ItemStack[] {
						null,
						IC2Items.getItem("crafting", "carbon_plate"),
						IC2Items.getItem("cable", "type:glass,insulation:0"),
						new ItemStack(IC2Items.getItem("energy_crystal").getItem(), 1, Short.MAX_VALUE),
						null, IC2Items.getItem("cable", "type:glass,insulation:0"), null,
						IC2Items.getItem("crafting", "carbon_plate"),
						IC2Items.getItem("cable", "type:glass,insulation:0")},
				new ItemStack(ItemHelper.itemNanoBow));
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stack = inv.getStackInSlot(3);
		if (stack == null || stack.getItem() != IC2Items.getItem("energy_crystal").getItem())
			return null;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null || !tag.hasKey("charge"))
			return new ItemStack(ItemHelper.itemNanoBow);
		double energy = tag.getDouble("charge");
		return ItemStackHelper.getStackWithEnergy(ItemHelper.itemNanoBow, "charge", Math.min(energy, 40000.0D));
	}
}
