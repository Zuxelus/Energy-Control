package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.utils.DataHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardEnergy extends ItemCardBase {

	public ItemCardEnergy() {
		super(ItemCardType.CARD_ENERGY, "card_energy");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		NBTTagCompound tag = CrossModLoader.getEnergyData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
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
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(4);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelFree"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelCapacity"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelPercentage"), 8));
		return result;
	}
}
