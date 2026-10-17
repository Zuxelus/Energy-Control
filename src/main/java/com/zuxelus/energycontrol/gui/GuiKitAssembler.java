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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

@Environment(EnvType.CLIENT)
public class GuiKitAssembler extends GuiContainerBase<ContainerKitAssembler> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_kit_assembler.png");

	private ContainerKitAssembler container;

	public GuiKitAssembler(ContainerKitAssembler container, Inventory inventory, Component title) {
		super(container, inventory, title, TEXTURE, 176, 182);
		this.container = container;
	}

	@Override
	protected void renderTooltip(GuiGraphics context, int mouseX, int mouseY) {
		Slot slot = container.getSlot(TileEntityKitAssembler.SLOT_INFO);
		if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot.isActive())
			renderInfoToolTip(context, slot, mouseX, mouseY);
		else
			super.renderTooltip(context, mouseX, mouseY);
		if (isHovering(165, 16, 4, 52, mouseX, mouseY))
			context.renderTooltip(font, Component.literal(String.format("%d FE/%d FE", (int) container.te.getEnergy(), TileEntityKitAssembler.CAPACITY)), mouseX, mouseY);
	}

	private void renderInfoToolTip(GuiGraphics context, Slot slot, int x, int y) {
		ItemStack stack = slot.getItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemCardMain))
			return;
		List<Component> stackList = stack.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
		List<Component> list = Lists.<Component>newArrayList();
		if (stackList.size() > 0)
			list.add(stackList.get(0));
		List<PanelString> data = new ItemCardReader(stack).getAllData();
		if (data != null)
			for (PanelString panelString : data) {
				if (panelString.textLeft != null)
					list.add(Component.literal(ChatFormatting.GRAY + panelString.textLeft));
			}
		context.renderTooltip(font, list, stack.getTooltipImage(), x, y);
	}

	@Override
	public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(context, mouseX, mouseY, partialTicks);

		int energyHeight = container.te.getEnergyFactor();
		if (energyHeight > 0)
			context.blit(TEXTURE, leftPos + 165, topPos + 16 + (52 - energyHeight), 176, 17 + 52 - energyHeight, 4, energyHeight);
		int productionWidth = container.te.getProductionFactor();
		if (productionWidth > 0)
			context.blit(TEXTURE, leftPos + 86, topPos + 35, 176, 0, productionWidth, 17);
	}

	@Override
	protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
		drawCenteredText(context, title, imageWidth, 6);
	}
}
