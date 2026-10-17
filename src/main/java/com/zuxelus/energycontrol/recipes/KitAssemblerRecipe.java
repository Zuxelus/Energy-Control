package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KitAssemblerRecipe implements Recipe<RecipeInput> {
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
		ItemStack stack1 = te.getItem(TileEntityKitAssembler.SLOT_CARD1);
		if (stack1.isEmpty() || stack1.getCount() < count1 || !input1.test(stack1))
			return false;
		ItemStack stack2 = te.getItem(TileEntityKitAssembler.SLOT_ITEM);
		if (stack2.isEmpty() || stack2.getCount() < count2 || !input2.test(stack2))
			return false;
		ItemStack stack3 = te.getItem(TileEntityKitAssembler.SLOT_CARD2);
		if (stack3.isEmpty() || stack3.getCount() < count3 || !input3.test(stack3))
			return false;
		ItemStack result = te.getItem(TileEntityKitAssembler.SLOT_RESULT);
		if (!result.isEmpty()) {
			if (!ItemStack.isSameItem(result, output))
				return false;
			if (result.getCount() + output.getCount() > result.getMaxStackSize())
				return false;
		}
		return true;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, input1, input2, input3);
	}

	@Override
	public boolean matches(RecipeInput inv, Level level) {
		return false;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(RecipeInput inv, HolderLookup.Provider registryManager) {
		return output;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registryManager) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.KIT_ASSEMBLER_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return KitAssemblerRecipeType.TYPE;
	}
}