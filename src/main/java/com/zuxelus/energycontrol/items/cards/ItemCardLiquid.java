package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.renderers.RenderHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemCardLiquid extends ItemCardMain implements IHasBars {

	@Override
	public CardState update(Level level, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null) {
			reader.reset();
			return CardState.NO_TARGET;
		}

		List<FluidInfo> list = CrossModLoader.getAllTanks(level, target);
		if (list == null || list.size() < 1) {
			reader.reset();
			return CardState.NO_TARGET;
		}

		FluidInfo tank = list.get(0);
		tank.write(reader);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(Level level, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
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
	@Environment(EnvType.CLIENT)
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

		// the card stores the fluid id; its texture and tint only exist on the client
		String fluidId = reader.getString("fluid");
		long capacity = reader.getLong("capacity");
		if (fluidId.isEmpty() || capacity <= 0)
			return;
		FluidVariant fluid = FluidVariant.of(BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidId)));
		if (fluid.isBlank())
			return;
		TextureAtlasSprite sprite = FluidVariantRendering.getSprite(fluid);
		if (sprite == null)
			return;

		float textureX = sprite.getU0();
		float textureY = sprite.getV0();
		float width = 14 / 16.0F * Math.min(reader.getLong("amount"), capacity) / capacity;
		float height = 0.4375F;

		int color = 0xFF000000 | FluidVariantRendering.getColor(fluid); // tint has no alpha

		matrixStack.scale(displayWidth / 0.875f, displayHeight / 0.875f, 1);
		RenderHelper.texturedRect(matrixStack, buffer, TextureAtlas.LOCATION_BLOCKS, x, y + 0.4375F / 2, x + 0.875F, y + 0.4375F / 2 + height, z,
				textureX, textureY, sprite.getU1(), sprite.getV1(), color);

		IHasBars.drawTransparentRect(matrixStack, buffer, x + 0.875F - width, y + height + 0.4375F / 2, x, y + 0.4375F / 2, -0.0001F, 0xB0000000);
		matrixStack.scale(0.875F / displayWidth, 0.875F / displayHeight, 1);
	}
}
