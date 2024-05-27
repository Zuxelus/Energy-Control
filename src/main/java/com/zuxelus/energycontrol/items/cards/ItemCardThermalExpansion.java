package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.utils.DataHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardThermalExpansion extends ItemCardBase {

	public ItemCardThermalExpansion() {
		super(ItemCardType.CARD_THERMAL_EXPANSION, "card_thermal_expansion");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.THERMAL_EXPANSION).getCardData(world, target);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField("usage"))
			result.add(new PanelString("msg.ec.InfoPanelPowerUsage", reader.getInt("usage"), showLabels));
		if (reader.hasField("power"))
			result.add(new PanelString("msg.ec.InfoPanelPower", reader.getString("power"), showLabels));
		if (reader.hasField("storage"))
			result.add(new PanelString("msg.ec.InfoPanelEnergy", String.format("%s / %s RF",reader.getInt("storage"), reader.getInt("maxStorage")), showLabels));
		if (reader.hasField(DataHelper.DIFF) && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getLong(DataHelper.DIFF), "RF/t", showLabels));
		if (reader.hasField("rsmode"))
			result.add(new PanelString("msg.ec.InfoPanelRedstoneMode", reader.getString("rsmode"), showLabels));
		if (reader.hasField("augmentation")) {
			result.add(new PanelString("msg.ec.InfoPanelAugmentation", "", showLabels));
			String[] augmentation = reader.getString("augmentation").split(",");
			for (int i = 0; i < augmentation.length; i++)
				result.add(new PanelString(" " + augmentation[i]));
		}
		if (reader.hasField("active"))
			addOnOff(result, isServer, reader.getBoolean("active"));
		switch (reader.getInt("type")) {
		case 2:
			result.add(new PanelString(reader.getString("lock")));
			break;
		case 3:
			result.add(new PanelString(reader.getString("lock")));
			result.add(new PanelString("msg.ec.InfoPanelWater", reader.getString("water"), showLabels));
			break;
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		return null;
	}
}
