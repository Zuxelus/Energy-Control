package com.zuxelus.energycontrol.recipes;

import java.util.Vector;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public class StorageArrayRecipe implements IRecipe {
	static {
		RecipeSorter.register("EnergyControl:storagearrayRecipe", StorageArrayRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}

	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		return getCraftingResult(inventory) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		int inventoryLength = inventory.getSizeInventory();
		int cardCount = 0;
		int arrayCount = 0;
		int cardCountLiquid = 0;
		int arrayCountLiquid = 0;
		ItemStack array = null;
		Vector<ItemStack> cards = new Vector<>();
		for (int i = 0; i < inventoryLength; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;
			if (!ItemCardMain.isCard(stack))
				return null;

			switch (stack.getItemDamage())
			{
			case ItemCardType.CARD_ENERGY:
				cards.add(stack);
				cardCount++;
				break;
			case ItemCardType.CARD_LIQUID:
				cards.add(stack);
				cardCountLiquid++;
				break;
			case ItemCardType.CARD_ENERGY_ARRAY:
				array = stack;
				arrayCount++;
				break;
			case ItemCardType.CARD_LIQUID_ARRAY:
				array = stack;
				arrayCountLiquid++;
				break;
			}
		}

		if ((cardCount + arrayCount) != 0 && (cardCountLiquid + arrayCountLiquid) != 0)
			return null;

		ItemStack stack = getCraftingResult(cardCount, arrayCount, ItemCardType.CARD_ENERGY_ARRAY, cards, array);
		if (stack != null)
			return stack;
		return getCraftingResult(cardCountLiquid, arrayCountLiquid, ItemCardType.CARD_LIQUID_ARRAY, cards, array);
	}

	private ItemStack getCraftingResult(int cardCount, int arrayCount, int type, Vector<ItemStack> cards, ItemStack array) {
		if (cardCount >= 2 && cardCount <= 16 && arrayCount == 0) {
			ItemStack itemStack = new ItemStack(ModItems.itemCard, 1, type);
			initArray(itemStack, cards);
			return itemStack;
		}
		if (cardCount == 0 && arrayCount == 1) {
			int cnt = new ItemCardReader(array).getInt("cardCount");
			if (cnt > 0)
				return new ItemStack(ModItems.itemComponent, cnt, ItemComponent.BASIC_CIRCUIT);
		} else if (arrayCount == 1 && cardCount > 0) {
			int cnt = new ItemCardReader(array).getInt("cardCount");
			if (cnt + cardCount <= 16) {
				ItemStack itemStack = new ItemStack(ModItems.itemCard, 1, type);
				itemStack.setTagCompound((NBTTagCompound) array.getTagCompound().copy());
				initArray(itemStack, cards);
				return itemStack;
			}
		}
		return null;
	}

	private static void initArray(ItemStack stack, Vector<ItemStack> cards) {
		if (stack.getItemDamage() != ItemCardType.CARD_ENERGY_ARRAY
				&& stack.getItemDamage() != ItemCardType.CARD_LIQUID_ARRAY
				&& stack.getItemDamage() != ItemCardType.CARD_GENERATOR_ARRAY)
			return;
		ItemCardReader reader = new ItemCardReader(stack);
		int cardCount = reader.getCardCount();
		for (ItemStack subCard : cards) {
			ItemCardReader wrapper = new ItemCardReader(subCard);
			ChunkCoordinates target = wrapper.getTarget();
			if (target == null)
				continue;
			reader.setInt(String.format("_%dx", cardCount), target.posX);
			reader.setInt(String.format("_%dy", cardCount), target.posY);
			reader.setInt(String.format("_%dz", cardCount), target.posZ);
			//reader.setInt(String.format("_%dtargetType", cardCount), wrapper.getInt("targetType"));
			cardCount++;
		}
		reader.setInt("cardCount", cardCount);
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}
}