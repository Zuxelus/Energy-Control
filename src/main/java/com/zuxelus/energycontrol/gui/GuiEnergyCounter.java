package com.zuxelus.energycontrol.gui;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerEnergyCounter;
import com.zuxelus.energycontrol.utils.StringUtils;
import com.zuxelus.zlib.network.NetworkHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiEnergyCounter extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_energy_counter.png");

	private String name;
	private ContainerEnergyCounter container;

	public GuiEnergyCounter(ContainerEnergyCounter container) {
		super(container);
		this.container = container;
		name = I18n.format("tile.energy_counter.name");
	}

	private void initControls() {
		buttonList.clear();
		buttonList.add(new GuiButton(0, guiLeft + 35, guiTop + 42, 127, 20, I18n.format("msg.ec.Reset")));
	}

	@Override
	public void initGui() {
		super.initGui();
		initControls();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
		String value = StringUtils.getFormatted("", container.te.counter, false);
		fontRendererObj.drawString(value, (xSize - fontRendererObj.getStringWidth(value)) / 2, 22, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		initControls();
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if (guiButton.id == 0)
			NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, 0);
	}
}
