package com.zuxelus.energycontrol.screen.controls;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.blockentities.HowlerAlarmBlockEntity;
import com.zuxelus.energycontrol.network.NetworkHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiHowlerAlarmListBox extends AbstractPressableButtonWidget {
	private static final Identifier TEXTURE = new Identifier("energycontrol:textures/gui/gui_howler_alarm.png");

	private static final int BASIC_X_OFFSET = 2;
	private static final int BASIC_Y_OFFSET = 2;
	private static final int SCROLL_WIDTH = 10;
	private static final int SCROLL_BUTTON_HEIGHT = 8;

	public int fontColor;
	public int selectedColor;
	public int selectedFontColor;
	private int scrollTop;
	private List<String> items;
	private HowlerAlarmBlockEntity alarm;
	public int lineHeight;
	private int sliderHeight;
	public boolean dragging;
	private int sliderY;
	private int dragDelta;

	public GuiHowlerAlarmListBox(int id, int left, int top, int width, int height, List<String> items, HowlerAlarmBlockEntity alarm) {
		super(left, top, width, height, new LiteralText(""));
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
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (dragging) {
			int pos = (mouseY - y - SCROLL_BUTTON_HEIGHT - dragDelta)
					* (lineHeight * items.size() + BASIC_Y_OFFSET - height)
					/ Math.max(height - 2 * SCROLL_BUTTON_HEIGHT - sliderHeight, 1);
			scrollTo(pos);
		}

		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer textRenderer = mc.textRenderer;
		String currentItem = alarm.getSoundName();
		if (lineHeight == 0) {
			lineHeight = textRenderer.fontHeight + 2;
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
		//ScaledResolution scaler = new ScaledResolution(mc);
		GL11.glScissor((int) (x * mc.getWindow().getScaleFactor()), (int) (mc.getWindow().getFramebufferHeight() - (y + height) * mc.getWindow().getScaleFactor()), (int) ((width - SCROLL_WIDTH) * mc.getWindow().getScaleFactor()), (int) (height * mc.getWindow().getScaleFactor()));

		for (String row : items) {
			if(row.equals(currentItem)) {
				fill(matrices, x, y + rowTop - scrollTop - 1, x + width - SCROLL_WIDTH, y + rowTop - scrollTop + lineHeight - 1, selectedColor);
				textRenderer.draw(matrices, row, x + BASIC_X_OFFSET, y + rowTop - scrollTop, selectedFontColor);
			} else
				textRenderer.draw(matrices, row, x + BASIC_X_OFFSET, y + rowTop - scrollTop, fontColor);
			
			rowTop += lineHeight;
		}
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		// Slider
		int sliderX = x + width - SCROLL_WIDTH + 1;
		sliderY = y + SCROLL_BUTTON_HEIGHT + ((height - 2 * SCROLL_BUTTON_HEIGHT - sliderHeight) * scrollTop)
				/ (lineHeight * items.size() + BASIC_Y_OFFSET - height);
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexture(matrices, sliderX, sliderY, 131, 16, SCROLL_WIDTH - 1, 1);

		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferbuilder.vertex((sliderX), sliderY + sliderHeight - 1, getZOffset()).texture(131 / 256F, (18) / 256F).next();
		bufferbuilder.vertex(sliderX + SCROLL_WIDTH - 1, sliderY + sliderHeight - 1, getZOffset()).texture((131 + SCROLL_WIDTH - 1) / 256F, (18) / 256F).next();
		bufferbuilder.vertex(sliderX + SCROLL_WIDTH - 1, sliderY + 1, getZOffset()).texture((131 + SCROLL_WIDTH - 1) / 256F, (17) / 256F).next();
		bufferbuilder.vertex((sliderX), sliderY + 1, getZOffset()).texture(131 / 256F, (17) / 256F).next();
		bufferbuilder.end();
		BufferRenderer.draw(bufferbuilder);

		drawTexture(matrices, sliderX, sliderY + sliderHeight - 1, 131, 19, SCROLL_WIDTH - 1, 1);
	}

	private void setCurrent(int targetY) {
		if (lineHeight == 0)
			return;

		int itemIndex = (targetY - BASIC_Y_OFFSET - y + scrollTop) / lineHeight;
		if (itemIndex >= items.size())
			itemIndex = items.size() - 1;
		
		String newSound = items.get(itemIndex);
		if (alarm.getWorld().isClient && !newSound.equals(alarm.getSoundName())) {
			NetworkHelper.updateSeverTileEntity(alarm.getPos(), 1, newSound);
			alarm.setSoundName(newSound);
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (mouseX > x + width - SCROLL_WIDTH) {// scroll click
			if (mouseY - y < SCROLL_BUTTON_HEIGHT)
				scrollUp();
			else if (height + y - mouseY < SCROLL_BUTTON_HEIGHT)
				scrollDown();
			else if (mouseY >= sliderY && mouseY <= sliderY + sliderHeight) {
				dragging = true;
				dragDelta = (int) mouseY - sliderY;
			}
		} else
			setCurrent((int)mouseY);
	}

	@Override
	public void onPress() { }

	@Override
	public void onRelease(double mouseX, double mouseY) {
		dragging = false;
	}
}
