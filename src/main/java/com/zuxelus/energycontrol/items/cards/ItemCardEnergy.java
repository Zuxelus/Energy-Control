package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemCardEnergy extends ItemCardBase {

	public ItemCardEnergy() {
		super(ItemCardType.CARD_ENERGY, "card_energy");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		if (te == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getEnergyData(te);
		if (tag != null && tag.hasKey("type")) {
			reader.setInt("type", tag.getInteger("type"));
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			if (tag.getInteger("type") == 12)
				reader.setString("euType", tag.getString("euType"));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();

		double energy = reader.getDouble("storage");
		double storage = reader.getDouble("maxStorage");
		String euType = "";

		switch (reader.getInt("type")) {
		case 10:
			euType = "AE";
			break;
		case 11:
			euType = "gJ";
			break;
		case 12:
			euType = reader.getString("euType");
			break;
		case 13:
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
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(4);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelFree"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelCapacity"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelPercentage"), 8, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_ENERGY;
	}
}
