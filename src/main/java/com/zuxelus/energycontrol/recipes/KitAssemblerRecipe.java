package com.zuxelus.energycontrol.recipes;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.recipes.EmptyInventory;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.world.item.crafting.Recipe;


import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KitAssemblerRecipe implements Recipe<EmptyInventory> {
	public record CountedIngredient(Ingredient ingredient, int count) {
		public static final Codec<CountedIngredient> CODEC = RecordCodecBuilder.create(i -> i.group(
				Ingredient.CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
				Codec.INT.optionalFieldOf("count", 1).forGetter(CountedIngredient::count)
			).apply(i, CountedIngredient::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, CountedIngredient> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, CountedIngredient::ingredient,
				ByteBufCodecs.VAR_INT, CountedIngredient::count,
				CountedIngredient::new);
	}

	public static final MapCodec<KitAssemblerRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			CountedIngredient.CODEC.fieldOf("input1").forGetter(o -> new CountedIngredient(o.input1, o.count1)),
			CountedIngredient.CODEC.fieldOf("input2").forGetter(o -> new CountedIngredient(o.input2, o.count2)),
			CountedIngredient.CODEC.fieldOf("input3").forGetter(o -> new CountedIngredient(o.input3, o.count3)),
			ItemStack.CODEC.fieldOf("result").forGetter(o -> o.result),
			Codec.INT.optionalFieldOf("time", 300).forGetter(o -> o.time)
		).apply(i, KitAssemblerRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, KitAssemblerRecipe> STREAM_CODEC = StreamCodec.composite(
			CountedIngredient.STREAM_CODEC, o -> new CountedIngredient(o.input1, o.count1),
			CountedIngredient.STREAM_CODEC, o -> new CountedIngredient(o.input2, o.count2),
			CountedIngredient.STREAM_CODEC, o -> new CountedIngredient(o.input3, o.count3),
			ItemStack.STREAM_CODEC, o -> o.result,
			ByteBufCodecs.INT, o -> o.time,
			KitAssemblerRecipe::new);
	public static final RecipeSerializer<KitAssemblerRecipe> SERIALIZER = new RecipeSerializer<KitAssemblerRecipe>() {
        public MapCodec<KitAssemblerRecipe> codec() { return MAP_CODEC; }
        public StreamCodec<RegistryFriendlyByteBuf, KitAssemblerRecipe> streamCodec() { return STREAM_CODEC; }
    };

	public final Ingredient input1;
	public final Ingredient input2;
	public final Ingredient input3;
	public final int count1;
	public final int count2;
	public final int count3;
	public final ItemStack result;
	public final int time;

	public KitAssemblerRecipe(CountedIngredient input1, CountedIngredient input2, CountedIngredient input3, ItemStack result, int time) {
		this.input1 = input1.ingredient();
		this.count1 = input1.count();
		this.input2 = input2.ingredient();
		this.count2 = input2.count();
		this.input3 = input3.ingredient();
		this.count3 = input3.count();
		this.result = result;
		this.time = time;
	}

	public ItemStack getOutput() {
		return result.copy();
	}

	public boolean isSuitable(TileEntityKitAssembler te) {
		ItemStack stack1 = te.getItem(TileEntityKitAssembler.SLOT_CARD1);
		if (stack1.isEmpty() || stack1.getCount() < count1 || !input1.test(stack1))
			return false;
		ItemStack stack2 = te.getItem(TileEntityKitAssembler.SLOT_ITEM);
		if (stack2.isEmpty() || stack2.getCount() < count2 || !input2.test(stack2))
			return false;
		ItemStack stack3 = te.getItem(TileEntityKitAssembler.SLOT_CARD2);
		if (stack3.isEmpty() || stack3.getCount() < count3 || !input3.test(stack3))
			return false;
		ItemStack result = te.getItem(TileEntityKitAssembler.SLOT_RESULT);
		if (!result.isEmpty()) {
			if (!result.is(this.result.getItem()))
				return false;
			if (result.getCount() + this.result.getCount() > result.getMaxStackSize())
				return false;
		}
		return true;
	}

	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, input1, input2, input3);
	}

	@Override
	public boolean matches(EmptyInventory inv, Level world) {
		return false;
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
	public String getGroup() {
		return "";
	}

	@Override
	public ItemStack assemble(EmptyInventory inv, HolderLookup.Provider registries) {
		return getOutput();
	}



    @Override public boolean canCraftInDimensions(int width, int height) { return false; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return getOutput(); }
	@Override
	public RecipeSerializer<KitAssemblerRecipe> getSerializer() {
		return ModItems.KIT_ASSEMBLER_SERIALIZER.get();
	}

	@Override
	public RecipeType<KitAssemblerRecipe> getType() {
		return KitAssemblerRecipeType.TYPE;
	}
}
