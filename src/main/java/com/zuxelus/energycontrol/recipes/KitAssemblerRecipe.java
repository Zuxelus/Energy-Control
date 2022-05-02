package com.zuxelus.energycontrol.recipes;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class KitAssemblerRecipe {
	private static List<KitAssemblerRecipe> recipes = new ArrayList<KitAssemblerRecipe>();
	private ResourceLocation id;
	public final ItemStack input1;
	public final ItemStack input2;
	public final ItemStack input3;
	public final ItemStack output;
	public final int time;

	public KitAssemblerRecipe(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack output, int time) {
		this.input1 = input1;
		this.input2 = input2;
		this.input3 = input3;
		this.output = output;
		this.time = time;
	}

	public boolean isSuitable(TileEntityKitAssembler te) {
		ItemStack stack1 = te.getStackInSlot(TileEntityKitAssembler.SLOT_CARD1);
		if (stack1 == null || stack1.stackSize < input1.stackSize || !input1.isItemEqual(stack1))
			return false;
		ItemStack stack2 = te.getStackInSlot(TileEntityKitAssembler.SLOT_ITEM);
		if (stack2 == null || stack2.stackSize < input2.stackSize || !input2.isItemEqual(stack2))
			return false;
		ItemStack stack3 = te.getStackInSlot(TileEntityKitAssembler.SLOT_CARD2);
		if (stack3 == null || stack3.stackSize < input3.stackSize || !input3.isItemEqual(stack3))
			return false;
		ItemStack result = te.getStackInSlot(TileEntityKitAssembler.SLOT_RESULT);
		if (result != null) {
			if (!result.isItemEqual(output))
				return false;
			if (result.stackSize + output.stackSize > result.getMaxStackSize())
				return false;
		}
		return true;
	}

	public static void addRecipe(KitAssemblerRecipe recipe) {
		recipes.add(recipe);
	}

	public static void addRecipe(Object input1, int count1, Object input2, int count2, Object input3, int count3, ItemStack output, int time) {
		List<ItemStack> input1list = new ArrayList<>();
		if (input1 instanceof ItemStack)
			input1list.add((ItemStack) input1);
		else if (input1 instanceof String)
			input1list.addAll(OreDictionary.getOres((String) input1));
		else
			return;
		List<ItemStack> input2list = new ArrayList<>();
		if (input2 instanceof ItemStack)
			input2list.add((ItemStack) input2);
		else if (input2 instanceof String)
			input2list.addAll(OreDictionary.getOres((String) input2));
		else
			return;
		List<ItemStack> input3list = new ArrayList<>();
		if (input3 instanceof ItemStack)
			input3list.add((ItemStack) input3);
		else if (input3 instanceof String)
			input3list.addAll(OreDictionary.getOres((String) input3));
		else
			return;
		for (ItemStack i1 : input1list)
			for (ItemStack i2 : input2list)
				for (ItemStack i3 : input3list) {
					i1.stackSize = count1;
					i2.stackSize = count2;
					i3.stackSize = count3;
					addRecipe(new KitAssemblerRecipe(i1, i2, i3, output, time));
				}
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