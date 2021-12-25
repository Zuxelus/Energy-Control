package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
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
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (width - backgroundWidth) / 2;
		int top = (height - backgroundHeight) / 2;
		drawTexture(matrixStack, left, top, 0, 0, backgroundWidth, backgroundHeight);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int x, int y) {
		ItemStack stack = te.getStack(InventoryPortablePanel.SLOT_CARD);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ((ItemCardMain) stack.getItem()).getStringData(player.world, Integer.MAX_VALUE, reader, false, true);

			int row = 0;
			for (PanelString panelString : joinedData) {
				if (row < 14) {
					if (panelString.textLeft != null)
						textRenderer.draw(matrixStack, panelString.textLeft, 9, row * 10 + 10, 0x06aee4);
					if (panelString.textCenter != null)
						textRenderer.draw(matrixStack, panelString.textCenter, (168 - textRenderer.getWidth(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4);
					if (panelString.textRight != null)
						textRenderer.draw(matrixStack, panelString.textRight, 168 - textRenderer.getWidth(panelString.textRight), row * 10 + 10, 0x06aee4);
				} else if (row == 14)
					textRenderer.draw(matrixStack, "...", 9, row * 10 + 10, 0x06aee4);
				row++;
			}
		}
	}
}
