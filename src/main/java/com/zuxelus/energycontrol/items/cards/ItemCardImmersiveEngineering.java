package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardImmersiveEngineering extends ItemCardMain {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getBlockEntity(target);
		CompoundNBT tag = CrossModLoader.getCrossMod(ModIDs.IMMERSIVE_ENGINEERING).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		result.add(new PanelString("msg.ec.InfoPanelEnergyUse", reader.getDouble("rotation"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelEnergyUse", reader.getDouble("per_tick"), showLabels));
		/*if (reader.hasField("usage"))
			result.add(new PanelString("msg.ec.InfoPanelEnergyUse", reader.getInt("usage"), "RF/t", showLabels));
		if (reader.hasField("max_usage"))
			result.add(new PanelString("msg.ec.InfoPanelMaxUse", reader.getInt("max_usage"), "RF/t", showLabels));
		if (reader.hasField("prod"))
			result.add(new PanelString("msg.ec.InfoPanelEnergyProd", reader.getInt("prod"), "RF/t", showLabels));
		if (reader.hasField("max_prod"))
			result.add(new PanelString("msg.ec.InfoPanelMaxProd", reader.getInt("max_prod"), "RF/t", showLabels));
		result.add(new PanelString("msg.ec.InfoPanelEnergy", String.format("%s / %s RF",reader.getInt("storage"), reader.getInt("maxStorage")), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelRedstoneMode", reader.getString("rsmode"), showLabels));
		result.add(new PanelString("msg.ec.InfoPanelAugmentation", "", showLabels));
		String[] augmentation = reader.getString("augmentation").split(",");
		for (int i = 0; i < augmentation.length; i++)
			result.add(new PanelString(" " + augmentation[i]));*/
		addOnOff(result, isServer, reader.getBoolean("active"));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}
}