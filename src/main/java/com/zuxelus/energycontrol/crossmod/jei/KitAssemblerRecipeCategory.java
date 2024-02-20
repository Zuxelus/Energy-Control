package com.zuxelus.energycontrol.crossmod.jei;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class KitAssemblerRecipeCategory implements IRecipeCategory<KitAssemblerRecipe> {
	public static final ResourceLocation id = new ResourceLocation(EnergyControl.MODID, "kit_assembler");
	public static final ResourceLocation texture = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");
	public static final RecipeType<KitAssemblerRecipe> recipeType = RecipeType.create(EnergyControl.MODID, "kit_assembler", KitAssemblerRecipe.class);
	private static final Component title = Component.literal(I18n.get(ModItems.kit_assembler.get().getDescriptionId()));
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawableStatic arrow;
	private final IDrawableAnimated animatedarrow;

	public KitAssemblerRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(texture, 28, 16, 144, 54);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.kit_assembler.get()));
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
	public Component getTitle() {
		return title;
	}

	@Override
	public RecipeType<KitAssemblerRecipe> getRecipeType() {
		return recipeType;
	}

	/*@Override
	public void setIngredients(KitAssemblerRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
	}*/

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, KitAssemblerRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 34, 1).addItemStacks(getStackList(recipe.input1, recipe.count1));
		builder.addSlot(RecipeIngredientRole.INPUT, 34, 19).addItemStacks(getStackList(recipe.input2, recipe.count2));
		builder.addSlot(RecipeIngredientRole.INPUT, 34, 37).addItemStacks(getStackList(recipe.input3, recipe.count3));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 19).addItemStack(recipe.output);
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
	public void draw(KitAssemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
		animatedarrow.draw(matrixStack, 57, 19);
	}
}
