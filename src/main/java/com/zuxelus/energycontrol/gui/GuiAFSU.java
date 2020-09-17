package com.zuxelus.energycontrol.gui;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerAFSU;
import com.zuxelus.energycontrol.gui.controls.GuiButtonImage;
import com.zuxelus.energycontrol.network.NetworkHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiAFSU extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_afsu.png");

	private String name;
	private ContainerAFSU container;
	private GuiTextField textboxTitle;
	private boolean modified;

	public GuiAFSU(ContainerAFSU container) {
		super(container);
		this.container = container;
		ySize = 196;
		name = I18n.format("tile.afsu.name");
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add((GuiButton) new GuiButtonImage(1, guiLeft + 152, guiTop + 4, 20, 20, 176, 32, 20, TEXTURE));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (buttonList.get(0) instanceof GuiButtonImage && ((GuiButtonImage) buttonList.get(0)).isMouseOver())
			drawHoveringText(Arrays.asList(I18n.format("info.redstoneMode"),I18n.format("info.redstoneMode" + container.te.getRedstoneMode())), mouseX, mouseY, fontRendererObj);
		if (mouseX >= guiLeft + 51 && mouseY >= guiTop + 34 && mouseX < guiLeft + 84 && mouseY < guiTop + 51)
			drawCreativeTabHoveringText(String.format("%.2f M/%d M EU", container.te.getEnergy() / 1000000, (int) container.te.getCapacity() / 1000000), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
		fontRendererObj.drawString(I18n.format("ic2.EUStorage.gui.info.armor"), 8, ySize - 126 + 3, 4210752);
		fontRendererObj.drawString(I18n.format("ic2.EUStorage.gui.info.level"), 51, 20, 4210752);
		int e = (int) Math.min(container.te.getEnergy(), container.te.getCapacity());
		fontRendererObj.drawString(" " + e, 92, 35, 4210752);
		fontRendererObj.drawString("/" + (int) container.te.getCapacity(), 90, 45, 4210752);
		String output = I18n.format("ic2.EUStorage.gui.info.output", container.te.getOutput());
		fontRendererObj.drawString(output, 51, 60, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		int energyWidth = (int) (25.0D * container.te.getEnergy() / container.te.getCapacity());
		if (energyWidth > 0)
			drawTexturedModalRect(guiLeft + 55, guiTop + 34, 176, 14, energyWidth, 14);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			byte mode = container.te.getRedstoneMode();
			mode++;
			if (mode > 6)
				mode = 0;
			NetworkHelper.updateSeverTileEntity(container.te.xCoord, container.te.yCoord, container.te.zCoord, 1, mode);
			container.te.setRedstoneMode(mode);
		}
	}
}