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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiKitAssembler extends GuiContainerBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");

	private ContainerKitAssembler container;

	public GuiKitAssembler(ContainerKitAssembler container) {
		super(container, "tile.kit_assembler.name", TEXTURE);
		this.container = container;
		ySize = 182;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (isPointInRegion(165, 16, 4, 52, mouseX, mouseY))
			drawCreativeTabHoveringText(String.format("%d EU/%d EU", (int) container.te.getEnergy(), TileEntityKitAssembler.CAPACITY), mouseX, mouseY);
	}

	@Override
	protected void renderToolTip(ItemStack stack, int mouseX, int mouseY) {
		Slot slot = container.getSlot(TileEntityKitAssembler.SLOT_INFO);
		if (isPointInRegion(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY) && slot.canBeHovered())
			renderInfoToolTip(slot, mouseX, mouseY);
		else
			super.renderToolTip(stack, mouseX, mouseY);
	}

	private void renderInfoToolTip(Slot slot, int x, int y) {
		ItemStack stack = slot.getStack();
		if (!ItemCardMain.isCard(stack))
			return;
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		List<String> stackList = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
		List<String> list = Lists.newArrayList();
		if (stackList.size() > 0)
			list.add(stackList.get(0));
		List<PanelString> data = new ItemCardReader(stack).getAllData();
		if (data != null)
			for (PanelString panelString : data) {
				if (panelString.textLeft != null)
					list.add(TextFormatting.GRAY + panelString.textLeft);
			}
		drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
		net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		int energyHeight = container.te.getEnergyFactor();
		if (energyHeight > 0)
			drawTexturedModalRect(guiLeft + 165, guiTop + 16 + (52 - energyHeight), 176, 17 + 52 - energyHeight, 4, energyHeight);
		int productionWidth = container.te.getProductionFactor();
		if (productionWidth > 0)
			drawTexturedModalRect(guiLeft + 86, guiTop + 35, 176, 0, productionWidth, 17);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(name, xSize, 6);
	}
}
