package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiHowlerAlarmListBox extends GuiButton {
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

	public GuiHowlerAlarmListBox(int id, int left, int top, int width, int height, List<String> items, TileEntityHowlerAlarm alarm) {
		super(id, left, top, width, height, "");
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
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (dragging) {
			int pos = (mouseY - y - SCROLL_BUTTON_HEIGHT - dragDelta)
					* (lineHeight * items.size() + BASIC_Y_OFFSET - height)
					/ Math.max(height - 2 * SCROLL_BUTTON_HEIGHT - sliderHeight, 1);
			scrollTo(pos);
		}

		FontRenderer fontRenderer = mc.fontRenderer;
		String currentItem = alarm.getSoundName();
		if (lineHeight == 0) {
			lineHeight = fontRenderer.FONT_HEIGHT + 2;
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
		ScaledResolution scaler = new ScaledResolution(mc);
		GL11.glScissor(x * scaler.getScaleFactor(), mc.displayHeight - (y + height) * scaler.getScaleFactor(), (width - SCROLL_WIDTH) * scaler.getScaleFactor(), height * scaler.getScaleFactor());

		for (String row : items) {
			if(row.equals(currentItem)) {
				drawRect(x, y + rowTop - scrollTop - 1, x + width - SCROLL_WIDTH, y + rowTop - scrollTop + lineHeight - 1, selectedColor);
				fontRenderer.drawString(row, x + BASIC_X_OFFSET, y + rowTop - scrollTop, selectedFontColor);
			} else
				fontRenderer.drawString(row, x + BASIC_X_OFFSET, y + rowTop - scrollTop, fontColor);
			
			rowTop += lineHeight;
		}
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		// Slider
		int sliderX = x + width - SCROLL_WIDTH + 1;
		sliderY = y + SCROLL_BUTTON_HEIGHT + ((height - 2 * SCROLL_BUTTON_HEIGHT - sliderHeight) * scrollTop)
				/ (lineHeight * items.size() + BASIC_Y_OFFSET - height);
		mc.getTextureManager().bindTexture(TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		drawTexturedModalRect(sliderX, sliderY, 131, 16, SCROLL_WIDTH - 1, 1);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((sliderX), sliderY + sliderHeight - 1, zLevel).tex(131 / 256F, (18) / 256F).endVertex();
		bufferbuilder.pos(sliderX + SCROLL_WIDTH - 1, sliderY + sliderHeight - 1, zLevel).tex(
				(131 + SCROLL_WIDTH - 1) / 256F, (18) / 256F).endVertex();
		bufferbuilder.pos(sliderX + SCROLL_WIDTH - 1, sliderY + 1, zLevel).tex((131 + SCROLL_WIDTH - 1) / 256F,
				(17) / 256F).endVertex();
		bufferbuilder.pos((sliderX), sliderY + 1, zLevel).tex(131 / 256F, (17) / 256F).endVertex();
		tessellator.draw();

		drawTexturedModalRect(sliderX, sliderY + sliderHeight - 1, 131, 19, SCROLL_WIDTH - 1, 1);
	}

	private void setCurrent(int targetY) {
		if (lineHeight == 0)
			return;

		int itemIndex = (targetY - BASIC_Y_OFFSET - y + scrollTop) / lineHeight;
		if (itemIndex >= items.size())
			itemIndex = items.size() - 1;
		
		String newSound = items.get(itemIndex);
		if (alarm.getWorld().isRemote && !newSound.equals(alarm.getSoundName())) {
			NetworkHelper.updateSeverTileEntity(alarm.getPos(), 1, newSound);
			alarm.setSoundName(newSound);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (super.mousePressed(mc, mouseX, mouseY)) {
			if (mouseX > x + width - SCROLL_WIDTH) {// scroll click
				if (mouseY - y < SCROLL_BUTTON_HEIGHT)
					scrollUp();
				else if (height + y - mouseY < SCROLL_BUTTON_HEIGHT)
					scrollDown();
				else if (mouseY >= sliderY && mouseY <= sliderY + sliderHeight) {
					dragging = true;
					dragDelta = mouseY - sliderY;
				}
			} else {
				setCurrent(mouseY);
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		super.mouseReleased(mouseX, mouseY);
		dragging = false;
	}
}
