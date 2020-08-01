package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.network.NetworkHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Identifier;

public class ColorGuiScreen extends Screen {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_colors.png");

	private InfoPanelScreen parentGui;

	protected int xSize = 234;
	protected int ySize = 94;
	protected int guiLeft;
	protected int guiTop;
	private int colorText;
	private int colorBack;
	private InfoPanelBlockEntity panel;

	public ColorGuiScreen(InfoPanelScreen parentGui, InfoPanelBlockEntity panel) {
		super(NarratorManager.EMPTY);
		this.parentGui = parentGui;
		this.panel = panel;
		colorBack = panel.getColorBackground();
		colorText = panel.getColorText();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		mouseX -= guiLeft;
		mouseY -= guiTop;
		if (mouseX >= 7 && mouseX <= 226) {
			int index = ((int) mouseX - 7) / 14;
			int shift = ((int) mouseX - 7) % 14;
			if (mouseY >= 32 && mouseY <= 41 && shift <= 9) {// back
				colorBack = index;
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 2, (colorBack << 4) | colorText);
				panel.setColorBackground(colorBack);
			} else if (mouseY >= 63 && mouseY <= 72 && shift <= 9) {// /text
				colorText = index;
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 2, (colorBack << 4) | colorText);
				panel.setColorText(colorText);
			}
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		blit(left, top, 0, 0, xSize, ySize);
		blit(left + 5 + colorBack * 14, top + 30, 234, 0, 14, 14);
		blit(left + 5 + colorText * 14, top + 61, 234, 0, 14, 14);
		mc.textRenderer.draw(I18n.translate("msg.ec.ScreenColor"), guiLeft + 8, guiTop + 20, 0x404040);
		mc.textRenderer.draw(I18n.translate("msg.ec.TextColor"), guiLeft + 8, guiTop + 52, 0x404040);

		super.render(mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		parentGui.isColored = !panel.getColored();
		minecraft.openScreen(parentGui);
	}

	@Override
	public void init() {
		super.init();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		buttons.clear();
	}
}
