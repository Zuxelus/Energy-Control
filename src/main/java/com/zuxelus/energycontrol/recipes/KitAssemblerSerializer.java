package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class KitAssemblerSerializer implements RecipeSerializer<KitAssemblerRecipe> {

	@Override
	public KitAssemblerRecipe read(Identifier id, JsonObject json) {
		Ingredient input1 = getIngredient(json, "input1");
		int count1 = JsonHelper.getInt(JsonHelper.getObject(json, "input1"), "count", 1);
		Ingredient input2 = getIngredient(json, "input2");
		int count2 = JsonHelper.getInt(JsonHelper.getObject(json, "input2"), "count", 1);
		Ingredient input3 = getIngredient(json, "input3");
		int count3 = JsonHelper.getInt(JsonHelper.getObject(json, "input3"), "count", 1);
		ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
		int time = JsonHelper.getInt(json, "time", 300);
		return new KitAssemblerRecipe(id, input1, count1, input2, count2, input3, count3, output, time);
	}

	private Ingredient getIngredient(JsonObject json, String name) {
		return Ingredient.fromJson(JsonHelper.hasArray(json, name) ? JsonHelper.getArray(json, name) : JsonHelper.getObject(json, name));
	}

	@Override
	public KitAssemblerRecipe read(Identifier id, PacketByteBuf buffer) {
		Ingredient input1 = Ingredient.fromPacket(buffer);
		int count1 = buffer.readVarInt();
		Ingredient input2 = Ingredient.fromPacket(buffer);
		int count2 = buffer.readVarInt();
		Ingredient input3 = Ingredient.fromPacket(buffer);
		int count3 = buffer.readVarInt();
		ItemStack output = buffer.readItemStack();
		int time = buffer.readInt();
		return new KitAssemblerRecipe(id, input1, count1, input2, count2, input3, count3, output, time);
	}

	@Override
	public void write(PacketByteBuf buffer, KitAssemblerRecipe recipe) {
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
