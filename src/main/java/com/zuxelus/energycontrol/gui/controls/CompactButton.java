package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CompactButton extends Button {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_thermal_monitor.png");
	private int id;

	public CompactButton(int id, int x, int y, int widthIn, int heightIn, String buttonText, Button.IPressable onPress) {
		super(x, y, widthIn, heightIn, buttonText, onPress);
		this.id = id;
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontRenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.getYImage(this.isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		blit(x, y, 0, 64 + i * 12, width / 2 + width % 2, height);
		blit(x + width / 2 + width % 2, y, 200 - width / 2, 64 + i * 12, width / 2, height);
		renderBg(minecraft, mouseX, mouseY);
		fontRenderer.drawString(getMessage(), x + (width - fontRenderer.getStringWidth(getMessage())) / 2, y + (height - 8) / 2, 0x404040);
	}

	public int getId() {
		return id;
	}
}
