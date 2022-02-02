package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class KitAssemblerSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<KitAssemblerRecipe> {

	@Override
	public KitAssemblerRecipe read(ResourceLocation id, JsonObject json) {
		Ingredient input1 = getIngredient(json, "input1");
		int count1 = JSONUtils.getInt(JSONUtils.getJsonObject(json, "input1"), "count", 1);
		Ingredient input2 = getIngredient(json, "input2");
		int count2 = JSONUtils.getInt(JSONUtils.getJsonObject(json, "input2"), "count", 1);
		Ingredient input3 = getIngredient(json, "input3");
		int count3 = JSONUtils.getInt(JSONUtils.getJsonObject(json, "input3"), "count", 1);
		ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
		int time = JSONUtils.getInt(json, "time", 300);
		return new KitAssemblerRecipe(id, input1, count1, input2, count2, input3, count3, output, time);
	}

	private Ingredient getIngredient(JsonObject json, String name) {
		return Ingredient.deserialize(JSONUtils.isJsonArray(json, name) ?
				JSONUtils.getJsonArray(json, name) :
				JSONUtils.getJsonObject(json, name));
	}

	@Override
	public KitAssemblerRecipe read(ResourceLocation id, PacketBuffer buffer) {
		Ingredient input1 = Ingredient.read(buffer);
		int count1 = buffer.readVarInt();
		Ingredient input2 = Ingredient.read(buffer);
		int count2 = buffer.readVarInt();
		Ingredient input3 = Ingredient.read(buffer);
		int count3 = buffer.readVarInt();
		ItemStack output = buffer.readItemStack();
		int time = buffer.readInt();
		return new KitAssemblerRecipe(id, input1, count1, input2, count2, input3, count3, output, time);
	}

	@Override
	public void write(PacketBuffer buffer, KitAssemblerRecipe recipe) {
		recipe.input1.write(buffer);
		buffer.writeVarInt(recipe.count1);
		recipe.input2.write(buffer);
		buffer.writeVarInt(recipe.count2);
		recipe.input3.write(buffer);
		buffer.writeVarInt(recipe.count3);
		buffer.writeItemStack(recipe.output);
		buffer.writeInt(recipe.time);
	}
}
