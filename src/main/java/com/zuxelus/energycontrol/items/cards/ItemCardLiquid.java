package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.*;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemCardLiquid extends ItemCardBase implements IHasBars {
	public ItemCardLiquid() {
		super(ItemCardType.CARD_LIQUID, "card_liquid");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		IFluidTank storage = CrossModLoader.getTankAt(world, target);
		if (storage == null)
			return CardState.NO_TARGET;

		int amount = 0;
		String name = "";
		String texture = "";
		if (storage.getFluid() != null) {
			amount = storage.getFluid().amount;
			if (amount > 0) {
				name = FluidRegistry.getFluidName(storage.getFluid());
				texture = storage.getFluid().getFluid().getStill().toString();
			}
		}
		reader.setInt("capacity", storage.getCapacity());
		reader.setInt("amount", amount);
		reader.setString("name", name);
		reader.setString("texture", texture);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		int capacity = reader.getInt("capacity");
		int amount = reader.getInt("amount");

		if ((displaySettings & 1) > 0) {
			String name = reader.getString("name");
			if (name.equals(""))
				name = isServer ? "N/A" : I18n.format("msg.ec.None");
			result.add(new PanelString("msg.ec.InfoPanelName", name, showLabels));
		}
		if ((displaySettings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelAmountmB", amount, showLabels));
		if ((displaySettings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFreemB", capacity - amount, showLabels));
		if ((displaySettings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacitymB", capacity, showLabels));
		if ((displaySettings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPercentage", capacity == 0 ? 100 : (amount * 100 / capacity), showLabels));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidName"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidAmount"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidFree"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidCapacity"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelLiquidPercentage"), 16, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelShowBar"), 1024, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_LIQUID;
	}

	@Override
	public void renderBars(TextureManager manager, double displayWidth, double displayHeight, ICardReader reader) {
		double x = -0.5D + 1 / 16.0F;
		double y = -0.5D + 1/ 16.0F;
		double z = 0;

		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(reader.getString("texture"));
		if (sprite == null)
			return;

		double textureX = sprite.getMinU();
		double textureY = sprite.getMinV();
		double width = 14 / 16.0F * reader.getInt("amount") / reader.getInt("capacity");
		double height = 0.4375;

		GlStateManager.scale(displayWidth / 0.875, displayHeight / 0.875, 1);
		manager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.disableBlend();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + 0.4375 / 2 + height, z).tex(textureX + 0, textureY + sprite.getMaxV() - sprite.getMinV()).endVertex();
		bufferbuilder.pos(x + 0.875, y + 0.4375 / 2 + height, z).tex(textureX + sprite.getMaxU() - sprite.getMinU(), textureY + sprite.getMaxV() - sprite.getMinV()).endVertex();
		bufferbuilder.pos(x + 0.875, y + 0.4375 / 2, z).tex(textureX + sprite.getMaxU() - sprite.getMinU(), textureY + 0).endVertex();
		bufferbuilder.pos(x, y + 0.4375 / 2, z).tex(textureX + 0, textureY + 0).endVertex();
		tessellator.draw();

		IHasBars.drawTransparentRect(x + 0.875 - width, y + height + 0.4375 / 2, x, y + 0.4375 / 2, -0.0001, 0xB0000000);
		GlStateManager.scale(0.875D / displayWidth, 0.875D / displayHeight, 1);
	}
}
