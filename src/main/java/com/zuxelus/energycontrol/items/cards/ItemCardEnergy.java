package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.DataHelper;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCardEnergy extends ItemCardMain {

	@Override
	public CardState update(Level world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockEntity te = world.getBlockEntity(target);
		if (te == null)
			return CardState.NO_TARGET;

		CompoundTag tag = CrossModLoader.getEnergyData(te);
		if (tag != null) {
			reader.setDouble(DataHelper.ENERGY, tag.getDouble(DataHelper.ENERGY));
			reader.setDouble(DataHelper.CAPACITY, tag.getDouble(DataHelper.CAPACITY));
			reader.setString(DataHelper.EUTYPE, tag.getString(DataHelper.EUTYPE));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(Level world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();

		double energy = reader.getDouble(DataHelper.ENERGY);
		double storage = reader.getDouble(DataHelper.CAPACITY);
		String euType = reader.getString(DataHelper.EUTYPE);

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
	@OnlyIn(Dist.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(4);
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelEnergy"), 1));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelFree"), 2));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelCapacity"), 4));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelPercentage"), 8));
		return result;
	}

	@Override
	public boolean isRemoteCard() {
		return true;
	}
}
