package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardEnergy extends ItemCardMain implements IHasBars {
	private static final int BAR_COLOR = 0xFF2ECC40; // filled part
	private static final int EMPTY_COLOR = 0xB0000000; // darkens the empty part, as on the liquid card

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockEntity te = world.getBlockEntity(target);
		if (te == null)
			return CardState.NO_TARGET;

		NbtCompound tag = CrossModLoader.getEnergyData(te);
		if (tag != null) {
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setString("euType", tag.getString("euType"));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();

		double energy = reader.getDouble("storage");
		double storage = reader.getDouble("maxStorage");
		String euType = reader.getString("euType");

		if ((settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", energy, euType, showLabels));
		if ((settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", storage, euType, showLabels));
		if ((settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFree", storage - energy, euType, showLabels));
		if ((settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPercentage", storage == 0 ? 100 : ((energy / storage) * 100), showLabels));
		return result;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(5);
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelEnergy"), 1));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelFree"), 2));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelCapacity"), 4));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelPercentage"), 8));
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

	// same place and size as the liquid card bar: a band across the middle of the screen
	@Override
	@Environment(EnvType.CLIENT)
	public void renderBars(float displayWidth, float displayHeight, ICardReader reader, MatrixStack matrixStack) {
		double storage = reader.getDouble("maxStorage");
		if (storage <= 0)
			return;
		float x = -0.5F + 1 / 16.0F;
		float y = -0.5F + 1 / 16.0F + 0.4375F / 2;
		float height = 0.4375F;
		float width = 14 / 16.0F * (float) (Math.min(reader.getDouble("storage"), storage) / storage);

		matrixStack.scale(displayWidth / 0.875F, displayHeight / 0.875F, 1);
		IHasBars.drawTransparentRect(matrixStack, x + 0.875F, y + height, x, y, 0, BAR_COLOR);
		IHasBars.drawTransparentRect(matrixStack, x + 0.875F - width, y + height, x, y, -0.0001F, EMPTY_COLOR);
		matrixStack.scale(0.875F / displayWidth, 0.875F / displayHeight, 1);
	}
}
