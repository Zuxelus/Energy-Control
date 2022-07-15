package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

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
		result.add(new PanelString("msg.ec.InfoPanelPowerUsage", reader.getInt("usage"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelPower", reader.getString("power"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelEnergy", String.format("%s / %s RF",reader.getInt("storage"), reader.getInt("maxStorage")), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelRedstoneMode", reader.getString("rsmode"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelAugmentation", "", showLabels));
		String[] augmentation = reader.getString("augmentation").split(",");
		for (int i = 0; i < augmentation.length; i++)
			result.add(new PanelString(" " + augmentation[i]));
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
