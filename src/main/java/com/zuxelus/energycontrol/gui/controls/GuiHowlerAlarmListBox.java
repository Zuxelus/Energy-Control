package com.zuxelus.energycontrol.gui.controls;

import net.minecraft.screen.ScreenTexts;

import java.util.List;

import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiHowlerAlarmListBox extends PressableWidget {
	private static final Identifier TEXTURE = Identifier.of("energycontrol:textures/gui/gui_howler_alarm.png");

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
		super(left, top, width, height, ScreenTexts.EMPTY);
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
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		if (dragging) {
			int pos = (mouseY - getY() - SCROLL_BUTTON_HEIGHT - dragDelta)
					* (lineHeight * items.size() + BASIC_Y_OFFSET - height)
					/ Math.max(height - 2 * SCROLL_BUTTON_HEIGHT - sliderHeight, 1);
			scrollTo(pos);
		}

		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		String currentItem = alarm.getSoundName();
		if (lineHeight == 0) {
			lineHeight = fontRenderer.fontHeight + 2;
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
		context.enableScissor(getX(), getY(), getX() + width - SCROLL_WIDTH, getY() + height);

		for (String row : items) {
			if(row.equals(currentItem)) {
				context.fill(getX(), getY() + rowTop - scrollTop - 1, getX() + width - SCROLL_WIDTH, getY() + rowTop - scrollTop + lineHeight - 1, selectedColor);
				context.drawText(fontRenderer, row, getX() + BASIC_X_OFFSET, getY() + rowTop - scrollTop, selectedFontColor, false);
			} else
				context.drawText(fontRenderer, row, getX() + BASIC_X_OFFSET, getY() + rowTop - scrollTop, fontColor, false);
			
			rowTop += lineHeight;
		}
		
		context.disableScissor();

		// Slider
		int sliderX = getX() + width - SCROLL_WIDTH + 1;
		sliderY = getY() + SCROLL_BUTTON_HEIGHT + ((height - 2 * SCROLL_BUTTON_HEIGHT - sliderHeight) * scrollTop) / (lineHeight * items.size() + BASIC_Y_OFFSET - height);
		context.drawTexture(TEXTURE, sliderX, sliderY, 131, 16, SCROLL_WIDTH - 1, 1);
		// slider body: one texture row stretched to the slider height
		context.drawTexture(TEXTURE, sliderX, sliderY + 1, SCROLL_WIDTH - 1, sliderHeight - 2, 131, 17, SCROLL_WIDTH - 1, 1, 256, 256);
		context.drawTexture(TEXTURE, sliderX, sliderY + sliderHeight - 1, 131, 19, SCROLL_WIDTH - 1, 1);
	}

	private void setCurrent(double targetY) {
		if (lineHeight == 0)
			return;

		int itemIndex = ((int) targetY - BASIC_Y_OFFSET - getY() + scrollTop) / lineHeight;
		if (itemIndex >= items.size())
			itemIndex = items.size() - 1;
		
		String newSound = items.get(itemIndex);
		if (alarm.getWorld().isClient && !newSound.equals(alarm.getSoundName())) {
			NetworkHelper.updateSeverTileEntity(alarm.getPos(), 1, newSound);
			alarm.setSoundName(newSound);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// consumed, otherwise the screen also moves keyboard focus away from the list
		if (keyCode == 264) { // down
			scrollDown();
			return true;
		}
		if (keyCode == 265) { // up
			scrollUp();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (isMouseOver(mouseX, mouseY)) {
			if (verticalAmount > 0)
				scrollUp();
			if (verticalAmount < 0)
				scrollDown();
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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
	public void appendClickableNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
