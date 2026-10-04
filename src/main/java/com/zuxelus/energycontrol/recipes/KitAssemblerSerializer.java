package com.zuxelus.energycontrol.recipes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class KitAssemblerSerializer implements RecipeSerializer<KitAssemblerRecipe> {
	// an ingredient object with an optional "count" next to "item"/"tag": { "item": "...", "count": 2 }
	// the count is decoded first because a field codec hands the whole object on to the ingredient codec
	private static final Codec<Pair<Integer, Ingredient>> COUNTED_INGREDIENT = Codec.pair(Codec.INT.optionalFieldOf("count", 1).codec(), Ingredient.DISALLOW_EMPTY_CODEC);

	private static final MapCodec<KitAssemblerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			COUNTED_INGREDIENT.fieldOf("input1").forGetter(recipe -> Pair.of(recipe.count1, recipe.input1)),
			COUNTED_INGREDIENT.fieldOf("input2").forGetter(recipe -> Pair.of(recipe.count2, recipe.input2)),
			COUNTED_INGREDIENT.fieldOf("input3").forGetter(recipe -> Pair.of(recipe.count3, recipe.input3)),
			ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
			Codec.INT.optionalFieldOf("time", 300).forGetter(recipe -> recipe.time)
		).apply(instance, (input1, input2, input3, output, time) -> new KitAssemblerRecipe(
			input1.getSecond(), input1.getFirst(), input2.getSecond(), input2.getFirst(), input3.getSecond(), input3.getFirst(), output, time)));

	private static final PacketCodec<RegistryByteBuf, KitAssemblerRecipe> PACKET_CODEC = PacketCodec.ofStatic(KitAssemblerSerializer::write, KitAssemblerSerializer::read);

	@Override
	public MapCodec<KitAssemblerRecipe> codec() {
		return CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, KitAssemblerRecipe> packetCodec() {
		return PACKET_CODEC;
	}

	private static KitAssemblerRecipe read(RegistryByteBuf buffer) {
		Ingredient input1 = Ingredient.PACKET_CODEC.decode(buffer);
		int count1 = buffer.readVarInt();
		Ingredient input2 = Ingredient.PACKET_CODEC.decode(buffer);
		int count2 = buffer.readVarInt();
		Ingredient input3 = Ingredient.PACKET_CODEC.decode(buffer);
		int count3 = buffer.readVarInt();
		ItemStack output = ItemStack.PACKET_CODEC.decode(buffer);
		int time = buffer.readInt();
		return new KitAssemblerRecipe(input1, count1, input2, count2, input3, count3, output, time);
	}

	private static void write(RegistryByteBuf buffer, KitAssemblerRecipe recipe) {
		Ingredient.PACKET_CODEC.encode(buffer, recipe.input1);
		buffer.writeVarInt(recipe.count1);
		Ingredient.PACKET_CODEC.encode(buffer, recipe.input2);
		buffer.writeVarInt(recipe.count2);
		Ingredient.PACKET_CODEC.encode(buffer, recipe.input3);
		buffer.writeVarInt(recipe.count3);
		ItemStack.PACKET_CODEC.encode(buffer, recipe.output);
		buffer.writeInt(recipe.time);
	}
}
