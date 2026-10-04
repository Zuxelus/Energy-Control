package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiPortablePanel extends HandledScreen<ContainerPortablePanel> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID + ":textures/gui/gui_portable_panel.png");
	private PlayerEntity player;

	private InventoryPortablePanel te;

	public GuiPortablePanel(ContainerPortablePanel container, PlayerInventory inventory, Text title) {
		super(container, inventory, title);
		this.te = container.te;
		this.player = inventory.player;
		this.backgroundWidth = 226;
		this.backgroundHeight = 226;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		super.render(context, mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(DrawContext context, float partialTicks, int x, int y) {
		int left = (width - backgroundWidth) / 2;
		int top = (height - backgroundHeight) / 2;
		context.drawTexture(TEXTURE, left, top, 0, 0, backgroundWidth, backgroundHeight);
	}

	@Override
	protected void drawForeground(DrawContext context, int x, int y) {
		ItemStack stack = te.getStack(InventoryPortablePanel.SLOT_CARD);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ((ItemCardMain) stack.getItem()).getStringData(player.getWorld(), Integer.MAX_VALUE, reader, false, true);

			int row = 0;
			for (PanelString panelString : joinedData) {
				if (row < 14) {
					if (panelString.textLeft != null)
						context.drawText(textRenderer, panelString.textLeft, 9, row * 10 + 10, 0x06aee4, false);
					if (panelString.textCenter != null)
						context.drawText(textRenderer, panelString.textCenter, (168 - textRenderer.getWidth(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4, false);
					if (panelString.textRight != null)
						context.drawText(textRenderer, panelString.textRight, 168 - textRenderer.getWidth(panelString.textRight), row * 10 + 10, 0x06aee4, false);
				} else if (row == 14)
					context.drawText(textRenderer, "...", 9, row * 10 + 10, 0x06aee4, false);
				row++;
			}
		}
	}
}
