package com.zuxelus.energycontrol.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.containers.PortablePanelContainer;
import com.zuxelus.energycontrol.items.PortablePanelInventory;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.MainCardItem;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class PortablePanelScreen extends ContainerScreen<PortablePanelContainer> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_portable_panel.png");
	private PlayerEntity player;

	public PortablePanelScreen(PortablePanelContainer container, PlayerEntity player) {
		super(container, player.inventory, new TranslatableText(""));
		this.player = player;
		containerWidth = 226;
		containerHeight = 226;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		super.render(mouseX, mouseY, delta);
		drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		ItemStack stack = container.be.getInvStack(PortablePanelInventory.SLOT_CARD);
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
						font.draw(panelString.textLeft, 9, row * 10 + 10, 0x06aee4);
					if (panelString.textCenter != null)
						font.draw(panelString.textCenter, (168 - font.getStringWidth(panelString.textCenter)) / 2, row * 10 + 10, 0x06aee4);
					if (panelString.textRight != null)
						font.draw(panelString.textRight, 168 - font.getStringWidth(panelString.textRight), row * 10 + 10, 0x06aee4);
				} else if (row == 14)
					font.draw("...", 9, row * 10 + 10, 0x06aee4);
				row++;
			}
		}
	}

	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int left = (width - containerWidth) / 2;
		int top = (height - containerHeight) / 2;
		blit(left, top, 0, 0, containerWidth, containerHeight);
	}
}
