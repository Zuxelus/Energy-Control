package com.zuxelus.energycontrol.recipes;

import java.util.Vector;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StorageArrayRecipe implements CraftingRecipe {
	private final ShapelessRecipe recipe;

	public StorageArrayRecipe(ShapelessRecipe internal) {
		this.recipe = internal;
	}

	public ShapelessRecipe getRecipe() {
		return recipe;
	}

	@Override
	public boolean matches(CraftingRecipeInput inv, World level) {
		return !craft(inv, level.getRegistryManager()).isEmpty();
	}

	@Override
	public ItemStack craft(CraftingRecipeInput inv, RegistryWrapper.WrapperLookup registryManager) {
		int inventoryLength = inv.getSize();
		int cardCount = 0;
		int arrayCount = 0;
		int cardCountLiquid = 0;
		int arrayCountLiquid = 0;
		ItemStack array = null;
		Vector<ItemStack> cards = new Vector<>();
		for (int i = 0; i < inventoryLength; i++) {
			ItemStack itemStack = inv.getStackInSlot(i);
			if (itemStack.isEmpty())
				continue;
			Item item = itemStack.getItem();
			if (!(item instanceof ItemCardMain))
				return ItemStack.EMPTY;
			if (item instanceof ItemCardEnergy) {
				cards.add(itemStack);
				cardCount++;
			} else if (item instanceof ItemCardLiquid) {
				cards.add(itemStack);
				cardCountLiquid++;
			} else if (item instanceof ItemCardEnergyArray) {
				array = itemStack;				
				arrayCount++;
			} else if (item instanceof ItemCardLiquidArray) {
				array = itemStack;
				arrayCountLiquid++;
			}
		}
		if ((cardCount + arrayCount) > 0 && (cardCountLiquid + arrayCountLiquid) > 0)
			return ItemStack.EMPTY;

		ItemStack stack = getCraftingResult(cardCount, arrayCount, ItemCardType.CARD_ENERGY_ARRAY, cards, array);
		if (!stack.isEmpty())
			return stack;
		return getCraftingResult(cardCountLiquid, arrayCountLiquid, ItemCardType.CARD_LIQUID_ARRAY, cards, array);
	}

	private ItemStack getCraftingResult(int cardCount, int arrayCount, int type, Vector<ItemStack> cards, ItemStack array) {
		if (cardCount >= 2 && cardCount <= 16 && arrayCount == 0) {
			ItemStack itemStack = createCard(type);
			initArray(itemStack, cards);
			return itemStack;
		}
		if (cardCount == 0 && arrayCount == 1) {
			int cnt = new ItemCardReader(array).getInt("cardCount");
			if (cnt > 0)
				return new ItemStack(ModItems.radio_transmitter, cnt);
		} else if (arrayCount == 1 && cardCount > 0) {
			int cnt = new ItemCardReader(array).getInt("cardCount");
			if (cnt + cardCount <= 16) {
				ItemStack itemStack = createCard(type);
				ItemStackHelper.setTag(itemStack, ItemStackHelper.getTagCompound(array));
				initArray(itemStack, cards);
				return itemStack;
			}
		}
		return ItemStack.EMPTY;
	}

	private ItemStack createCard(int type) {
		if (type == ItemCardType.CARD_ENERGY_ARRAY)
			return new ItemStack(ModItems.card_energy_array);
		//if (type == ItemCardType.CARD_LIQUID_ARRAY)
		return new ItemStack(ModItems.card_liquid_array);
	}

	private static void initArray(ItemStack stack, Vector<ItemStack> cards) {
		if (!(stack.getItem() instanceof ItemCardEnergyArray) && !(stack.getItem() instanceof ItemCardLiquidArray))
			return;
		ItemCardReader reader = new ItemCardReader(stack);
		int cardCount = reader.getCardCount();
		for (ItemStack subCard : cards) {
			ItemCardReader wrapper = new ItemCardReader(subCard);
			BlockPos target = wrapper.getTarget();
			if (target == null)
				continue;
			reader.setInt(String.format("_%dx", cardCount), target.getX());
			reader.setInt(String.format("_%dy", cardCount), target.getY());
			reader.setInt(String.format("_%dz", cardCount), target.getZ());
			cardCount++;
		}
		reader.setInt("cardCount", cardCount);
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return CraftingRecipeCategory.MISC;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registryManager) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.ARRAY_SERIALIZER;
	}
}
