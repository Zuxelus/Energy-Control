package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class ItemCardLiquid extends ItemCardMain implements IHasBars {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) {
			reader.reset();
			return CardState.NO_TARGET;
		}

		List<FluidInfo> list = CrossModLoader.getAllTanks(world, target);
		if (list == null || list.size() < 1) {
			reader.reset();
			return CardState.NO_TARGET;
		}

		FluidInfo tank = list.get(0);
		tank.write(reader);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		long capacity = reader.getLong("capacity");
		long amount = reader.getLong("amount");

		if ((settings & 1) > 0) {
			String name = reader.getString("name");
			if (name.isEmpty())
				name = isServer ? "N/A" : I18n.translate("msg.ec.None");
			result.add(new PanelString("msg.ec.InfoPanelName", name, showLabels));
		}
		if ((settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelAmount", amount, "mB", showLabels));
		if ((settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFree", (double) capacity - amount, "mB", showLabels));
		if ((settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", capacity, "mB", showLabels));
		if ((settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPercentage", capacity == 0 ? 100 : (amount * 100 / capacity), showLabels));
		return result;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(5);
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidName"), 1));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidAmount"), 2));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidFree"), 4));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidCapacity"), 8));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelLiquidPercentage"), 16));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelShowBar"), 1024));
		return result;
	}

	@Override
	public boolean isRemoteCard() {
		return true;
	}

	// IHasBars
	@Override
	public boolean enableBars(ItemStack stack) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void renderBars(float displayWidth, float displayHeight, ICardReader reader, MatrixStack matrixStack) {
		float x = -0.5F + 1 / 16.0F;
		float y = -0.5F + 1/ 16.0F;
		float z = 0;

		String texture = reader.getString("texture");
		if (texture.isEmpty())
			return;

		Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(new Identifier(texture));
		if (sprite == null)
			return;

		float textureX = sprite.getMinU();
		float textureY = sprite.getMinV();
		float width = 14 / 16.0F * reader.getInt("amount") / reader.getInt("capacity");
		float height = 0.4375F;

		int color = reader.getInt("color");
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;

		matrixStack.scale(displayWidth / 0.875f, displayHeight / 0.875f, 1);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		Tessellator tesselator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuffer();
		bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		bufferbuilder.vertex(matrix, x, y + 0.4375F / 2 + height, z).texture(textureX, sprite.getMaxV()).color(f1, f2, f3, f).next();
		bufferbuilder.vertex(matrix, x + 0.875F, y + 0.4375F / 2 + height, z).texture(sprite.getMaxU(), sprite.getMaxV()).color(f1, f2, f3, f).next();
		bufferbuilder.vertex(matrix, x + 0.875F, y + 0.4375F / 2, z).texture(sprite.getMaxU(), textureY).color(f1, f2, f3, f).next();
		bufferbuilder.vertex(matrix, x, y + 0.4375F / 2, z).texture(textureX, textureY).color(f1, f2, f3, f).next();
		tesselator.draw();
		RenderSystem.disableDepthTest();

		IHasBars.drawTransparentRect(matrixStack, x + 0.875F - width, y + height + 0.4375F / 2, x, y + 0.4375F / 2, -0.0001F, 0xB0000000);
		matrixStack.scale(0.875F / displayWidth, 0.875F / displayHeight, 1);
	}
}
