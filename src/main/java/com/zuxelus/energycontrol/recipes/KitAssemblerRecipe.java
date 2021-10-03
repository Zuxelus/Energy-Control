package com.zuxelus.energycontrol.recipes;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.jei.KitAssemblerRecipeWrapper;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class KitAssemblerRecipe implements IRecipe {
	private static List<KitAssemblerRecipe> recipes = new ArrayList<KitAssemblerRecipe>();
	private ResourceLocation id;
	public final Ingredient input1;
	public final Ingredient input2;
	public final Ingredient input3;
	public final int count1;
	public final int count2;
	public final int count3;
	public final ItemStack output;
	public final int time;

	public KitAssemblerRecipe(Ingredient input1, int count1, Ingredient input2, int count2, Ingredient input3, int count3, ItemStack output, int time) {
		this.input1 = input1;
		this.count1 = count1;
		this.input2 = input2;
		this.count2 = count2;
		this.input3 = input3;
		this.count3 = count3;
		this.output = output;
		this.time = time;
	}

	public boolean isSuitable(TileEntityKitAssembler te) {
		ItemStack stack1 = te.getStackInSlot(TileEntityKitAssembler.SLOT_CARD1);
		if (stack1.isEmpty() || stack1.getCount() < count1 || !input1.test(stack1))
			return false;
		ItemStack stack2 = te.getStackInSlot(TileEntityKitAssembler.SLOT_ITEM);
		if (stack2.isEmpty() || stack2.getCount() < count2 || !input2.test(stack2))
			return false;
		ItemStack stack3 = te.getStackInSlot(TileEntityKitAssembler.SLOT_CARD2);
		if (stack3.isEmpty() || stack3.getCount() < count3 || !input3.test(stack3))
			return false;
		ItemStack result = te.getStackInSlot(TileEntityKitAssembler.SLOT_RESULT);
		if (!result.isEmpty()) {
			if (!result.isItemEqual(output))
				return false;
			if (result.getCount() + output.getCount() > result.getMaxStackSize())
				return false;
		}
		return true;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return output;
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.from(Ingredient.EMPTY, input1, input2, input3);
	}

	@Override
	public ResourceLocation getRegistryName() {
		return id;
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation id) {
		this.id = id;
		addRecipe(this);
		return this;
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		return IRecipe.class;
	}

	public static void addRecipe(KitAssemblerRecipe recipe) {
		recipes.add(recipe);
	}

	public static List<KitAssemblerRecipe> getRecipes() {
		return recipes;
	}

	public static KitAssemblerRecipe findRecipe(TileEntityKitAssembler te) {
		for(KitAssemblerRecipe recipe : recipes) {
			if (recipe.isSuitable(te))
				return recipe; 
		}
		return null;
	}
}