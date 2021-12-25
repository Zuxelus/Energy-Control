package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiKitAssembler extends GuiContainerBase<ContainerKitAssembler> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");

	private ContainerKitAssembler container;

	public GuiKitAssembler(ContainerKitAssembler container, PlayerInventory inventory, Text title) {
		super(container, inventory, title, TEXTURE);
		this.container = container;
		backgroundHeight = 182;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		Slot slot = container.getSlot(TileEntityKitAssembler.SLOT_INFO);
		if (isPointWithinBounds(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot.isEnabled())
			renderInfoToolTip(matrixStack, slot, mouseX, mouseY);
		else
			drawMouseoverTooltip(matrixStack, mouseX, mouseY);
		if (isPointWithinBounds(165, 16, 4, 52, mouseX, mouseY))
			renderTooltip(matrixStack, new LiteralText(String.format("%d FE/%d FE", (int) container.te.getEnergy(), TileEntityKitAssembler.CAPACITY)), mouseX, mouseY);
	}

	private void renderInfoToolTip(MatrixStack matrixStack, Slot slot, int x, int y) {
		ItemStack stack = slot.getStack();
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
			return;
		List<Text> stackList = stack.getTooltip(client.player, client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
		List<Text> list = Lists.<Text>newArrayList();
		if (stackList.size() > 0)
			list.add(stackList.get(0));
		List<PanelString> data = new ItemCardReader(stack).getAllData();
		if (data != null)
			for (PanelString panelString : data) {
				if (panelString.textLeft != null)
					list.add(new LiteralText(Formatting.GRAY + panelString.textLeft));
			}
		renderTooltip(matrixStack, list, stack.getTooltipData(), x, y);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackground(matrixStack, partialTicks, mouseX, mouseY);

		int energyHeight = container.te.getEnergyFactor();
		if (energyHeight > 0)
			drawTexture(matrixStack, x + 165, y + 16 + (52 - energyHeight), 176, 17 + 52 - energyHeight, 4, energyHeight);
		int productionWidth = container.te.getProductionFactor();
		if (productionWidth > 0)
			drawTexture(matrixStack, x + 86, y + 35, 176, 0, productionWidth, 17);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, backgroundWidth, 6);
	}
}
