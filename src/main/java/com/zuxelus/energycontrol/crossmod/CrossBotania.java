package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.block.tile.mana.TileSpreader;

public class CrossBotania extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		return null;
	}

	@Override
	public CompoundNBT getCardData(TileEntity te) {
		if (te instanceof TilePool) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString("euType", "mana");
			tag.putDouble("storage", ((TilePool) te).getCurrentMana());
			tag.putDouble("maxStorage", ((TilePool) te).manaCap);
			return tag;
		}
		if (te instanceof TileSpreader) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString("euType", "mana");
			tag.putDouble("storage", ((TileSpreader) te).getCurrentMana());
			tag.putDouble("maxStorage", ((TileSpreader) te).getMaxMana());
			return tag;
		}
		if (te instanceof TileEntityGeneratingFlower) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString("euType", "mana");
			tag.putDouble("storage", ((TileEntityGeneratingFlower) te).getMana());
			tag.putDouble("maxStorage", ((TileEntityGeneratingFlower) te).getMaxMana());
			if (((TileEntityGeneratingFlower) te).isPassiveFlower())
				tag.putString("age", String.format("%s / %s",
					PanelString.getFormatter().format(((TileEntityGeneratingFlower) te).passiveDecayTicks),
					PanelString.getFormatter().format(BotaniaAPI.instance().getPassiveFlowerDecay())));
			return tag;
		}
		return null;
	}
}
