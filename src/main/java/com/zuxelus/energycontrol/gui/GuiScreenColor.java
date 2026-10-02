package com.zuxelus.energycontrol.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.gui.controls.GuiTextNumeric;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenColor extends GuiBase {
	private final static ResourceLocation PICKER = ResourceLocation.parse(EnergyControl.MODID + ":dynamic/color_picker");
	// Picker layout: a center disc (white / black) surrounded by rings split into hue sectors.
	// Light picker: value = 1, ring = saturation. Dark picker: saturation = 1, ring = value.
	private static final int HUE_SECTORS = 16;
	private static final float[] LIGHT_RINGS = { 0.5F, 1.0F };
	private static final float[] DARK_RINGS = { 0.4F, 0.7F };
	private static final float CENTER_RADIUS = 8.0F;
	private static final float RADIUS = 36.0F;
	private static final int SIZE = 80;
	private static boolean pickerRegistered;

	private GuiPanelBase<?> parentGui;
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

	public GuiScreenColor(GuiPanelBase<?> parentGui, TileEntityInfoPanel panel) {
		super("", 234, 120, EnergyControl.MODID + ":textures/gui/gui_colors.png");
		this.parentGui = parentGui;
		this.panel = panel;
		colorBack = panel.getColorBackground();
		colorText = panel.getColorText();
		offset = 116;
	}

	@Override
	public void init() {
		super.init();
		registerPickerTexture();
		fieldList.clear();
		rText = new GuiTextNumeric(font, guiLeft + 10, guiTop + 18, 26, 12, CommonComponents.EMPTY, 255);
		rText.setMaxLength(3);
		rText.setValue(Integer.toString((colorText & 0x00FF0000) >> 16));
		//rText.setEnableBackgroundDrawing(false);
		fieldList.add(rText);
		gText = new GuiTextNumeric(font, guiLeft + 46, guiTop + 18, 26, 12, CommonComponents.EMPTY, 255);
		gText.setMaxLength(3);
		gText.setValue(Integer.toString((colorText & 0x0000FF00) >> 8));
		fieldList.add(gText);
		bText = new GuiTextNumeric(font, guiLeft + 82, guiTop + 18, 26, 12, CommonComponents.EMPTY, 255);
		bText.setMaxLength(3);
		bText.setValue(Integer.toString(colorText & 0x000000FF));
		fieldList.add(bText);
		fieldList2.clear();
		rText2 = new GuiTextNumeric(font, guiLeft + 10 + offset, guiTop + 18, 26, 12, CommonComponents.EMPTY, 255);
		rText2.setMaxLength(3);
		rText2.setValue(Integer.toString((colorBack & 0x00FF0000) >> 16));
		//rText.setEnableBackgroundDrawing(false);
		fieldList2.add(rText2);
		gText2 = new GuiTextNumeric(font, guiLeft + 46 + offset, guiTop + 18, 26, 12, CommonComponents.EMPTY, 255);
		gText2.setMaxLength(3);
		gText2.setValue(Integer.toString((colorBack & 0x0000FF00) >> 8));
		fieldList2.add(gText2);
		bText2 = new GuiTextNumeric(font, guiLeft + 82 + offset, guiTop + 18, 26, 12, CommonComponents.EMPTY, 255);
		bText2.setMaxLength(3);
		bText2.setValue(Integer.toString(colorBack & 0x000000FF));
		fieldList2.add(bText2);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics matrixStack, int mouseX, int mouseY) {
		//RenderSystem.setShader(GameRenderer::getPositionTexShader);
		//RenderSystem.setShaderTexture(0, texture);
		//blit(matrixStack, 158 + (colorBack % 4) * 14, 21 + (colorBack / 4) * 14, 234, 0, 14, 14);
		matrixStack.drawString(font, I18n.get("msg.ec.ScreenColor"), 152, 6, colorBack, false);
		matrixStack.drawString(font, I18n.get("msg.ec.TextColor"), 8, 6, colorText, false);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		matrixStack.blit(PICKER, guiLeft + 20, guiTop + 34, isDarkPicker ? 80 : 0, 0, 80, 80, 160, 80);
		matrixStack.blit(PICKER, guiLeft + 20 + offset, guiTop + 34, isDarkPicker2 ? 80 : 0, 0, 80, 80, 160, 80);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		for (GuiTextNumeric text : fieldList)
			text.renderWidget(matrixStack, mouseX, mouseY, partialTicks);
		for (GuiTextNumeric text : fieldList2)
			text.renderWidget(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (mouseButton == 0) {
			for (GuiTextNumeric text : fieldList) {
				text.setFocused(text.isMouseOver(mouseX, mouseY));
				text.mouseClicked(mouseX, mouseY, mouseButton);
			}
			for (GuiTextNumeric text : fieldList2) {
				text.setFocused(text.isMouseOver(mouseX, mouseY));
				text.mouseClicked(mouseX, mouseY, mouseButton);
			}
			checkColorPicker(mouseX - guiLeft, mouseY - guiTop);
			checkColorPicker2(mouseX - guiLeft, mouseY - guiTop);
		}
		return true;
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
		if (isInside(mouseX, mouseY, 20, 34, SIZE, SIZE)) {
			Color c = getPickerColor(mouseX - 20 - SIZE / 2, mouseY - 34 - SIZE / 2, isDarkPicker);
			if (c == null)
				return;
			setColorText(c);
			fieldList.get(0).setValue(Integer.toString(c.getRed()));
			fieldList.get(1).setValue(Integer.toString(c.getGreen()));
			fieldList.get(2).setValue(Integer.toString(c.getBlue()));
		}
	}

	private void checkColorPicker2(double mouseX, double mouseY) {
		if (isInside(mouseX, mouseY, 10 + offset, 40, 8, 8)) {
			isDarkPicker2 = false;
			return;
		}
		if (isInside(mouseX, mouseY, 100 + offset, 40, 8, 8)) {
			isDarkPicker2 = true;
			return;
		}
		if (isInside(mouseX, mouseY, 20 + offset, 34, SIZE, SIZE)) {
			Color c = getPickerColor(mouseX - 20 - SIZE / 2 - offset, mouseY - 34 - SIZE / 2, isDarkPicker2);
			if (c == null)
				return;
			setColorBackground(c);
			fieldList2.get(0).setValue(Integer.toString(c.getRed()));
			fieldList2.get(1).setValue(Integer.toString(c.getGreen()));
			fieldList2.get(2).setValue(Integer.toString(c.getBlue()));
		}
	}

	// x, y are relative to the picker center. Returns null outside the circle.
	private static Color getPickerColor(double x, double y, boolean dark) {
		double dist = Math.sqrt(x * x + y * y);
		if (dist > RADIUS)
			return null;
		if (dist < CENTER_RADIUS)
			return dark ? Color.BLACK : Color.WHITE;
		float[] rings = dark ? DARK_RINGS : LIGHT_RINGS;
		int ring = Math.min((int) ((dist - CENTER_RADIUS) / (RADIUS - CENTER_RADIUS) * rings.length), rings.length - 1);
		double angle = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
		int sector = (int) Math.floor(angle * HUE_SECTORS / 360.0 + 0.5) % HUE_SECTORS;
		float hue = sector * 360.0F / HUE_SECTORS;
		return dark ? getColorFromHSV(hue, 1.0F, rings[ring]) : getColorFromHSV(hue, rings[ring], 1.0F);
	}

	// Builds the 160x80 picker texture (light picker at u = 0, dark picker at u = 80)
	// from getPickerColor, so the drawn sectors always match the clickable ones.
	private static void registerPickerTexture() {
		if (pickerRegistered)
			return;
		NativeImage image = new NativeImage(SIZE * 2, SIZE, true);
		for (int mode = 0; mode < 2; mode++)
			for (int px = 0; px < SIZE; px++)
				for (int py = 0; py < SIZE; py++) {
					double x = px + 0.5 - SIZE / 2;
					double y = py + 0.5 - SIZE / 2;
					Color c = getPickerColor(x, y, mode == 1);
					int abgr = 0;
					if (c != null) {
						boolean border = !c.equals(getPickerColor(x + 1, y, mode == 1)) || !c.equals(getPickerColor(x - 1, y, mode == 1))
								|| !c.equals(getPickerColor(x, y + 1, mode == 1)) || !c.equals(getPickerColor(x, y - 1, mode == 1));
						float k = border ? 0.6F : 1.0F;
						abgr = 0xFF000000 | ((int) (c.getBlue() * k) << 16) | ((int) (c.getGreen() * k) << 8) | (int) (c.getRed() * k);
					}
					image.setPixelRGBA(px + mode * SIZE, py, abgr);
				}
		Minecraft.getInstance().getTextureManager().register(PICKER, new DynamicTexture(image));
		pickerRegistered = true;
	}

	private static Color getColorFromHSV(float hue, float saturation, float value) {
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
		NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), 7, colorText);
		panel.setColorText(colorText);
	}

	private void setColorBackground(Color c) {
		colorBack = c.getRGB();
		NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), 6, colorBack);
		panel.setColorBackground(colorBack);
	}

	private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			minecraft.setScreen(parentGui);
			return true;
		}
		if (keyCode == 258) {
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
			return true;
		} else {
			for (GuiTextNumeric text : fieldList) {
				String value = text.getValue();
				if (text.keyPressed(keyCode, scanCode, modifiers)) {
					if (!value.equals(text.getValue()))
						setColorText(new Color(getColotInt(0), getColotInt(1), getColotInt(2)));
					return true;
				}
			}
			for (GuiTextNumeric text : fieldList2) {
				String value = text.getValue();
				if (text.keyPressed(keyCode, scanCode, modifiers)) {
					if (!value.equals(text.getValue()))
						setColorBackground(new Color(getColotInt2(0), getColotInt2(1), getColotInt2(2)));
					return true;
				}
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		for (GuiTextNumeric text : fieldList) {
			String value = text.getValue();
			if (text.charTyped(typedChar, keyCode)) {
				if (!value.equals(text.getValue()))
					setColorText(new Color(getColotInt(0), getColotInt(1), getColotInt(2)));
				return true;
			}
		}
		for (GuiTextNumeric text : fieldList2) {
			String value = text.getValue();
			if (text.charTyped(typedChar, keyCode)) {
				if (!value.equals(text.getValue()))
					setColorBackground(new Color(getColotInt2(0), getColotInt2(1), getColotInt2(2)));
				return true;
			}
		}
		return super.charTyped(typedChar, keyCode);
	}

	private int getColotInt(int id) {
		String text = fieldList.get(id).getValue();
		if (text == null || text.isEmpty())
			return 0;
		return Integer.parseInt(text);
	}

	private int getColotInt2(int id) {
		String text = fieldList2.get(id).getValue();
		if (text == null || text.isEmpty())
			return 0;
		return Integer.parseInt(text);
	}
}
