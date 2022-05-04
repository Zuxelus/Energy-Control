package com.zuxelus.energycontrol.crossmod.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class KitAssemblerRecipeHandler extends TemplateRecipeHandler {
	int ticks;

	public class CachedKitAssemblerRecipe extends TemplateRecipeHandler.CachedRecipe {
		public PositionedStack output;
		public List<PositionedStack> ingredients = new ArrayList();

		public CachedKitAssemblerRecipe(ItemStack stack1, ItemStack stack2, ItemStack stack3, ItemStack output) {
			super();
			ingredients.add(new PositionedStack(stack1, 57, 2));
			ingredients.add(new PositionedStack(stack2, 57, 2 + 18));
			ingredients.add(new PositionedStack(stack3, 57, 2 + 18 * 2));
			this.output = new PositionedStack(output, 116, 20);
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(KitAssemblerRecipeHandler.this.cycleticks / 20, ingredients);
		}

		@Override
		public PositionedStack getResult() {
			return output;
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiKitAssembler.class;
	}

	@Override
	public String getGuiTexture() {
		return EnergyControl.MODID + ":textures/gui/gui_kit_assembler.png";
	}

	public String getRecipeId() {
		return EnergyControl.MODID + ".kit_assembler";
	}

	@Override
	public String getRecipeName() {
		return "Kit Assembler";
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (KitAssemblerRecipe recipe : KitAssemblerRecipe.getRecipes())
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.output, result))
				arecipes.add(new CachedKitAssemblerRecipe(recipe.input1, recipe.input2, recipe.input3, recipe.output));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals(getRecipeId())) {
			for (KitAssemblerRecipe recipe : KitAssemblerRecipe.getRecipes())
				arecipes.add(new CachedKitAssemblerRecipe(recipe.input1, recipe.input2, recipe.input3, recipe.output));
		} else
			super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (KitAssemblerRecipe recipe : KitAssemblerRecipe.getRecipes()) 
			if (recipe.input1.getItem() == ingredient.getItem() || recipe.input2.getItem() == ingredient.getItem() || recipe.input3.getItem() == ingredient.getItem())
				arecipes.add(new CachedKitAssemblerRecipe(recipe.input1, recipe.input2, recipe.input3, recipe.output));
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(81, 24, 25, 16), getRecipeId(), new Object[0]));
	}

	@Override
	public void drawBackground(int i) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiDraw.changeTexture(getGuiTexture());
		GuiDraw.drawTexturedModalRect(20, 1, 25, 16, 130, 65);
	}

	@Override
	public void drawExtras(int i) {
		float f = ticks >= 20 ? (ticks - 20) % 20 / 20.0F : 0.0F;
		drawProgressBar(81, 20, 176, 0, 25, 16, f, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		ticks += 1;
	}
}
