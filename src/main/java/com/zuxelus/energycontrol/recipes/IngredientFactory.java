package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class IngredientFactory implements IIngredientFactory {

	@Override
	public Ingredient parse(JsonContext context, JsonObject json) {
		return new IngredientNBT(getItemStack(json, context));
	}

	private static ItemStack getItemStack(JsonObject json, JsonContext context) {
		String itemName = context.appendModId(JsonUtils.getString(json, "item"));

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

		if (item == null)
			throw new JsonSyntaxException("Unknown item '" + itemName + "'");

		if (item.getHasSubtypes() && !json.has("data"))
			throw new JsonParseException("Missing data for item '" + itemName + "'");

		if (json.has("nbt")) {
			// Lets hope this works? Needs test
			try {
				JsonElement element = json.get("nbt");
				NBTTagCompound nbt;
				if (element.isJsonObject())
					nbt = JsonToNBT.getTagFromJson(CraftingHelper.GSON.toJson(element));
				else
					nbt = JsonToNBT.getTagFromJson(JsonUtils.getString(element, "nbt"));

				NBTTagCompound nbt2 = nbt.copy();
				for (String name : nbt2.getKeySet()) {
					NBTBase tag = nbt2.getTag(name);
					if (tag.getId() == 8) {// String
						String text = nbt.getString(name);
						if (text.length() > 1 && text.endsWith("b")) {
							text = text.substring(0, text.length() - 1);
							boolean isByte = true;
							for (char ch : text.toCharArray()) {
								isByte = isByte && Character.isDigit(ch);
							}
							if (isByte) {
								nbt.removeTag(name);
								nbt.setByte(name, Byte.parseByte(text));
							}
						}
					}
				}

				NBTTagCompound tmp = new NBTTagCompound();
				if (nbt.hasKey("ForgeCaps")) {
					tmp.setTag("ForgeCaps", nbt.getTag("ForgeCaps"));
					nbt.removeTag("ForgeCaps");
				}

				tmp.setTag("tag", nbt);
				tmp.setString("id", itemName);
				tmp.setInteger("Count", JsonUtils.getInt(json, "count", 1));
				tmp.setInteger("Damage", JsonUtils.getInt(json, "data", 0));

				return new ItemStack(tmp);
			} catch (NBTException e) {
				throw new JsonSyntaxException("Invalid NBT Entry: " + e);
			}
		}

		return new ItemStack(item, JsonUtils.getInt(json, "count", 1), JsonUtils.getInt(json, "data", 0));
	}

	private static class IngredientNBT extends Ingredient {
		private final ItemStack stack;

		public IngredientNBT(ItemStack stack) {
			super(stack);
			this.stack = stack;
		}

		@Override
		public boolean apply(@Nullable ItemStack input) {
			if (input == null)
				return false;
			return this.stack.getItem() == input.getItem() && this.stack.getItemDamage() == input.getItemDamage()
					&& ItemStack.areItemStackShareTagsEqual(this.stack, input);
		}

		@Override
		public boolean isSimple() {
			return false;
		}
	}
}
