package com.zuxelus.energycontrol.screen;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.items.PortablePanelInventory;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.MainCardItem;
import com.zuxelus.energycontrol.screen.handlers.PortablePanelScreenHandler;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PortablePanelScreen extends HandledScreen<PortablePanelScreenHandler> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_portable_panel.png");
	private PlayerEntity player;

	public PortablePanelScreen(PortablePanelScreenHandler container, PlayerInventory playerInventory, Text text) {
		super(container, playerInventory, text);
		this.player = playerInventory.player;
		backgroundWidth = 226;
		backgroundHeight = 226;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		ItemStack stack = handler.be.getStack(PortablePanelInventory.SLOT_CARD);
		if (!stack.isEmpty() && stack.getItem() instanceof MainCardItem) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ((MainCardItem) stack.getItem()).getStringData(player.world, Integer.MAX_VALUE, reader, true);

			int row = 0;
			for (PanelString panelString : joinedData) {
				if (row < 14) {
					if (panelString.textLeft != null)
						textRenderer.draw(matrices, panelString.textLeft, 9, row * 10 + 10, 0x06aee4);
					if (panelString.textCenter != null)
						textRenderer.draw(matrices, panelString.textCenter, (168 - textRenderer.getWidth(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4);
					if (panelString.textRight != null)
						textRenderer.draw(matrices, panelString.textRight, 168 - textRenderer.getWidth(panelString.textRight), row * 10 + 10, 0x06aee4);
				} else if (row == 14)
					textRenderer.draw(matrices, "...", 9, row * 10 + 10, 0x06aee4);
				row++;
			}
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		client.getTextureManager().bindTexture(TEXTURE);
		int left = (width - backgroundWidth) / 2;
		int top = (height - backgroundHeight) / 2;
		drawTexture(matrices, left, top, 0, 0, backgroundWidth, backgroundHeight);
	}
}
