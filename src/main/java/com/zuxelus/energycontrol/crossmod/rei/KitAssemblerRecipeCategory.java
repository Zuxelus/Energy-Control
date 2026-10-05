package com.zuxelus.energycontrol.crossmod.rei;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.init.ModItems;

import it.unimi.dsi.fastutil.ints.IntList;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.TransferRecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class KitAssemblerRecipeCategory implements TransferRecipeCategory<KitAssemblerDisplay> {
	public static final Identifier id = KitAssemblerDisplay.ID;
	private static final EntryStack icon = EntryStack.create(ModItems.kit_assembler);

	@Override
	public EntryStack getLogo() {
		return icon;
	}

	@Override
	public String getCategoryName() {
		return I18n.translate(ModItems.kit_assembler.getTranslationKey());
	}

	@Override
	public Identifier getIdentifier() {
		return id;
	}

	private static Point getStartPoint(Rectangle bounds) {
		return new Point(bounds.getCenterX() - 41, bounds.y + 7);
	}

	@Override
	public List<Widget> setupDisplay(KitAssemblerDisplay display, Rectangle bounds) {
		Point startPoint = getStartPoint(bounds);
		List<Widget> widgets = Lists.newArrayList();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 19)));
		widgets.add(Widgets.createArrow(new Point(startPoint.x + 24, startPoint.y + 18)).animationDurationTicks(display.getTime()));
		widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5), new LiteralText(String.format("%d ticks", display.getTime()))).noShadow().rightAligned().color(-12566464, -4473925));
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 19))
				.entries(display.getOutputEntries()).disableBackground().markOutput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y))
				.entries(display.getInputEntries().get(0)).markInput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 18))
				.entries(display.getInputEntries().get(1)).markInput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 36))
				.entries(display.getInputEntries().get(2)).markInput());
		return widgets;
	}

	// REI 5 leaves highlighting the missing inputs to the category; the indexes come from KitAssemblerTransferHandler
	@Override
	public void renderRedSlots(MatrixStack matrices, List<Widget> widgets, Rectangle bounds, KitAssemblerDisplay display, IntList redSlots) {
		Point startPoint = getStartPoint(bounds);
		matrices.push();
		matrices.translate(0, 0, 400);
		for (int i : redSlots) {
			int x = startPoint.x + 1;
			int y = startPoint.y + i * 18;
			DrawableHelper.fill(matrices, x, y, x + 16, y + 16, 0x60ff0000);
		}
		matrices.pop();
	}
}
