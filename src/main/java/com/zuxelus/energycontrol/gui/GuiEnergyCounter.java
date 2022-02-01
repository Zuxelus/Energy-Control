package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerEnergyCounter;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEnergyCounter extends GuiContainerBase<ContainerEnergyCounter> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_energy_counter.png");
	protected TileEntityEnergyCounter te;
	private TextFieldWidget rateBox;

	public GuiEnergyCounter(ContainerEnergyCounter container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		te = container.te;
	}

	@Override
	protected void init() {
		super.init();
		rateBox = new TextFieldWidget(font, leftPos + 90, topPos + 20, 40, 14, null, StringTextComponent.EMPTY);
		rateBox.setValue(String.valueOf(te.getRate()));
		addWidget(rateBox);
		setInitialFocus(rateBox);
		addButton(new Button(leftPos + 60, topPos + 56, 60, 20, new TranslationTextComponent("msg.ec.Reset"), (button) -> { actionPerformed(); }));
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
		if (rateBox != null)
			rateBox.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, imageWidth, 6);
		drawLeftAlignedText(matrixStack, I18n.get("msg.ec.cbInfoPanelRate") + ":", 10, 24);
		drawRightAlignedText(matrixStack, "FE/t", 170, 24);
		drawLeftAlignedText(matrixStack, I18n.get("msg.ec.cbInfoPanelTotal") + ":", 10, 40);
		drawRightAlignedText(matrixStack, Integer.toString(te.getTotal()), 120, 40);
		drawRightAlignedText(matrixStack, "FE", 146, 40);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (rateBox != null) {
			rateBox.mouseReleased(mouseX - leftPos, mouseY - topPos, mouseButton);
			if (rateBox.isFocused())
				return true;
			magicalSpecialHackyFocus(null);
			updateSpeed();
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void tick() {
		super.tick();
		if (rateBox != null)
			rateBox.tick();
	}

	private void actionPerformed() {
		NetworkHelper.updateSeverTileEntity(te.getBlockPos(), 2, 0); // reset total
	}

	private void updateSpeed() {
		if (rateBox == null)
			return;
		if (te.getLevel().isClientSide) {
			String value = rateBox.getValue();
			int rate = 0; 
			if (value != null && value.matches("[0-9.]+"))
				rate = Integer.parseInt(value);
			NetworkHelper.updateSeverTileEntity(te.getBlockPos(), 1, rate);
			te.setRate(rate);
		}
	}

	@Override
	public void onClose() {
		updateSpeed();
		super.onClose();
	}
}
