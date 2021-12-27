package com.zuxelus.energycontrol.crossmod.rei;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class KitAssemblerRecipeCategory implements DisplayCategory<KitAssemblerDisplay> {
	public static final CategoryIdentifier<KitAssemblerDisplay> id = CategoryIdentifier.of(EnergyControl.MODID,
			"kit_assembler");
	private static final Text title = new TranslatableText(ModItems.kit_assembler.getTranslationKey());
	private static final Renderer icon = EntryStacks.of(ModItems.kit_assembler);

	@Override
	public Renderer getIcon() {
		return icon;
	}

	@Override
	public Text getTitle() {
		return title;
	}

	@Override
	public CategoryIdentifier<? extends KitAssemblerDisplay> getCategoryIdentifier() {
		return id;
	}

	@Override
	public List<Widget> setupDisplay(KitAssemblerDisplay display, Rectangle bounds) {
		Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 7);
		List<Widget> widgets = Lists.newArrayList();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 19)));
		widgets.add(Widgets.createArrow(new Point(startPoint.x + 24, startPoint.y + 18)).animationDurationTicks(display.getTime()));
		widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5), new LiteralText(String.format("%d ticks", display.getTime()))).noShadow().rightAligned().color(-12566464, -4473925));
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 19))
				.entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y))
				.entries(display.getInputEntries().get(0)).markInput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 18))
				.entries(display.getInputEntries().get(1)).markInput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 36))
				.entries(display.getInputEntries().get(2)).markInput());
		return widgets;
	}
}
