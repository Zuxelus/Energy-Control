package com.zuxelus.energycontrol.crossmod.rei;

import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class KitAssemblerDisplay extends BasicDisplay implements SimpleGridMenuDisplay {
	// kept here, not in the client-only KitAssemblerRecipeCategory, because the server plugin needs it too
	public static final CategoryIdentifier<KitAssemblerDisplay> ID = CategoryIdentifier.of(EnergyControl.MODID, "kit_assembler");
	public final int count1;
	public final int count2;
	public final int count3;
	private int time;

	public KitAssemblerDisplay(KitAssemblerRecipe recipe) {
		super(List.of(withCount(recipe.input1, recipe.count1), withCount(recipe.input2, recipe.count2), withCount(recipe.input3, recipe.count3)),
				Collections.singletonList(EntryIngredients.of(recipe.output)));
		this.count1 = recipe.count1;
		this.count2 = recipe.count2;
		this.count3 = recipe.count3;
		time = recipe.time;
	}

	public KitAssemblerDisplay(List<EntryIngredient> input, List<EntryIngredient> output, int count1, int count2, int count3, int time) {
		super(input, output);
		this.count1 = count1;
		this.count2 = count2;
		this.count3 = count3;
		this.time = time;
	}

	// shows the required amount on each ingredient (the recipe keeps counts separately from its ingredients)
	private static EntryIngredient withCount(Ingredient ingredient, int count) {
		return EntryIngredients.ofIngredient(ingredient).map(stack -> EntryStacks.of(stack.<ItemStack>castValue().copyWithCount(count)));
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return ID;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public int getHeight() {
		return 3;
	}

	public int getTime() {
		return this.time;
	}

	@Override
	public DisplaySerializer<KitAssemblerDisplay> getSerializer() {
		return SERIALIZER;
	}

	// displays are made on the server and synced to the client
	public static final DisplaySerializer<KitAssemblerDisplay> SERIALIZER = DisplaySerializer.of(
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(KitAssemblerDisplay::getInputEntries),
			EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(KitAssemblerDisplay::getOutputEntries),
			Codec.INT.fieldOf("count1").forGetter(display -> display.count1),
			Codec.INT.fieldOf("count2").forGetter(display -> display.count2),
			Codec.INT.fieldOf("count3").forGetter(display -> display.count3),
			Codec.INT.fieldOf("time").forGetter(KitAssemblerDisplay::getTime)
		).apply(instance, KitAssemblerDisplay::new)),
		StreamCodec.composite(
			EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), KitAssemblerDisplay::getInputEntries,
			EntryIngredient.streamCodec().apply(ByteBufCodecs.list()), KitAssemblerDisplay::getOutputEntries,
			ByteBufCodecs.VAR_INT, display -> display.count1,
			ByteBufCodecs.VAR_INT, display -> display.count2,
			ByteBufCodecs.VAR_INT, display -> display.count3,
			ByteBufCodecs.VAR_INT, KitAssemblerDisplay::getTime,
			KitAssemblerDisplay::new));
}