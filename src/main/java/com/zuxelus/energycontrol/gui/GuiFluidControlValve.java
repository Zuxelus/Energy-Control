package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerFluidControlValve;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityFluidControlValve;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiFluidControlValve extends GuiContainerBase<ContainerFluidControlValve> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_fluid_control_valve.png");
	protected TileEntityFluidControlValve te;
	private TextFieldWidget speedBox;

	public GuiFluidControlValve(ContainerFluidControlValve container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		te = container.te;
	}

	@Override
	protected void init() {
		super.init();
		speedBox = new TextFieldWidget(font, leftPos + 90, topPos + 20, 40, 14, null, StringTextComponent.EMPTY);
		speedBox.setValue(String.valueOf(te.getSpeed()));
		addWidget(speedBox);
		setInitialFocus(speedBox);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
		if (speedBox != null)
			speedBox.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, imageWidth, 6);
		drawLeftAlignedText(matrixStack, I18n.get("Fluid Rate:"), 10, 24);
		drawRightAlignedText(matrixStack, "mB/sec", 170, 24);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (speedBox != null) {
			speedBox.mouseReleased(mouseX - leftPos, mouseY - topPos, mouseButton);
			if (speedBox.isFocused())
				return true;
			magicalSpecialHackyFocus(null);
			updateSpeed();
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		super.tick();
		if (speedBox != null)
			speedBox.tick();
	}

	private void updateSpeed() {
		if (speedBox == null)
			return;
		if (te.getLevel().isClientSide) {
			String value = speedBox.getValue();
			int speed = 0; 
			if (value != null && value.matches("[0-9.]+"))
				speed = Integer.parseInt(value);
			NetworkHelper.updateSeverTileEntity(te.getBlockPos(), 1, speed);
			te.setSpeed(speed);
		}
	}

	@Override
	public void onClose() {
		updateSpeed();
		super.onClose();
	}
}
