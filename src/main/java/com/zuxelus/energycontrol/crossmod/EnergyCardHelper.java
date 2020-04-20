package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.utils.CardState;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyCardHelper {
	public static EnergyStorageData getStorageAt(World world, BlockPos pos, int type) {
		if (world == null)
			return null;
		
		TileEntity entity = world.getTileEntity(pos);
		switch (type) {
			case EnergyStorageData.TARGET_TYPE_IC2:
				return CrossModLoader.crossIc2.getEnergyStorageData(entity);
			/*case EnergyStorageData.TARGET_TYPE_RF:
				return CrossModLoader.crossRF.getStorageData(entity);
			case EnergyStorageData.TARGET_TYPE_GT6:
				return CrossModLoader.crossGT6.getStorageData(entity);*/
			case EnergyStorageData.TARGET_TYPE_UNKNOWN:
				EnergyStorageData data = CrossModLoader.crossIc2.getEnergyStorageData(entity);
				/*if (data == null)
					data = CrossModLoader.crossGT6.getStorageData(entity);
				if (data == null)
					data = CrossModLoader.crossRF.getStorageData(entity);*/
				return data;
		}
		return null;
	}

	public static CardState updateCardValues(ItemCardReader reader, EnergyStorageData storage, int type) {
		switch (type) {
		case EnergyStorageData.TARGET_TYPE_IC2:
			reader.setDouble("maxStorageL", storage.values.get(0));
			reader.setDouble("energyL", storage.values.get(1));
			return CardState.OK;
		/*case EnergyStorageData.TARGET_TYPE_GT6:
			return CrossModLoader.crossGT6.updateCardValues(card, storage);*/
		default:
			return CardState.NO_TARGET;
		}
	}
}
