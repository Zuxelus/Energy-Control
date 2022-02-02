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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiKitAssembler extends GuiContainerBase<ContainerKitAssembler> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");

	private ContainerKitAssembler container;

	public GuiKitAssembler(ContainerKitAssembler container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		this.container = container;
		ySize = 182;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		Slot slot = container.getSlot(TileEntityKitAssembler.SLOT_INFO);
		if (isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY) && slot.isEnabled())
			renderInfoToolTip(slot, mouseX, mouseY);
		else
			renderHoveredToolTip(mouseX, mouseY);
		if (isPointInRegion(165, 16, 4, 52, mouseX, mouseY))
			renderTooltip(String.format("%d FE/%d FE", (int) container.te.getEnergy(), TileEntityKitAssembler.CAPACITY), mouseX, mouseY);
	}

	private void renderInfoToolTip(Slot slot, int x, int y) {
		ItemStack stack = slot.getStack();
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
			return;
		net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(stack);
		List<ITextComponent> stackList = stack.getTooltip(minecraft.player, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
		List<String> list = Lists.<String>newArrayList();
		if (stackList.size() > 0)
			list.add(stackList.get(0).getFormattedText());
		List<PanelString> data = new ItemCardReader(stack).getAllData();
		if (data != null)
			for (PanelString panelString : data) {
				if (panelString.textLeft != null)
					list.add(TextFormatting.GRAY + panelString.textLeft);
			}
		FontRenderer fontStack = stack.getItem().getFontRenderer(stack);
		renderTooltip(list, x, y, (fontStack == null ? font : fontStack));
		net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		int energyHeight = container.te.getEnergyFactor();
		if (energyHeight > 0)
			blit(guiLeft + 165, guiTop + 16 + (52 - energyHeight), 176, 17 + 52 - energyHeight, 4, energyHeight);
		int productionWidth = container.te.getProductionFactor();
		if (productionWidth > 0)
			blit(guiLeft + 86, guiTop + 35, 176, 0, productionWidth, 17);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredText(title.getString(), xSize, 6);
	}
}
