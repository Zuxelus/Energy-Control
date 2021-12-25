package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;


@Environment(EnvType.CLIENT)
public class GuiRangeTriggerInvertRedstone extends PressableWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_range_trigger.png");

	TileEntityRangeTrigger trigger;
	private boolean checked;

	public GuiRangeTriggerInvertRedstone(int x, int y, TileEntityRangeTrigger trigger) {
		super(x, y, 0, 0, LiteralText.EMPTY);
		height = 15;
		width = 18;
		this.trigger = trigger;
		checked = trigger.getInvertRedstone();
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexture(matrixStack, x, y + 1, 176, checked ? 15 : 0, 18, 15);
	}

	@Override
	public void onPress() {
		checked = !checked;

		if (trigger.getWorld().isClient && trigger.getInvertRedstone() != checked) {
			NetworkHelper.updateSeverTileEntity(trigger.getPos(), 2, checked ? 1 : 0);
			trigger.setInvertRedstone(checked);
		}
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
