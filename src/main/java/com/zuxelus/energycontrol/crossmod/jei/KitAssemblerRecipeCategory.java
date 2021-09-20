package com.zuxelus.energycontrol.crossmod.jei;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class KitAssemblerRecipeCategory implements IRecipeCategory<KitAssemblerRecipe> {
	public static final ResourceLocation id = new ResourceLocation(EnergyControl.MODID, "kit_assembler");
	public static final ResourceLocation texture = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");
	private static final String title = I18n.get(ModItems.kit_assembler.get().getDescriptionId());
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawableStatic arrow;
	private final IDrawableAnimated animatedarrow;

	public KitAssemblerRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(texture, 28, 16, 144, 54);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.kit_assembler.get()));
		arrow = guiHelper.createDrawable(texture, 176, 0, 24, 17);
		animatedarrow = guiHelper.createAnimatedDrawable(arrow, 24, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public Class<? extends KitAssemblerRecipe> getRecipeClass() {
		return KitAssemblerRecipe.class;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public ResourceLocation getUid() {
		return id;
	}

	@Override
	public void setIngredients(KitAssemblerRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, KitAssemblerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0, true, 33, 0);
		stacks.init(1, true, 33, 18);
		stacks.init(2, true, 33, 36);
		stacks.init(3, false, 92, 18);
		stacks.set(0, getStackList(recipe.input1, recipe.count1));
		stacks.set(1, getStackList(recipe.input2, recipe.count2));
		stacks.set(2, getStackList(recipe.input3, recipe.count3));
		stacks.set(3, recipe.output);
	}

	private static List<ItemStack> getStackList(Ingredient ingredient, int count) {
		List<ItemStack> list = new ArrayList<>();
		for (ItemStack stack : ingredient.getItems()) {
			if (count == 1)
				list.add(stack);
			else {
				ItemStack copy = stack.copy();
				copy.setCount(count);
				list.add(copy);
			}
		}
		return list;
	}

	@Override
	public void draw(KitAssemblerRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		animatedarrow.draw(matrixStack, 57, 19);
	}
}
