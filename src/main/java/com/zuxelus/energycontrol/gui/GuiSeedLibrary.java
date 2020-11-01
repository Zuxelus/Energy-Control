package com.zuxelus.energycontrol.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerSeedLibrary;
import com.zuxelus.energycontrol.gui.controls.GuiButtonGeneral;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.utils.SeedLibraryFilter;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSeedLibrary extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_seed_library.png");

	public static final String BLACK = "\u00A70";
	public static final String DARK_BLUE = "\u00A71";
	public static final String DARK_GREEN = "\u00A72";
	public static final String DARK_AQUA = "\u00A73";
	public static final String DARK_RED = "\u00A74";
	public static final String DARK_PURPLE = "\u00A75";
	public static final String GOLD = "\u00A76";
	public static final String GRAY = "\u00A77";
	public static final String DARK_GRAY = "\u00A78";
	public static final String BLUE = "\u00A79";
	public static final String GREEN = "\u00A7A";
	public static final String AQUA = "\u00A7B";
	public static final String RED = "\u00A7C";
	public static final String LIGHT_PURPLE = "\u00A7D";
	public static final String YELLOW = "\u00A7E";
	public static final String WHITE = "\u00A7F";

	private ContainerSeedLibrary container;
	public int lastMouseX = -1;
	public int lastMouseY = -1;
	String tooltip = null;

	protected List realControls = null;
	protected List noControls = new ArrayList();
	private boolean rightClick = false;
	private GuiButton rightSelect;

	public static final int BORDER = 4;
	public int main_width, main_height, left, top, center, middle, right, bottom, sliders_x, sliders_y, sliders_spacing;
	public int current_slider = -1, drag_start_x = 0, drag_start_value = 0;
	public GuiButtonGeneral unk_type_button, unk_ggr_button;

	public GuiSeedLibrary(ContainerSeedLibrary container) {
		super(container);
		this.container = container;

		ySize = 222;

		main_width = xSize - BORDER * 2;
		main_height = (ySize - 96) - BORDER * 2 - 18 * 2;

		left = BORDER;
		top = BORDER;
		center = left + main_width / 2;
		middle = top + main_height / 2;
		right = left + main_width;
		bottom = top + main_height;

		sliders_x = center + main_width / 4 - (63 / 2);
		sliders_y = top + 2 + 9 - 1;
		sliders_spacing = 11 + 9;
	}

	@Override
	public void initGui() {
		super.initGui();
		GuiButtonGeneral importButton = new GuiButtonGeneral(0, guiLeft + 132, guiTop + 88, 18, 20, TEXTURE, 176, 0, 21);
		importButton.tooltip = "Import seeds";
		addButton(importButton);

		GuiButtonGeneral exportButton = new GuiButtonGeneral(1, guiLeft + 151, guiTop + 88, 18, 20, TEXTURE, 176, 42, 21);
		exportButton.tooltip = "Export seeds";
		addButton(exportButton);

		unk_type_button = new GuiButtonGeneral(2, guiLeft + left + main_width / 8 - 9, guiTop + middle + 20, 18, 20, TEXTURE, 176, 84, 21);
		// unk_type_button.tooltip = "Seeds with unknown type included";
		addButton(unk_type_button);

		unk_ggr_button = new GuiButtonGeneral(3, guiLeft + left + (main_width * 3) / 8 - 9, guiTop + middle + 20, 18, 20, TEXTURE, 176, 84, 21);
		// unk_ggr_button.tooltip = "Seeds with unknown GGR included";
		addButton(unk_ggr_button);

		int x = guiLeft + left + 3;
		int y = guiTop + 86;
		for (int dir = 0; dir < 6; dir++) {
			String key = "BTNSWE";
			String name = "" + key.charAt(dir);
		}

		String[] labels = new String[] { "Growth", "Gain", "Resistance", "Total" };
		String[] tooltips = new String[] { "Faster growth speed", "More resources on harvest", "Better weed resistance", "Worse environmental tolerance" };
		int label_left = guiLeft + center + 10;
		int label_width = (main_width / 2) - 20;
		int label_top = guiTop + top + 2;
		int label_height = 9;

		realControls = buttonList;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", 1);
		tag.setInteger("button", button.id);
		tag.setBoolean("click", rightClick);
		NetworkHelper.updateSeverTileEntity(container.te.getPos(), tag);
	}

	public void drawCenteredString(String s, int x, int y, int color) {
		fontRenderer.drawString(s, x - fontRenderer.getStringWidth(s) / 2, y, color);
	}

	public void drawRightString(String s, int x, int y, int color) {
		fontRenderer.drawString(s, x - fontRenderer.getStringWidth(s), y, color);
	}

	public void draw3DRect(int left, int top, int right, int bottom) {
		drawRect(left, top, right, bottom, 0xff373737);
		drawRect(left + 1, top + 1, right, bottom, 0xffffffff);
		drawRect(left + 1, top + 1, right - 1, bottom - 1, 0xffc6c6c6);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		SeedLibraryFilter filter = container.te.getGUIFilter();

		drawCenteredString("Seed Type", left + main_width / 4, top + 2, 0x404040);
		drawCenteredString(filter.getCropName(), left + main_width / 4, top + 2 + 8 + 1 + 18 + 2, 0x404040);

		String count;
		if (container.te.seeds_available >= 65535)
			count = "MANY";
		else
			count = container.te.seeds_available + "";
		drawCenteredString(count, 108, 90, 0x404040);
		drawCenteredString("Seeds", 108, 99, 0x404040);

		drawCenteredString("Missing info", left + main_width / 4, middle + 2, 0x404040);
		drawCenteredString("Type", left + main_width / 8, middle + 11, 0x404040);
		drawCenteredString("GGR", left + (main_width * 3) / 8, middle + 11, 0x404040);

		if (filter.unknown_type == 0) {
			unk_type_button.setTextureTop(168);
			unk_type_button.tooltip = "Seeds with unknown type " + RED + "excluded";
		} else if (filter.unknown_type == 1) {
			unk_type_button.setTextureTop(84);
			unk_type_button.tooltip = "Seeds with unknown type included";
		} else {
			unk_type_button.setTextureTop(126);
			unk_type_button.tooltip = "Seeds with unknown type " + GREEN + "only";
		}

		if (filter.unknown_ggr == 0) {
			unk_ggr_button.setTextureTop(168);
			unk_ggr_button.tooltip = "Seeds with unknown GGR " + RED + "excluded";
		} else if (filter.unknown_ggr == 1) {
			unk_ggr_button.setTextureTop(84);
			unk_ggr_button.tooltip = "Seeds with unknown GGR included";
		} else {
			unk_ggr_button.setTextureTop(126);
			unk_ggr_button.tooltip = "Seeds with unknown GGR " + GREEN + "only";
		}

		if (!container.te.getActive()) {
			drawRect(left, top, right, bottom + 22, 0xff000000);
			drawCenteredString("Out of power.", center, middle - 3, 0x404040);
			drawCenteredString("Connect to LV power", center, middle + 6, 0x404040);
			drawCenteredString("or insert a battery.", center, middle + 15, 0x404040);

			// Re-bind the GUI's texture, because something else took over.
			mc.renderEngine.bindTexture(TEXTURE);
			drawTexturedModalRect(left + 5, bottom + 2, 178, 44, 14, 16);
			fontRenderer.drawString("Battery slot", left + 23, bottom + 5, 0x404040);

			buttonList = noControls;
		} else
			buttonList = realControls;

		fontRenderer.drawString("Inventory", 8, (ySize - 96) + 4, 0x404040);

		if (lastMouseX != mouseX || lastMouseY != mouseY)
			onMouseMoved(mouseX, mouseY);

		if (tooltip != null && tooltip.length() > 0)
			drawHoveringText(tooltip, mouseX - guiLeft, mouseY - guiTop);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// Bind the GUI's texture.
		mc.renderEngine.bindTexture(TEXTURE);

		// Ensure the color is standard.
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Transfer the coordinate space to within the GUI screen.
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);

		// Draw the background.
		drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);

		// Draw the faded seed bag in the dashed outline.
		drawTexturedModalRect(left + (main_width / 4) - 9, top + 11, 194, 0, 18, 18);

		// Draw the sliders and arrows.
		SeedLibraryFilter filter = container.te.getGUIFilter();
		drawSlider(0, filter.min_growth, filter.max_growth);
		drawSlider(1, filter.min_gain, filter.max_gain);
		drawSlider(2, filter.min_resistance, filter.max_resistance);
		drawSlider(3, filter.min_total / 3, filter.max_total / 3);

		// Restore previous coordinates.
		GL11.glPopMatrix();
	}

	private void drawSlider(int index, int min, int max) {
		int pre_size = min * 2;
		int in_size = (max - min) * 2 + 1;
		int post_size = (31 - max) * 2;

		int x = sliders_x;
		int y = sliders_y + 1 + sliders_spacing * index;

		// Black before.
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
		drawTexturedModalRect(x, y, 0, 224, pre_size, 7);

		// Green during.
		GL11.glColor4f(0.0F, 0.5F, 0.0F, 1.0F);
		drawTexturedModalRect(x + pre_size, y, pre_size, 224, in_size, 7);

		// Black after.
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
		drawTexturedModalRect(x + pre_size + in_size, y, pre_size + in_size, 224, post_size, 7);

		// Green arrows.
		GL11.glColor4f(0.0F, 0.5F, 0.0F, 1.0F);
		drawTexturedModalRect(x + pre_size - 2, y - 1, 0, 232, 3, 9);
		drawTexturedModalRect(x + pre_size + in_size - 1, y - 1, 3, 232, 3, 9);

		// With slight smoothing.
		GL11.glEnable(3042 /* GL_BLEND */);
		GL11.glColor4f(0.0F, 0.5F, 0.0F, 0.25F);
		drawTexturedModalRect(x + pre_size - 2, y - 1, 6, 232, 3, 9);
		drawTexturedModalRect(x + pre_size + in_size - 1, y - 1, 9, 232, 3, 9);
		GL11.glDisable(3042 /* GL_BLEND */);

		// Return to standard colors
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (!container.te.getActive()) {
			current_slider = -1;
			return;
		}

		if (mouseButton == 1) {
			// Pass the right click to the directional buttons.
			rightClick = true;
			for (int l = 0; l < buttonList.size(); l++) {
				GuiButton guibutton = (GuiButton) buttonList.get(l);
				if (guibutton.id < 4 || guibutton.id > 9) {
					continue;
				}
				if (guibutton.mousePressed(mc, mouseX, mouseY)) {
					rightSelect = guibutton;
					// mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F); TODO
					actionPerformed(guibutton);
				}
			}
			rightClick = false;
		}

		if (mouseButton == 0) {
			// LMB down.

			// Set current slider to what's under the mouse, so it can track.
			current_slider = getSliderAt(mouseX, mouseY);

			// And if there is one, keep track of the starting point as well.
			if (current_slider != -1) {
				drag_start_x = mouseX;
				drag_start_value = container.te.getSliderValue(current_slider);
			}
		}
	}

	public int getSliderAt(int x, int y) {
		// Adjust for GUI coordinates.
		x -= guiLeft;
		y -= guiTop;

		if (x < (sliders_x - 2) || y < sliders_y) {
			// Above or left of the bars.
			return -1;
		}

		x -= sliders_x;
		y -= sliders_y;

		int bar = y / sliders_spacing;
		int remainder = y % sliders_spacing;
		if (bar > 3 || remainder >= 10) {
			// Below or between the bars.
			return -1;
		}

		int min = container.te.getSliderValue(bar * 2);
		int max = container.te.getSliderValue(bar * 2 + 1);

		if (x < min * 2 - 2) {
			// Left of both arrows.
			return -1;
		} else if (x <= min * 2) {
			// Over the minimum arrow.
			return bar * 2;
		} else if (x < max * 2) {
			// Between the arrows.
			return -1;
		} else if (x <= max * 2 + 2) {
			// Over the maximum arrow;
			return bar * 2 + 1;
		} else {
			// Right of both arrows.
			return -1;
		}
	}

	public String getSliderName(int slider) {
		String name;
		int bar = slider / 2;
		int arrow = slider % 2;

		if (arrow == 0) {
			name = "Minimum ";
		} else {
			name = "Maximum ";
		}

		if (bar == 0) {
			name += DARK_GREEN + "Growth";
		} else if (bar == 1) {
			name += GOLD + "Gain";
		} else if (bar == 2) {
			name += AQUA + "Resistance";
		} else { // bar == 3
			name += YELLOW + "Total";
		}

		return name;
	}

	private void setSliderValue(int slider, int value) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", 2);
		tag.setInteger("slider", slider);
		tag.setInteger("value", value);
		NetworkHelper.updateSeverTileEntity(container.te.getPos(), tag);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);

		if (rightSelect != null && state == 1) {
			// Release a button pressed with RMB.
			rightSelect.mouseReleased(mouseX, mouseY);
			rightSelect = null;
		}

		if (!container.te.getActive()) {
			current_slider = -1;
			return;
		}

		if (state == 0)
			// LMB up, stop tracking the mouse with a slider.
			if (current_slider != -1)
				current_slider = -1;
	}

	private void onMouseMoved(int newX, int newY) {
		// Update the tooltip.
		tooltip = getTooltip(newX, newY);

		// If we're following the mouse with a slider, move it.
		if (current_slider == -1)
			return;

		int value = drag_start_value + (newX - drag_start_x) / 2;
		if (value < 0)
			value = 0;
		if (value > 31)
			value = 31;

		int bar = (current_slider / 2);
		int min = container.te.getSliderValue(bar * 2);
		int max = container.te.getSliderValue(bar * 2 + 1);
		boolean is_max = (current_slider % 2) == 1;
		if (is_max && min > value)
			value = min;
		else if (!is_max && max < value)
			value = max;

		if (container.te.getSliderValue(current_slider) != value)
			setSliderValue(current_slider, value);
	}

	private String getTooltip(int x, int y) {
		int slider = current_slider;
		if (slider == -1)
			slider = getSliderAt(x, y);

		if (slider != -1) {
			int value = container.te.getSliderValue(slider);
			if (slider > 5)
				value *= 3;
			return getSliderName(slider) + WHITE + ": " + value;
		}

		for (Object control : buttonList) {
			if (control instanceof GuiButtonGeneral) {
				String tooltip = ((GuiButtonGeneral) control).getActiveTooltip(x, y);
				if (tooltip != null)
					return tooltip;
			}
		}

		return null;
	}
}
