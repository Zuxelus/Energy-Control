package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import cofh.thermalexpansion.block.machine.TileMachineBase;
import net.minecraft.tileentity.TileEntity;
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

		TileEntity te = world.getTileEntity(target);
		if (te instanceof TileMachineBase) {
			TileMachineBase machine = (TileMachineBase) te;
			reader.setInt("type", 1);
			reader.setBoolean("active", machine.isActive);
			reader.setInt("usage", machine.getInfoEnergyPerTick());
			reader.setString("rsmode", machine.getControl().name());
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		switch (reader.getInt("type")) {
		case 1:
			result.add(new PanelString("msg.ec.InfoPanelPowerUsage", reader.getInt("usage"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelRedstoneMode", reader.getString("rsmode"), showLabels));
			addOnOff(result, isServer, reader.getBoolean("active"));
			break;
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_THERMAL_EXPANSION;
	}
}
