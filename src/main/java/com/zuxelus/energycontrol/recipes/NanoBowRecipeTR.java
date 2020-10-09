package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import techreborn.init.TRContent;

public class NanoBowRecipeTR extends ShapedRecipe {

	public NanoBowRecipeTR() {
		super(new Identifier(EnergyControl.MODID, "nano_bow"), "", 3, 3,
				DefaultedList.copyOf(Ingredient.EMPTY,
						Ingredient.EMPTY, Ingredient.ofItems(TRContent.Plates.CARBON), Ingredient.ofItems(TRContent.Cables.GLASSFIBER),
						Ingredient.ofItems(TRContent.ENERGY_CRYSTAL), Ingredient.EMPTY, Ingredient.ofItems(TRContent.Cables.GLASSFIBER),
						Ingredient.EMPTY, Ingredient.ofItems(TRContent.Plates.CARBON), Ingredient.ofItems(TRContent.Cables.GLASSFIBER)),
				new ItemStack(ModItems.NANO_BOW_ITEM));
	}

	@Override
	public ItemStack craft(CraftingInventory inv) {
		ItemStack stack = inv.getInvStack(3);
		if (stack.isEmpty() || stack.getItem() != TRContent.ENERGY_CRYSTAL)
			return ItemStack.EMPTY;
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("energy"))
			return new ItemStack(ModItems.NANO_BOW_ITEM);
		double energy = tag.getDouble("energy");
		return ItemStackHelper.getStackWithEnergy(ModItems.NANO_BOW_ITEM, "energy", Math.min(energy, 40000.0D));
	}

	public static class Serializer implements RecipeSerializer<ShapedRecipe> {
		public ShapedRecipe read(Identifier identifier, JsonObject jsonObject) {
			return new NanoBowRecipeTR();
		}

		public ShapedRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			return new NanoBowRecipeTR();
		}

		public void write(PacketByteBuf packetByteBuf, ShapedRecipe shapedRecipe) { }
	}
}
