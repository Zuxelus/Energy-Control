package com.zuxelus.energycontrol.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.gui.controls.GuiTextNumeric;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenColor extends GuiBase {
	private final static ResourceLocation PICKER = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_color_picker.png");

	private GuiPanelBase<?> parentGui;
	private int colorText;
	private int colorBack;
	private TileEntityInfoPanel panel;
	private ArrayList<GuiTextNumeric> fieldList = new ArrayList<>();
	protected GuiTextNumeric rText;
	protected GuiTextNumeric gText;
	protected GuiTextNumeric bText;
	private boolean isDarkPicker; 

	public GuiScreenColor(GuiPanelBase<?> parentGui, TileEntityInfoPanel panel) {
		super("", 234, 120, EnergyControl.MODID + ":textures/gui/gui_colors.png");
		this.parentGui = parentGui;
		this.panel = panel;
		colorBack = panel.getColorBackground();
		colorText = panel.getColorText();
	}

	@Override
	public void init() {
		super.init();
		fieldList.clear();
		rText = new GuiTextNumeric(font, guiLeft + 10, guiTop + 18, 26, 12, "");
		rText.setMaxStringLength(3);
		rText.setText(Integer.toString((colorText & 0x00FF0000) >> 16));
		//rText.setEnableBackgroundDrawing(false);
		fieldList.add(rText);
		gText = new GuiTextNumeric(font, guiLeft + 46, guiTop + 18, 26, 12, "");
		gText.setMaxStringLength(3);
		gText.setText(Integer.toString((colorText & 0x0000FF00) >> 8));
		fieldList.add(gText);
		bText = new GuiTextNumeric(font, guiLeft + 82, guiTop + 18, 26, 12, "");
		bText.setMaxStringLength(3);
		bText.setText(Integer.toString(colorText & 0x000000FF));
		fieldList.add(bText);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		minecraft.getTextureManager().bindTexture(texture);
		blit(158 + (colorBack % 4) * 14, 21 + (colorBack / 4) * 14, 234, 0, 14, 14);
		//blit(5 + colorText * 14, 61, 234, 0, 14, 14);
		font.drawString(I18n.format("msg.ec.ScreenColor"), 152, 6, 0x404040);
		font.drawString(I18n.format("msg.ec.TextColor"), 8, 6, colorText);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		minecraft.getTextureManager().bindTexture(PICKER);
		blit(guiLeft + 20, guiTop + 34, isDarkPicker ? 80 : 0, 0, 80, 80, 160, 80);
		minecraft.getTextureManager().bindTexture(texture);
		for (GuiTextNumeric text : fieldList)
			text.renderButton(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		//mouseX -= guiLeft;
		//mouseY -= guiTop;
		if (mouseButton == 0) {
			for (GuiTextNumeric text : fieldList)
				text.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			checkColorPicker(mouseX - guiLeft, mouseY - guiTop);
		}
		int screenColor = getScreenColor(mouseX - guiLeft, mouseY - guiTop);
		if (screenColor > -1) {
			colorBack = screenColor;
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 6, colorBack);
			panel.setColorText(colorBack);
		}
		return true;
		//return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private void checkColorPicker(double mouseX, double mouseY) {
		if (isInside(mouseX, mouseY, 10, 40, 8, 8)) {
			isDarkPicker = false;
			return;
		}
		if (isInside(mouseX, mouseY, 100, 40, 8, 8)) {
			isDarkPicker = true;
			return;
		}
		if (isInside(mouseX, mouseY, 20, 34, 80, 80)) {
			double x = mouseX - 20 - 40;
			double y = mouseY - 34 - 40;
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
			/*for (GuiTextNumeric text : fieldList)
				text.setTextColor(c.getRGB());*/
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

	private int getScreenColor(double mouseX, double mouseY) {
		if (!isInside(mouseX, mouseY, 160, 23, 52, 46))
			return - 1;
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++)
				if (isInside(mouseX, mouseY, 160 + j * 14, 23 + i * 14, 8, 8))
					return i * 4 + j;
		return -1;
	}

	private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	@Override
	public void tick() {
		super.tick();
		for (GuiTextNumeric text : fieldList)
			text.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			minecraft.displayGuiScreen(parentGui);
			return true;
		}
		if (keyCode == 15) {
			if (fieldList.get(0).isFocused()) {
				fieldList.get(0).setFocused2(false);
				fieldList.get(1).setFocused2(true);
			} else if (fieldList.get(1).isFocused()) {
				fieldList.get(1).setFocused2(false);
				fieldList.get(2).setFocused2(true);
			} else if (fieldList.get(2).isFocused()) {
				fieldList.get(2).setFocused2(false);
				fieldList.get(0).setFocused2(true);
			}
		} else
			for (GuiTextNumeric text : fieldList) {
				String value = text.getText();
				text.keyPressed(keyCode, scanCode, modifiers);
				if (!value.equals(text.getText())) {
					Color c = new Color(getColotInt(0), getColotInt(1), getColotInt(2));
					setColorText(c);
				}
			}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	private int getColotInt(int id) {
		String text = fieldList.get(id).getText();
		if (text == null || text.isEmpty())
			return 0;
		return Integer.parseInt(text);
	}
}
