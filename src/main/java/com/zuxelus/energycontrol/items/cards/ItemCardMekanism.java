package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardMekanism extends ItemCardMain {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		CompoundNBT tag = CrossModLoader.getCrossMod(ModIDs.MEKANISM).getCardData(te);
		if (tag == null)
			tag = CrossModLoader.getCrossMod(ModIDs.MEKANISM_GENERATORS).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		switch (reader.getInt("type")) {
		case 1:
			String euType = reader.getString("euType");
			result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelTemperature", reader.getString("temp"), showLabels));
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
	public Item getKitFromCard() {
		return ModItems.kit_mekanism.get();
	}
}
