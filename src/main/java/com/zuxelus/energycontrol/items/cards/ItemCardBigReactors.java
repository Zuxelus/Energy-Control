package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemCardBigReactors extends ItemCardBase {
	private static final DecimalFormat df = new DecimalFormat("0.0");

	public ItemCardBigReactors() {
		super(ItemCardType.CARD_BIG_REACTORS, "card_big_reactors");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.BIG_REACTORS).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		switch (reader.getInt("type")) {
		case 1:
			if ((settings & 2) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelCoreHeat", reader.getInt("heat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelCasingHeat", reader.getDouble("coreHeat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelPassiveCooling", reader.getBoolean("cooling").toString(), showLabels));
			}
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getString("storage") + " FE", showLabels));
			if ((settings & 16) > 0)
				if (reader.getBoolean("cooling"))
					result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), "FE/t", showLabels));
				else
					result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), "mB/t", showLabels));
			if ((settings & 32) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuel") + " mB", showLabels));
				result.add(new PanelString("msg.ec.InfoPanelWastemb", reader.getDouble("waste"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelBurnupRatemb", reader.getDouble("consumption"), showLabels));
			}
			if ((settings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelFuelRods", reader.getDouble("rods"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			}
			break;
		case 2:
			if ((settings & 2) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelRotorSpeed", df.format(reader.getDouble("speed")), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelMaxSpeed", reader.getDouble("speedMax"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelRotorEfficiency", reader.getDouble("efficiency"), "%", showLabels));
			}
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getString("storage"), showLabels));
			if ((settings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), "FE/t", showLabels));
			if ((settings & 32) > 0)
				result.add(new PanelString("msg.ec.InfoPanelBurnupRatemb", reader.getDouble("consumption"), showLabels));
			if ((settings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelBlades", reader.getInt("blades"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelRotorMass", reader.getInt("mass"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			}
			break;
		}
		if ((settings & 1) > 0)
			addOnOff(result, isServer, reader.getBoolean("reactorPoweredB"));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelCapacity"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFuel"), 32));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOther"), 64));
		return result;
	}
}
