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

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class GuiKitAssembler extends GuiContainerBase<ContainerKitAssembler> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");

	private ContainerKitAssembler container;

	public GuiKitAssembler(ContainerKitAssembler container, Inventory inventory, Component title) {
		super(container, inventory, title, TEXTURE, 176, 182);
		this.container = container;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(matrixStack, mouseX, mouseY, partialTicks);
		Slot slot = container.getSlot(TileEntityKitAssembler.SLOT_INFO);
		if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot.isActive())
			renderInfoToolTip(matrixStack, slot, mouseX, mouseY);
		if (isHovering(165, 16, 4, 52, mouseX, mouseY))
			matrixStack.setTooltipForNextFrame(this.font, Component.literal(String.format("%d FE/%d FE", (int) container.te.getEnergy(), TileEntityKitAssembler.CAPACITY)), mouseX, mouseY);
	}

	private void renderInfoToolTip(GuiGraphicsExtractor matrixStack, Slot slot, int x, int y) {
		ItemStack stack = slot.getItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
			return;
		List<Component> stackList = stack.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
		List<Component> list = Lists.<Component>newArrayList();
		if (stackList.size() > 0)
			list.add(stackList.get(0));
		List<PanelString> data = new ItemCardReader(stack).getAllData();
		if (data != null)
			for (PanelString panelString : data) {
				if (panelString.textLeft != null)
					list.add(Component.literal(ChatFormatting.GRAY + panelString.textLeft));
			}
		matrixStack.setTooltipForNextFrame(this.font, list, stack.getTooltipImage(), x, y);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(matrixStack, mouseX, mouseY, partialTicks);

		int energyHeight = container.te.getEnergyFactor();
		if (energyHeight > 0)
			matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos + 165, topPos + 16 + (52 - energyHeight), 176, 17 + 52 - energyHeight, 4, energyHeight, 256, 256);
		int productionWidth = container.te.getProductionFactor();
		if (productionWidth > 0)
			matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos + 86, topPos + 35, 176, 0, productionWidth, 17, 256, 256);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, imageWidth, 6);
	}
}
