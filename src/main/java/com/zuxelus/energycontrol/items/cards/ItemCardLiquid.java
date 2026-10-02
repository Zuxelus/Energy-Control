package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.renderers.ModRenderTypes;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemCardLiquid extends ItemCardMain implements IHasBars {
    public ItemCardLiquid(Properties properties) { super(properties); }

	@Override
	public CardState update(Level world, ICardReader reader, int range, BlockPos pos) {
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
	public List<PanelString> getStringData(Level world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		long capacity = reader.getLong("capacity");
		long amount = reader.getLong("amount");

		if ((settings & 1) > 0) {
			String name = reader.getString("name");
			if (name.isEmpty())
				name = isServer ? "N/A" : I18n.get("msg.ec.None");
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
	@OnlyIn(Dist.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(5);
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelLiquidName"), 1));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelLiquidAmount"), 2));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelLiquidFree"), 4));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelLiquidCapacity"), 8));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelLiquidPercentage"), 16));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelShowBar"), 1024));
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
	public void renderBars(float displayWidth, float displayHeight, ICardReader reader, PoseStack matrixStack, MultiBufferSource buffer) {
		float x = -0.5F + 1 / 16.0F;
		float y = -0.5F + 1/ 16.0F;
		float z = 0;

		String fluidName = reader.getString("fluidName");
		if (fluidName.isEmpty())
			return;

		Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidName));
		IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid);

		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidExt.getStillTexture());
		if (sprite == null)
			return;

		float textureX = sprite.getU0();
		float textureY = sprite.getV0();
		float width = 14 / 16.0F * reader.getInt("amount") / reader.getInt("capacity");
		float height = 0.4375F;

		int color = fluidExt.getTintColor();
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;

		matrixStack.scale(displayWidth / 0.875f, displayHeight / 0.875f, 1);
		VertexConsumer builder = buffer.getBuffer(ModRenderTypes.screenImage(TextureAtlas.LOCATION_BLOCKS));
		Matrix4f matrix = matrixStack.last().pose();
		builder.addVertex(matrix, x, y + 0.4375F / 2 + height, z).setColor(f1, f2, f3, f).setUv(textureX, sprite.getV1()).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, x + 0.875F, y + 0.4375F / 2 + height, z).setColor(f1, f2, f3, f).setUv(sprite.getU1(), sprite.getV1()).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, x + 0.875F, y + 0.4375F / 2, z).setColor(f1, f2, f3, f).setUv(sprite.getU1(), textureY).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, x, y + 0.4375F / 2, z).setColor(f1, f2, f3, f).setUv(textureX, textureY).setLight(LightTexture.FULL_BRIGHT);

		IHasBars.drawTransparentRect(matrixStack, buffer, x + 0.875F - width, y + height + 0.4375F / 2, x, y + 0.4375F / 2, -0.0001F, 0xB0000000);
		matrixStack.scale(0.875F / displayWidth, 0.875F / displayHeight, 1);
	}
}
