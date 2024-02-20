package com.zuxelus.energycontrol.gui.controls;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHowlerAlarmListBox extends AbstractButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("energycontrol:textures/gui/gui_howler_alarm.png");

	private static final int BASIC_X_OFFSET = 2;
	private static final int BASIC_Y_OFFSET = 2;
	private static final int SCROLL_WIDTH = 10;
	private static final int SCROLL_BUTTON_HEIGHT = 8;

	public int fontColor;
	public int selectedColor;
	public int selectedFontColor;
	private int scrollTop;
	private List<String> items;
	private TileEntityHowlerAlarm alarm;
	public int lineHeight;
	private int sliderHeight;
	public boolean dragging;
	private int sliderY;
	private int dragDelta;

	public GuiHowlerAlarmListBox(int left, int top, int width, int height, List<String> items, TileEntityHowlerAlarm alarm) {
		super(left, top, width, height, CommonComponents.EMPTY);
		this.items = items;
		this.alarm = alarm;
		fontColor = 0x404040;
		selectedColor = 0xff404040;
		selectedFontColor = 0xA0A0A0;
		scrollTop = 0;
		lineHeight = 0;
		sliderHeight = 0;
		dragging = false;
		dragDelta = 0;
	}

	private void scrollTo(int pos) {
		scrollTop = pos;
		if (scrollTop < 0)
			scrollTop = 0;
		int max = lineHeight * items.size() + BASIC_Y_OFFSET - height;
		if (max < 0)
			max = 0;
		if (scrollTop > max)
			scrollTop = max;
	}

	public void scrollUp() {
		scrollTop -= 8;
		if (scrollTop < 0)
			scrollTop = 0;
	}

	public void scrollDown() {
		scrollTop += 8;
		int max = lineHeight * items.size() + BASIC_Y_OFFSET - height;
		if (max < 0)
			max = 0;
		if (scrollTop > max)
			scrollTop = max;
	}

	@Override
	public void renderWidget(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (dragging) {
			int pos = (mouseY - getY() - SCROLL_BUTTON_HEIGHT - dragDelta)
					* (lineHeight * items.size() + BASIC_Y_OFFSET - height)
					/ Math.max(height - 2 * SCROLL_BUTTON_HEIGHT - sliderHeight, 1);
			scrollTo(pos);
		}

		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		String currentItem = alarm.getSoundName();
		if (lineHeight == 0) {
			lineHeight = fontRenderer.lineHeight + 2;
			if (scrollTop == 0) {
				int rowsPerHeight = height / lineHeight;
				int currentIndex = items.indexOf(currentItem);
				if (currentIndex >= rowsPerHeight)
					scrollTop = (currentIndex + 1) * lineHeight + BASIC_Y_OFFSET - height;
			}
			float scale = height / ((float) lineHeight * items.size() + BASIC_Y_OFFSET);
			if (scale > 1)
				scale = 1;
			sliderHeight = Math.round(scale * (height - 2 * SCROLL_BUTTON_HEIGHT));
			if (sliderHeight < 4)
				sliderHeight = 4;
		}

		int rowTop = BASIC_Y_OFFSET;
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		Window scaler = minecraft.getWindow();
		GL11.glScissor((int) (getX() * scaler.getGuiScale()), (int) (scaler.getHeight() - (getY() + height) * scaler.getGuiScale()), (int) ((width - SCROLL_WIDTH) * scaler.getGuiScale()), (int) (height * scaler.getGuiScale()));

		for (String row : items) {
			if(row.equals(currentItem)) {
				matrixStack.fill(getX(), getY() + rowTop - scrollTop - 1, getX() + width - SCROLL_WIDTH, getY() + rowTop - scrollTop + lineHeight - 1, selectedColor);
				matrixStack.drawString(fontRenderer, row, getX() + BASIC_X_OFFSET, getY() + rowTop - scrollTop, selectedFontColor, false);
			} else
				matrixStack.drawString(fontRenderer, row, getX() + BASIC_X_OFFSET, getY() + rowTop - scrollTop, fontColor, false);
			
			rowTop += lineHeight;
		}
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		// Slider
		int sliderX = getX() + width - SCROLL_WIDTH + 1;
		sliderY = getY() + SCROLL_BUTTON_HEIGHT + ((height - 2 * SCROLL_BUTTON_HEIGHT - sliderHeight) * scrollTop) / (lineHeight * items.size() + BASIC_Y_OFFSET - height);
		matrixStack.blit(TEXTURE, sliderX, sliderY, 131, 16, SCROLL_WIDTH - 1, 1);

		/*Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex((sliderX), sliderY + sliderHeight - 1, getBlitOffset()).uv(131 / 256F, (18) / 256F).endVertex();
		bufferbuilder.vertex(sliderX + SCROLL_WIDTH - 1, sliderY + sliderHeight - 1, getBlitOffset()).uv((131 + SCROLL_WIDTH - 1) / 256F, (18) / 256F).endVertex();
		bufferbuilder.vertex(sliderX + SCROLL_WIDTH - 1, sliderY + 1, getBlitOffset()).uv((131 + SCROLL_WIDTH - 1) / 256F, (17) / 256F).endVertex();
		bufferbuilder.vertex((sliderX), sliderY + 1, getBlitOffset()).uv(131 / 256F, (17) / 256F).endVertex();
		tesselator.end();*/

		matrixStack.blit(TEXTURE, sliderX, sliderY + sliderHeight - 1, 131, 19, SCROLL_WIDTH - 1, 1);
	}

	private void setCurrent(double targetY) {
		if (lineHeight == 0)
			return;

		int itemIndex = ((int) targetY - BASIC_Y_OFFSET - getY() + scrollTop) / lineHeight;
		if (itemIndex >= items.size())
			itemIndex = items.size() - 1;
		
		String newSound = items.get(itemIndex);
		if (alarm.getLevel().isClientSide && !newSound.equals(alarm.getSoundName())) {
			NetworkHelper.updateSeverTileEntity(alarm.getBlockPos(), 1, newSound);
			alarm.setSoundName(newSound);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 264) {
			scrollDown();
		} else if (keyCode == 265) {
			scrollUp();
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void onPress() { }

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (mouseX > getX() + width - SCROLL_WIDTH) {// scroll click
			if (mouseY - getY() < SCROLL_BUTTON_HEIGHT)
				scrollUp();
			else if (height + getY() - mouseY < SCROLL_BUTTON_HEIGHT)
				scrollDown();
			else if (mouseY >= sliderY && mouseY <= sliderY + sliderHeight) {
				dragging = true;
				dragDelta = (int) mouseY - sliderY;
			}
		} else
			setCurrent(mouseY);
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		dragging = false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double p_94736_) {
		if (isMouseOver(mouseX, mouseY)) {
			if (p_94736_ > 0) {
				scrollUp();
			}
			if (p_94736_ < 0) {
				scrollDown();
			}
		}
		return super.mouseScrolled(mouseX, mouseY, p_94736_);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) { }
}
