package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class KitAssemblerSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<KitAssemblerRecipe> {

	@Override
	public KitAssemblerRecipe fromJson(ResourceLocation id, JsonObject json) {
		Ingredient input1 = getIngredient(json, "input1");
		int count1 = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(json, "input1"), "count", 1);
		Ingredient input2 = getIngredient(json, "input2");
		int count2 = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(json, "input2"), "count", 1);
		Ingredient input3 = getIngredient(json, "input3");
		int count3 = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(json, "input3"), "count", 1);
		ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
		int time = GsonHelper.getAsInt(json, "time", 300);
		return new KitAssemblerRecipe(id, input1, count1, input2, count2, input3, count3, output, time);
	}

	private Ingredient getIngredient(JsonObject json, String name) {
		return Ingredient.fromJson(GsonHelper.isArrayNode(json, name) ?
				GsonHelper.getAsJsonArray(json, name) :
				GsonHelper.getAsJsonObject(json, name));
	}

	@Override
	public KitAssemblerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
		Ingredient input1 = Ingredient.fromNetwork(buffer);
		int count1 = buffer.readVarInt();
		Ingredient input2 = Ingredient.fromNetwork(buffer);
		int count2 = buffer.readVarInt();
		Ingredient input3 = Ingredient.fromNetwork(buffer);
		int count3 = buffer.readVarInt();
		ItemStack output = buffer.readItem();
		int time = buffer.readInt();
		return new KitAssemblerRecipe(id, input1, count1, input2, count2, input3, count3, output, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, KitAssemblerRecipe recipe) {
		recipe.input1.toNetwork(buffer);
		buffer.writeVarInt(recipe.count1);
		recipe.input2.toNetwork(buffer);
		buffer.writeVarInt(recipe.count2);
		recipe.input3.toNetwork(buffer);
		buffer.writeVarInt(recipe.count3);
		buffer.writeItem(recipe.output);
		buffer.writeInt(recipe.time);
	}
}
