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

public class ItemCardIC2 extends ItemCardBase {
	public ItemCardIC2() {
		super(ItemCardType.CARD_IC2, "card_ic2");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.IC2).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField(DataHelper.HEAT) && (settings & 1) > 0)
			addHeat(result, reader.getInt(DataHelper.HEAT), reader.getInt(DataHelper.MAXHEAT), showLabels);
		if (reader.hasField(DataHelper.MAXHEAT) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxHeat", reader.getInt(DataHelper.MAXHEAT), showLabels));
		if (reader.hasField(DataHelper.MAXHEAT) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMelting", reader.getInt(DataHelper.MAXHEAT) * 85 / 100, showLabels));
		if (reader.hasField(DataHelper.ENERGY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGY), "EU", showLabels));
		if (reader.hasField(DataHelper.ENERGYHU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGYHU), "HU", showLabels));
		if (reader.hasField(DataHelper.ENERGYKU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGYKU), "KU", showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITY), "EU", showLabels));
		if (reader.hasField(DataHelper.CAPACITYHU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITYHU), "HU", showLabels));
		if (reader.hasField(DataHelper.CAPACITYKU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITYKU), "KU", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "EU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTHU) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTHU), "HU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTKU) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTKU), "KU/t", showLabels));
		if (reader.hasField("outputmB") && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("outputmB"), "mB/s", showLabels));
		if (reader.hasField(DataHelper.MULTIPLIER) && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble(DataHelper.MULTIPLIER), showLabels));
		if (reader.hasField(DataHelper.TANK) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK), showLabels));
		if (reader.hasField(DataHelper.TANK2) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK2), showLabels));
		if (reader.hasField("pellets") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPellets", reader.getInt("pellets"), showLabels));
		if (reader.hasField("motors") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMotors", reader.getInt("motors"), showLabels));
		if (reader.hasField("coils") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCoils", reader.getInt("coils"), showLabels));
		if (reader.hasField("conductors") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConductors", reader.getInt("conductors"), showLabels));
		if (reader.hasField("wind") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelWindStrength", reader.getDouble("wind"), showLabels));
		if (reader.hasField("waterFlow") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelWaterFlow", reader.getDouble("waterFlow"), showLabels));
		if (reader.hasField("height") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeight", reader.getInt("height"), showLabels));
		if (reader.hasField("health") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelRotorHealth", reader.getDouble("health"), showLabels));
		if (reader.hasField("timeLeft") && (settings & 64) > 0) {
			int timeLeft = reader.getInt("timeLeft");
			int hours = timeLeft / 3600;
			int minutes = (timeLeft % 3600) / 60;
			int seconds = timeLeft % 60;
			result.add(new PanelString("msg.ec.InfoPanelTimeRemaining", String.format("%d:%02d:%02d", hours, minutes, seconds), showLabels));
		}
		if (reader.hasField("active") && (settings & 32) > 0)
			addOnOff(result, isServer, reader.getBoolean("active"));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(7);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMultiplier"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTank"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 32));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOther"), 64));
		return result;
	}
}