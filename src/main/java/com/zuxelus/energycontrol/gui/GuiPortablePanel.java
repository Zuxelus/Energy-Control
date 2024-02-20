package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPortablePanel extends AbstractContainerScreen<ContainerPortablePanel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_portable_panel.png");
	private Player player;

	private InventoryPortablePanel te;

	public GuiPortablePanel(ContainerPortablePanel container, Inventory inventory, Component title) {
		super(container, inventory, title);
		this.te = container.te;
		this.player = inventory.player;
		this.imageWidth = 226;
		this.imageHeight = 226;
	}

	@Override
	public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics matrixStack, float partialTicks, int x, int y) {
		int left = (width - imageWidth) / 2;
		int top = (height - imageHeight) / 2;
		matrixStack.blit(TEXTURE, left, top, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(GuiGraphics matrixStack, int x, int y) {
		ItemStack stack = te.getItem(InventoryPortablePanel.SLOT_CARD);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ((ItemCardMain) stack.getItem()).getStringData(player.level(), Integer.MAX_VALUE, reader, false, true);

			int row = 0;
			for (PanelString panelString : joinedData) {
				if (row < 14) {
					if (panelString.textLeft != null)
						matrixStack.drawString(font, panelString.textLeft, 9, row * 10 + 10, 0x06aee4, false);
					if (panelString.textCenter != null)
						matrixStack.drawString(font, panelString.textCenter, (168 - font.width(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4, false);
					if (panelString.textRight != null)
						matrixStack.drawString(font, panelString.textRight, 168 - font.width(panelString.textRight), row * 10 + 10, 0x06aee4, false);
				} else if (row == 14)
					matrixStack.drawString(font, "...", 9, row * 10 + 10, 0x06aee4, false);
				row++;
			}
		}
	}
}
