package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCardEnergy extends ItemCardMain {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getBlockEntity(target);
		if (te == null)
			return CardState.NO_TARGET;

		CompoundNBT tag = CrossModLoader.getEnergyData(te);
		if (tag != null && tag.contains("type")) {
			reader.setInt("type", tag.getInt("type"));
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			if (tag.getInt("type") == 12)
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
		String euType;

		switch (reader.getInt("type")) {
		case ItemCardType.EU_AE:
			euType = "AE";
			break;
		case ItemCardType.EU_gJ:
			euType = "gJ";
			break;
		case 12:
			euType = reader.getString("euType");
			break;
		case ItemCardType.EU_RF:
			euType = "RF";
			break;
		default:
			euType = "EU";
			break;
		}
		if ((settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, energy, showLabels));
		if ((settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, storage, showLabels));
		if ((settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFree" + euType, storage - energy, showLabels));
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
	public Item getKitFromCard() {
		return ModItems.kit_energy.get();
	}

	@Override
	public boolean isRemoteCard() {
		return true;
	}
}
