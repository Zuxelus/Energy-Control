package com.zuxelus.energycontrol.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.gui.controls.GuiTextNumeric;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenColor extends GuiBase {
	private final static ResourceLocation PICKER = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_color_picker.png");

	private GuiPanelBase parentGui;
	private int colorText;
	private int colorBack;
	private TileEntityInfoPanel panel;
	private ArrayList<GuiTextNumeric> fieldList = new ArrayList<>();
	private ArrayList<GuiTextNumeric> fieldList2 = new ArrayList<>();
	protected GuiTextNumeric rText;
	protected GuiTextNumeric gText;
	protected GuiTextNumeric bText;
	private boolean isDarkPicker;
	protected GuiTextNumeric rText2;
	protected GuiTextNumeric gText2;
	protected GuiTextNumeric bText2;
	private boolean isDarkPicker2;
	int offset;

	public GuiScreenColor(GuiPanelBase parentGui, TileEntityInfoPanel panel) {
		super("", 234, 120, EnergyControl.MODID + ":textures/gui/gui_colors.png");
		this.parentGui = parentGui;
		this.panel = panel;
		colorBack = panel.getColorBackground();
		colorText = panel.getColorText();
		offset = 116;
	}

	@Override
	public void initGui() {
		super.initGui();
		fieldList.clear();
		rText = new GuiTextNumeric(0, fontRenderer, 10, 18, 26, 12);
		rText.setMaxStringLength(3);
		rText.setText(Integer.toString((colorText & 0x00FF0000) >> 16));
		fieldList.add(rText);
		gText = new GuiTextNumeric(0, fontRenderer, 46, 18, 26, 12);
		gText.setMaxStringLength(3);
		gText.setText(Integer.toString((colorText & 0x0000FF00) >> 8));
		fieldList.add(gText);
		bText = new GuiTextNumeric(0, fontRenderer, 82, 18, 26, 12);
		bText.setMaxStringLength(3);
		bText.setText(Integer.toString(colorText & 0x000000FF));
		fieldList.add(bText);
		fieldList2.clear();
		rText2 = new GuiTextNumeric(0, fontRenderer, 10 + offset, 18, 26, 12);
		rText2.setMaxStringLength(3);
		rText2.setText(Integer.toString((colorBack & 0x00FF0000) >> 16));
		fieldList2.add(rText2);
		gText2 = new GuiTextNumeric(0, fontRenderer, 46 + offset, 18, 26, 12);
		gText2.setMaxStringLength(3);
		gText2.setText(Integer.toString((colorBack & 0x0000FF00) >> 8));
		fieldList2.add(gText2);
		bText2 = new GuiTextNumeric(0, fontRenderer, 82 + offset, 18, 26, 12);
		bText2.setMaxStringLength(3);
		bText2.setText(Integer.toString(colorBack & 0x000000FF));
		fieldList2.add(bText2);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("msg.ec.ScreenColor"), 152, 6, colorBack);
		fontRenderer.drawString(I18n.format("msg.ec.TextColor"), 8, 6, colorText);
		for (GuiTextNumeric text : fieldList)
			text.drawTextBox();
		for (GuiTextNumeric text : fieldList2)
			text.drawTextBox();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		mc.getTextureManager().bindTexture(PICKER);
		drawModalRectWithCustomSizedTexture(guiLeft + 20, guiTop + 34, isDarkPicker ? 80 : 0, 0, 80, 80, 160, 80);
		drawModalRectWithCustomSizedTexture(guiLeft + 20 + offset, guiTop + 34, isDarkPicker2 ? 80 : 0, 0, 80, 80, 160, 80);
		mc.getTextureManager().bindTexture(texture);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton == 0) {
			for (GuiTextNumeric text : fieldList)
				text.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			for (GuiTextNumeric text : fieldList2)
				text.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			checkColorPicker(mouseX - guiLeft, mouseY - guiTop);
			checkColorPicker2(mouseX - guiLeft, mouseY - guiTop);
		}
		/*int screenColor = getScreenColor(mouseX - guiLeft, mouseY - guiTop);
		if (screenColor > -1) {
			colorBack = screenColor;
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 6, colorBack);
			panel.setColorText(colorBack);
		}*/
	}

	private void checkColorPicker(int mouseX, int mouseY) {
		if (isInside(mouseX, mouseY, 10, 40, 8, 8)) {
			isDarkPicker = false;
			return;
		}
		if (isInside(mouseX, mouseY, 100, 40, 8, 8)) {
			isDarkPicker = true;
			return;
		}
		if (isInside(mouseX, mouseY, 20, 34, 80, 80)) {
			int x = mouseX - 20 - 40;
			int y = mouseY - 34 - 40;
			float saturation = (float) Math.sqrt(x * x + y * y) / 36.0F;
			if (saturation > 1)
				return;
			int hue = (int) (x == 0 ? 0 : Math.round(Math.toDegrees(Math.atan(Math.abs(y / x)))));
			if (x <= 0 && y >= 0)
				hue = 180 - hue;
			else if (x <= 0 && y <= 0)
				hue = 180 + hue;
			else if (x >= 0 && y <= 0)
				hue = 360 - hue;
			if (hue > 360)
				hue = 359;
			Color c = isDarkPicker ? getColorFromHSV(hue, 1.0F, saturation) : getColorFromHSV(hue, saturation, 1.0F);
			setColorText(c);
			fieldList.get(0).setText(Integer.toString(c.getRed()));
			fieldList.get(1).setText(Integer.toString(c.getGreen()));
			fieldList.get(2).setText(Integer.toString(c.getBlue()));
		}
	}

	private void checkColorPicker2(int mouseX, int mouseY) {
		if (isInside(mouseX, mouseY, 10 + offset, 40, 8, 8)) {
			isDarkPicker2 = false;
			return;
		}
		if (isInside(mouseX, mouseY, 100 + offset, 40, 8, 8)) {
			isDarkPicker2 = true;
			return;
		}
		if (isInside(mouseX, mouseY, 20 + offset, 34, 80, 80)) {
			int x = mouseX - 20 - 40 - offset;
			int y = mouseY - 34 - 40;
			float saturation = (float) Math.sqrt(x * x + y * y) / 36.0F;
			if (saturation > 1)
				return;
			int hue = (int) (x == 0 ? 0 : Math.round(Math.toDegrees(Math.atan(Math.abs(y / x)))));
			if (x <= 0 && y >= 0)
				hue = 180 - hue;
			else if (x <= 0 && y <= 0)
				hue = 180 + hue;
			else if (x >= 0 && y <= 0)
				hue = 360 - hue;
			if (hue > 360)
				hue = 359;
			Color c = isDarkPicker2 ? getColorFromHSV(hue, 1.0F, saturation) : getColorFromHSV(hue, saturation, 1.0F);
			setColorBackground(c);
			fieldList2.get(0).setText(Integer.toString(c.getRed()));
			fieldList2.get(1).setText(Integer.toString(c.getGreen()));
			fieldList2.get(2).setText(Integer.toString(c.getBlue()));
		}
	}

	private Color getColorFromHSV(int hue, float saturation, float value) {
		float c = saturation * value;
		float x = c * (1 - Math.abs((hue / 60.0F) % 2 - 1));
		float m = value - c;
		if (hue < 60)
			return new Color(c + m, x + m, m);
		if (hue < 120)
			return new Color(x + m, c + m, m);
		if (hue < 180)
			return new Color(m, c + m, x + m);
		if (hue < 240)
			return new Color(m, x + m, c + m);
		if (hue < 300)
			return new Color(x + m, m, c + m);
		return new Color(c + m, m, x + m);
	}

	private void setColorText(Color c) {
		colorText = c.getRGB();
		NetworkHelper.updateSeverTileEntity(panel.getPos(), 7, colorText);
		panel.setColorText(colorText);
	}

	private void setColorBackground(Color c) {
		colorBack = c.getRGB();
		NetworkHelper.updateSeverTileEntity(panel.getPos(), 6, colorBack);
		panel.setColorBackground(colorBack);
	}

	private int getScreenColor(int mouseX, int mouseY) {
		if (!isInside(mouseX, mouseY, 160, 23, 52, 46))
			return - 1;
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++)
				if (isInside(mouseX, mouseY, 160 + j * 14, 23 + i * 14, 8, 8))
					return i * 4 + j;
		return -1;
	}

	private boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		for (GuiTextNumeric text : fieldList)
			text.updateCursorCounter();
		for (GuiTextNumeric text : fieldList2)
			text.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1)
			FMLClientHandler.instance().getClient().displayGuiScreen(parentGui);
		else if (keyCode == 15) {
			if (fieldList.get(0).isFocused()) {
				fieldList.get(0).setFocused(false);
				fieldList.get(1).setFocused(true);
			} else if (fieldList.get(1).isFocused()) {
				fieldList.get(1).setFocused(false);
				fieldList.get(2).setFocused(true);
			} else if (fieldList.get(2).isFocused()) {
				fieldList.get(2).setFocused(false);
				fieldList2.get(0).setFocused(true);
			} else if (fieldList2.get(0).isFocused()) {
				fieldList2.get(0).setFocused(false);
				fieldList2.get(1).setFocused(true);
			} else if (fieldList2.get(1).isFocused()) {
				fieldList2.get(1).setFocused(false);
				fieldList2.get(2).setFocused(true);
			} else if (fieldList2.get(2).isFocused()) {
				fieldList2.get(2).setFocused(false);
				fieldList.get(0).setFocused(true);
			}
		} else {
			for (GuiTextNumeric text : fieldList) {
				String value = text.getText();
				text.textboxKeyTyped(typedChar, keyCode);
				if (!value.equals(text.getText())) {
					Color c = new Color(getColotInt(0), getColotInt(1), getColotInt(2));
					setColorText(c);
				}
			}
			for (GuiTextNumeric text : fieldList2) {
				String value = text.getText();
				text.textboxKeyTyped(typedChar, keyCode);
				if (!value.equals(text.getText())) {
					Color c = new Color(getColotInt2(0), getColotInt2(1), getColotInt2(2));
					setColorBackground(c);
				}
			}
		}
	}

	private int getColotInt(int id) {
		String text = fieldList.get(id).getText();
		if (text == null || text.isEmpty())
			return 0;
		return Integer.parseInt(text);
	}

	private int getColotInt2(int id) {
		String text = fieldList2.get(id).getText();
		if (text == null || text.isEmpty())
			return 0;
		return Integer.parseInt(text);
	}
}
