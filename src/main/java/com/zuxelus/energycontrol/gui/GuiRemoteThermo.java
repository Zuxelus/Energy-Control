package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerRemoteThermo;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.gui.controls.GuiThermoInvertRedstone;
import com.zuxelus.energycontrol.network.NetworkHelper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRemoteThermo extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_remote_thermo.png");

	private ContainerRemoteThermo container;
	private GuiTextField textboxHeat = null;
	private String name;

	public GuiRemoteThermo(ContainerRemoteThermo container) {
		super(container);
		this.container = container;
		name = I18n.format("tile.remote_thermo.name");
		xSize = 214;
		ySize = 166;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		addButton(new CompactButton(0, guiLeft + 47, guiTop - 5 + 20, 22, 12, "-1"));
		addButton(new CompactButton(1, guiLeft + 47, guiTop - 5 + 31, 22, 12, "-10"));
		addButton(new CompactButton(2, guiLeft + 12, guiTop - 5 + 20, 36, 12, "-100"));
		addButton(new CompactButton(3, guiLeft + 12, guiTop - 5 + 31, 36, 12, "-1000"));
		addButton(new CompactButton(4, guiLeft + 12, guiTop - 5 + 42, 57, 12, "-10000"));

		addButton(new CompactButton(5, guiLeft + 122, guiTop - 5 + 20, 22, 12, "+1"));
		addButton(new CompactButton(6, guiLeft + 122, guiTop - 5 + 31, 22, 12, "+10"));
		addButton(new CompactButton(7, guiLeft + 143, guiTop - 5 + 20, 36, 12, "+100"));
		addButton(new CompactButton(8, guiLeft + 143, guiTop - 5 + 31, 36, 12, "+1000"));
		addButton(new CompactButton(9, guiLeft + 122, guiTop - 5 + 42, 57, 12, "+10000"));

		addButton(new GuiThermoInvertRedstone(10, guiLeft + 70, guiTop + 33, container.te));

		textboxHeat = new GuiTextField(10, fontRendererObj, 70, 16, 51, 12);
		textboxHeat.setFocused(true);
		textboxHeat.setText(Integer.toString(container.te.getHeatLevel()));
	}

	private void updateHeat(int delta) {
		if (textboxHeat != null) {
			int heat = 0;
			try {
				String value = textboxHeat.getText();
				if (!"".equals(value))
					heat = Integer.parseInt(value);
			} catch (NumberFormatException e) { }
			heat += delta;
			if (heat <= 0)
				heat = 1;
			if (heat >= 1000000)
				heat = 1000000;
			if (container.te.getWorld().isRemote && container.te.getHeatLevel() != heat) {
				NetworkHelper.updateSeverTileEntity(container.te.getPos(), 1, heat);
				container.te.setHeatLevel(heat);
			}
			textboxHeat.setText(new Integer(heat).toString());
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (textboxHeat != null)
			textboxHeat.updateCursorCounter();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
		if (textboxHeat != null)
			textboxHeat.drawTextBox();
	}

	@Override
	public void onGuiClosed() {
		updateHeat(0);
		super.onGuiClosed();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id >= 10)
			return;

		int delta = Integer.parseInt(button.displayString.replace("+", ""));
		updateHeat(delta);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

		// Charge level progress bar
		int chargeWidth = (int) (76F * container.te.getEnergy() / container.te.getMaxStorage());
		if (chargeWidth > 76)
			chargeWidth = 76;

		if (chargeWidth > 0)
			drawTexturedModalRect(left + 55 - 14, top + 54, 8, 166, chargeWidth, 14);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) // Esc
			mc.thePlayer.closeScreen();
		else if (typedChar == 13) // Enter
			updateHeat(0);
		else if (textboxHeat != null && textboxHeat.isFocused() && (Character.isDigit(typedChar) || typedChar == 0 || typedChar == 8))
			textboxHeat.textboxKeyTyped(typedChar, keyCode);
	}
}
