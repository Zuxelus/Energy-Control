package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.renderers.ModRenderTypes;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.util.LightCoordsUtil;

public class ItemCardLiquid extends ItemCardMain implements IHasBars {

	public ItemCardLiquid(Properties properties) {
		super(properties);
	}

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

	@Override
	public void renderBars(float displayWidth, float displayHeight, ICardReader reader, PoseStack matrixStack, SubmitNodeCollector buffer) {
		float x = -0.5F + 1 / 16.0F;
		float y = -0.5F + 1/ 16.0F;
		float z = 0;

		String fluidName = reader.getString("fluidName");
		if (fluidName.isEmpty())
			return;

		Identifier id = Identifier.tryParse(fluidName);
		if (id == null)
			return;
		Fluid fluid = BuiltInRegistries.FLUID.getValue(id);
		FluidState state = fluid.defaultFluidState();
		FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(state);
		if (model == null)
			return;

		TextureAtlasSprite sprite = model.stillMaterial().sprite();
		float textureX = sprite.getU0();
		float textureY = sprite.getV0();
		float u1 = sprite.getU1();
		float v1 = sprite.getV1();
		long capacity = reader.getLong("capacity");
		float width = capacity <= 0 ? 0 : 14 / 16.0F * reader.getLong("amount") / capacity;
		float height = 0.4375F;

		int color = model.fluidTintSource() != null ? model.fluidTintSource().color(state) : -1;
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		if (f == 0)
			f = 1.0F;
		float alpha = f;

		matrixStack.pushPose();
		matrixStack.scale(displayWidth / 0.875f, displayHeight / 0.875f, 1);
		buffer.submitCustomGeometry(matrixStack, ModRenderTypes.screenImage(sprite.atlasLocation()), (pose, builder) -> {
			builder.addVertex(pose, x, y + 0.4375F / 2 + height, z).setColor(f1, f2, f3, alpha).setUv(textureX, v1).setLight(LightCoordsUtil.FULL_BRIGHT);
			builder.addVertex(pose, x + 0.875F, y + 0.4375F / 2 + height, z).setColor(f1, f2, f3, alpha).setUv(u1, v1).setLight(LightCoordsUtil.FULL_BRIGHT);
			builder.addVertex(pose, x + 0.875F, y + 0.4375F / 2, z).setColor(f1, f2, f3, alpha).setUv(u1, textureY).setLight(LightCoordsUtil.FULL_BRIGHT);
			builder.addVertex(pose, x, y + 0.4375F / 2, z).setColor(f1, f2, f3, alpha).setUv(textureX, textureY).setLight(LightCoordsUtil.FULL_BRIGHT);
		});

		IHasBars.drawTransparentRect(matrixStack, buffer, x + 0.875F - width, y + height + 0.4375F / 2, x, y + 0.4375F / 2, -0.0001F, 0xB0000000);
		matrixStack.popPose();
	}
}
