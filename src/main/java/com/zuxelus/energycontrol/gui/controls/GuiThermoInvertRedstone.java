package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiThermoInvertRedstone extends AbstractButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/gui/gui_thermal_monitor.png");

	TileEntityThermalMonitor thermo;
	private boolean checked;

	public GuiThermoInvertRedstone(int x, int y, TileEntityThermalMonitor thermo) {
		super(x, y, 0, 0, TextComponent.EMPTY);
		height = 15;
		width = 51;
		this.thermo = thermo;
		checked = thermo.getInvertRedstone();
	}

	@Override
	public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int delta = checked ? 15 : 0;
		blit(matrixStack, x, y + 1, 199, delta, 51, 15);
	}

	@SuppressWarnings("resource")
	@Override
	public void onPress() {
		checked = !checked;
		if (thermo.getLevel().isClientSide && thermo.getInvertRedstone() != checked) {
			NetworkHelper.updateSeverTileEntity(thermo.getBlockPos(), 2, checked ? (int) 1 : (int) 0);
			thermo.setInvertRedstone(checked);
		}
	}

	@Override
	public void updateNarration(NarrationElementOutput output) {
		// TODO Auto-generated method stub
	}
}
