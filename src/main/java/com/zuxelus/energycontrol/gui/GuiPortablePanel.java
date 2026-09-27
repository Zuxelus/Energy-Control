package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GuiPortablePanel extends AbstractContainerScreen<ContainerPortablePanel> {
	private static final Identifier TEXTURE = Identifier.parse(EnergyControl.MODID + ":textures/gui/gui_portable_panel.png");
	private Player player;

	private InventoryPortablePanel te;

	public GuiPortablePanel(ContainerPortablePanel container, Inventory inventory, Component title) {
		super(container, inventory, title, 226, 226);
		this.te = container.te;
		this.player = inventory.player;
	}


	@Override
	public void extractBackground(GuiGraphicsExtractor matrixStack, int x, int y, float partialTicks) {
		super.extractBackground(matrixStack, x, y, partialTicks);
		int left = (width - imageWidth) / 2;
		int top = (height - imageHeight) / 2;
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left, top, 0, 0, imageWidth, imageHeight, 256, 256);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor matrixStack, int x, int y) {
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
						matrixStack.text(font, panelString.textLeft, 9, row * 10 + 10, ARGB.opaque(0x06aee4), false);
					if (panelString.textCenter != null)
						matrixStack.text(font, panelString.textCenter, (168 - font.width(panelString.textCenter)) / 2, row * 10 + 10, ARGB.opaque(0x06aee4), false);
					if (panelString.textRight != null)
						matrixStack.text(font, panelString.textRight, 168 - font.width(panelString.textRight), row * 10 + 10, ARGB.opaque(0x06aee4), false);
				} else if (row == 14)
					matrixStack.text(font, "...", 9, row * 10 + 10, ARGB.opaque(0x06aee4), false);
				row++;
			}
		}
	}
}
