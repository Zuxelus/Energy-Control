package com.zuxelus.energycontrol.recipes;

import java.util.Vector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.*;

public class StorageArrayRecipe implements CraftingRecipe {
	private final ShapelessRecipe recipe;

	public StorageArrayRecipe(ShapelessRecipe internal) {
		this.recipe = internal;
	}

	public ShapelessRecipe getRecipe() {
		return recipe;
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		return !assemble(inv).isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		int inventoryLength = inv.size();
		int cardCount = 0;
		int arrayCount = 0;
		int cardCountLiquid = 0;
		int arrayCountLiquid = 0;
		Vector<ItemStack> arrays = new Vector<>();
		Vector<ItemStack> cards = new Vector<>();
		for (int i = 0; i < inventoryLength; i++) {
			ItemStack itemStack = inv.getItem(i);
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
				arrays.add(itemStack);
				arrayCount++;
			} else if (item instanceof ItemCardLiquidArray) {
				arrays.add(itemStack);
				arrayCountLiquid++;
			}
		}
		if ((cardCount + arrayCount) > 0 && (cardCountLiquid + arrayCountLiquid) > 0)
			return ItemStack.EMPTY;

		ItemStack stack = getCraftingResult(cardCount, arrayCount, ItemCardType.CARD_ENERGY_ARRAY, cards, arrays);
		if (!stack.isEmpty())
			return stack;
		return getCraftingResult(cardCountLiquid, arrayCountLiquid, ItemCardType.CARD_LIQUID_ARRAY, cards, arrays);
	}

	private ItemStack getCraftingResult(int cardCount, int arrayCount, int type, Vector<ItemStack> cards, Vector<ItemStack> arrays) {
		if (cardCount >= 2 && cardCount <= 16 && arrayCount == 0) {
			ItemStack itemStack = createCard(type);
			initArray(itemStack, cards);
			return itemStack;
		}
		if (cardCount == 0 && arrayCount == 1) {
			int cnt = new ItemCardReader(arrays.get(0)).getInt("cardCount");
			if (cnt > 0)
				return new ItemStack(ModItems.radio_transmitter, cnt);
		} else if (arrayCount >= 1 && cardCount + arrayCount >= 2) {
			// merge all arrays and cards into the first array
			int cnt = cardCount;
			for (ItemStack array : arrays)
				cnt += new ItemCardReader(array).getInt("cardCount");
			if (cnt <= 16) {
				ItemStack itemStack = createCard(type);
				ItemStackHelper.setTag(itemStack, ItemStackHelper.getTagCompound(arrays.get(0)));
				for (int i = 1; i < arrays.size(); i++)
					appendArray(itemStack, arrays.get(i));
				initArray(itemStack, cards);
				return itemStack;
			}
		}
		return ItemStack.EMPTY;
	}

	private static void appendArray(ItemStack stack, ItemStack array) {
		ItemCardReader reader = new ItemCardReader(stack);
		ItemCardReader source = new ItemCardReader(array);
		int cardCount = reader.getCardCount();
		for (int i = 0; i < source.getCardCount(); i++) {
			reader.setInt(String.format("_%dx", cardCount), source.getInt(String.format("_%dx", i)));
			reader.setInt(String.format("_%dy", cardCount), source.getInt(String.format("_%dy", i)));
			reader.setInt(String.format("_%dz", cardCount), source.getInt(String.format("_%dz", i)));
			cardCount++;
		}
		reader.setInt("cardCount", cardCount);
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
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public RecipeSerializer<StorageArrayRecipe> getSerializer() {
		return ModItems.ARRAY_SERIALIZER;
	}
}
