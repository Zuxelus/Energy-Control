package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemCardLiquid extends ItemCardBase implements IHasBars {

	public ItemCardLiquid() {
		super(ItemCardType.CARD_LIQUID, "card_liquid");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null) {
			reader.reset();
			return CardState.NO_TARGET;
		}

		List<FluidInfo> list = CrossModLoader.getAllTanks(world, target.posX, target.posY, target.posZ);
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
			result.add(new PanelString("msg.ec.InfoPanelName", StatCollector.translateToLocal(name), showLabels));
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
	public List<PanelSetting> getSettingsList() {
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

		String fluidName = reader.getString("fluidName");
		if (fluidName.isEmpty())
			return;

		Fluid fluid = FluidRegistry.getFluid(fluidName); // 1.7.10
		if (fluid == null) {
			tryRenderHBMFluid(manager, displayWidth, displayHeight, reader, fluidName);
			return;
		}

		IIcon sprite = fluid.getStillIcon(); // 1.7.10
		if (sprite == null)
			return;

		double textureX = sprite.getMinU();
		double textureY = sprite.getMinV();
		double width = 14 / 16.0F * reader.getInt(DataHelper.AMOUNT) / reader.getInt(DataHelper.CAPACITY);
		double height = 0.4375;

		GL11.glScaled(displayWidth / 0.875, displayHeight / 0.875, 1);
		manager.bindTexture(TextureMap.locationBlocksTexture);

		GL11.glDisable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + 0.4375 / 2 + height, z, textureX + 0, textureY + sprite.getMaxV() - sprite.getMinV());
		tessellator.addVertexWithUV(x + 0.875, y + 0.4375 / 2 + height, z, textureX + sprite.getMaxU() - sprite.getMinU(), textureY + sprite.getMaxV() - sprite.getMinV());
		tessellator.addVertexWithUV(x + 0.875, y + 0.4375 / 2, z, textureX + sprite.getMaxU() - sprite.getMinU(), textureY + 0);
		tessellator.addVertexWithUV(x, y + 0.4375 / 2, z, textureX + 0, textureY + 0);
		tessellator.draw();

		IHasBars.drawTransparentRect(x + 0.875 - width, y + height + 0.4375 / 2, x, y + 0.4375 / 2, -0.0001, 0xB0000000);
		GL11.glScaled(0.875D / displayWidth, 0.875D / displayHeight, 1);
	}

	public void tryRenderHBMFluid(TextureManager manager, double displayWidth, double displayHeight, ICardReader reader, String fluidName) {
		ResourceLocation texture = CrossModLoader.getCrossMod(ModIDs.HBM).getFluidTexture(fluidName);
		if (texture == null)
			return;

		double x = -0.5D + 1 / 16.0F;
		double y = -0.5D + 1/ 16.0F;
		double z = 0;

		double width = 14 / 16.0F * reader.getInt(DataHelper.AMOUNT) / reader.getInt(DataHelper.CAPACITY);
		double height = 0.4375;

		GL11.glScaled(displayWidth / 0.875, displayHeight / 0.875, 1);
		manager.bindTexture(texture);

		GL11.glDisable(GL11.GL_BLEND);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + 0.4375 / 2 + height, z, 0, 2 * (displayHeight + 0.125D));
		tessellator.addVertexWithUV(x + 0.875, y + 0.4375 / 2 + height, z, 4 * (displayWidth + 0.125D), 2 * (displayHeight + 0.125D));
		tessellator.addVertexWithUV(x + 0.875, y + 0.4375 / 2, z, 4 * (displayWidth + 0.125D), 0);
		tessellator.addVertexWithUV(x, y + 0.4375 / 2, z, 0, 0);
		tessellator.draw();

		IHasBars.drawTransparentRect(x + 0.875 - width, y + height + 0.4375 / 2, x, y + 0.4375 / 2, -0.0001, 0xB0000000);
		GL11.glScaled(0.875D / displayWidth, 0.875D / displayHeight, 1);
	}
}