package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardLiquid extends ItemCardBase implements IHasBars {

	public ItemCardLiquid() {
		super(ItemCardType.CARD_LIQUID, "card_liquid");
	}

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
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		long capacity = reader.getLong(DataHelper.CAPACITY);
		long amount = reader.getLong(DataHelper.AMOUNT);

		if ((settings & 1) > 0) {
			String name = reader.getString("name");
			if (name.isEmpty())
				name = isServer ? "N/A" : I18n.format("msg.ec.None");
			result.add(new PanelString("msg.ec.InfoPanelName", net.minecraft.util.text.translation.I18n.translateToLocal(name), showLabels));
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
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidName"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidAmount"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidFree"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidCapacity"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidPercentage"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelShowBar"), 1024));
		return result;
	}

	// IHasBars
	@Override
	public boolean enableBars(ItemStack stack) {
		return true;
	}

	@Override
	public void renderBars(TextureManager manager, double displayWidth, double displayHeight, ICardReader reader) {
		double x = -0.5D + 1 / 16.0F;
		double y = -0.5D + 1/ 16.0F;
		double z = 0;

		String texture = reader.getString("texture");
		if (texture.isEmpty())
			return;

		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(texture);
		if (sprite == null)
			return;

		double textureX = sprite.getMinU();
		double textureY = sprite.getMinV();
		double width = 14 / 16.0F * reader.getInt("amount") / reader.getInt("capacity");
		double height = 0.4375;

		int color = reader.getInt("color");
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;

		GlStateManager.scale(displayWidth / 0.875, displayHeight / 0.875, 1);
		manager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(x, y + 0.4375 / 2 + height, z).tex(textureX + 0, textureY + sprite.getMaxV() - sprite.getMinV()).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos(x + 0.875, y + 0.4375 / 2 + height, z).tex(textureX + sprite.getMaxU() - sprite.getMinU(), textureY + sprite.getMaxV() - sprite.getMinV()).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos(x + 0.875, y + 0.4375 / 2, z).tex(textureX + sprite.getMaxU() - sprite.getMinU(), textureY + 0).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos(x, y + 0.4375 / 2, z).tex(textureX + 0, textureY + 0).color(f1, f2, f3, f).endVertex();
		tessellator.draw();
		//GlStateManager.disableDepth();

		IHasBars.drawTransparentRect(x + 0.875 - width, y + height + 0.4375 / 2, x, y + 0.4375 / 2, -0.0001, 0xB0000000);
		GlStateManager.scale(0.875D / displayWidth, 0.875D / displayHeight, 1);
	}
}
