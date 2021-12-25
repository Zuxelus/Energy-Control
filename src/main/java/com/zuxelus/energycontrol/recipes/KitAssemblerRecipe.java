package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.recipes.EmptyInventory;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class KitAssemblerRecipe implements Recipe<EmptyInventory>{
	private final Identifier id;
	public final Ingredient input1;
	public final Ingredient input2;
	public final Ingredient input3;
	public final int count1;
	public final int count2;
	public final int count3;
	public final ItemStack output;
	public final int time;

	public KitAssemblerRecipe(Identifier id, Ingredient input1, int count1, Ingredient input2, int count2, Ingredient input3, int count3, ItemStack output, int time) {
		this.id = id;
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
		ItemStack stack1 = te.getStack(TileEntityKitAssembler.SLOT_CARD1);
		if (stack1.isEmpty() || stack1.getCount() < count1 || !input1.test(stack1))
			return false;
		ItemStack stack2 = te.getStack(TileEntityKitAssembler.SLOT_ITEM);
		if (stack2.isEmpty() || stack2.getCount() < count2 || !input2.test(stack2))
			return false;
		ItemStack stack3 = te.getStack(TileEntityKitAssembler.SLOT_CARD2);
		if (stack3.isEmpty() || stack3.getCount() < count3 || !input3.test(stack3))
			return false;
		ItemStack result = te.getStack(TileEntityKitAssembler.SLOT_RESULT);
		if (!result.isEmpty()) {
			if (!result.isItemEqualIgnoreDamage(output))
				return false;
			if (result.getCount() + output.getCount() > result.getMaxCount())
				return false;
		}
		return true;
	}

	@Override
	public boolean matches(EmptyInventory inv, World world) {
		return false;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public ItemStack craft(EmptyInventory inv) {
		return output;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.copyOf(Ingredient.EMPTY, input1, input2, input3);
	}

	@Override
	public Identifier getId() {
		return id;
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