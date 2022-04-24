package com.zuxelus.energycontrol.crossmod.jei;

import com.zuxelus.energycontrol.EnergyControl;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class KitAssemblerRecipeCategory extends BlankRecipeCategory<KitAssemblerRecipeWrapper> {
	public static final String id = EnergyControl.MODID + ":kit_assembler";
	public static final ResourceLocation texture = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");
	private static final String title = I18n.format("tile.kit_assembler.name");
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawableStatic arrow;
	private final IDrawableAnimated animatedarrow;

	public KitAssemblerRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(texture, 28, 16, 144, 54);
		icon = guiHelper.createDrawable(texture, 2, 2, 28, 28);
		arrow = guiHelper.createDrawable(texture, 176, 0, 24, 17);
		animatedarrow = guiHelper.createAnimatedDrawable(arrow, 24, StartDirection.LEFT, false);
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return new DrawableIcon(texture, 50, 0, 14, 16, 64, 64);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getUid() {
		return id;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		animatedarrow.draw(minecraft, 57, 19);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, KitAssemblerRecipeWrapper wrapper, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0, true, 33, 0);
		stacks.init(1, true, 33, 18);
		stacks.init(2, true, 33, 36);
		stacks.init(3, false, 92, 18);
		stacks.set(ingredients);
		/*stacks.set(0, wrapper.getInputList(1));
		stacks.set(1, wrapper.getInputList(2));
		stacks.set(2, wrapper.getInputList(3));
		stacks.set(3, wrapper.getOutput());*/
	}
}
