package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPortablePanel extends ContainerScreen<ContainerPortablePanel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_portable_panel.png");
	private PlayerEntity player;

	private InventoryPortablePanel te;

	public GuiPortablePanel(ContainerPortablePanel container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title);
		this.te = container.te;
		this.player = inventory.player;
		this.imageWidth = 226;
		this.imageHeight = 226;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		int left = (width - imageWidth) / 2;
		int top = (height - imageHeight) / 2;
		blit(matrixStack, left, top, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int x, int y) {
		ItemStack stack = te.getItem(InventoryPortablePanel.SLOT_CARD);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemCardMain) {
			ItemCardReader reader = new ItemCardReader(stack);

			CardState state = reader.getState();
			List<PanelString> joinedData;
			if (state != CardState.OK && state != CardState.CUSTOM_ERROR)
				joinedData = ItemCardReader.getStateMessage(state);
			else
				joinedData = ((ItemCardMain) stack.getItem()).getStringData(player.level, Integer.MAX_VALUE, reader, false, true);

			int row = 0;
			for (PanelString panelString : joinedData) {
				if (row < 14) {
					if (panelString.textLeft != null)
						font.draw(matrixStack, panelString.textLeft, 9, row * 10 + 10, 0x06aee4);
					if (panelString.textCenter != null)
						font.draw(matrixStack, panelString.textCenter, (168 - font.width(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4);
					if (panelString.textRight != null)
						font.draw(matrixStack, panelString.textRight, 168 - font.width(panelString.textRight), row * 10 + 10, 0x06aee4);
				} else if (row == 14)
					font.draw(matrixStack, "...", 9, row * 10 + 10, 0x06aee4);
				row++;
			}
		}
	}
}
