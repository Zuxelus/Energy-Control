package com.zuxelus.energycontrol.items.cards;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import erogenousbeef.core.common.CoordTriplet;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardBigReactors extends ItemCardBase {
	private static DecimalFormat df = new DecimalFormat("0.0");

	public ItemCardBigReactors() {
		super(ItemCardType.CARD_BIG_REACTORS, "card_big_reactors");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return CardState.NO_TARGET;

			reader.setInt("type", 1);
			reader.setBoolean("reactorPoweredB", reactor.getActive());
			reader.setBoolean("cooling", reactor.isPassivelyCooled());
			reader.setString("system", "RF" /*reactor.getPowerSystem().unitOfMeasure*/);
			reader.setDouble("heat", (double) reactor.getFuelHeat());
			reader.setInt("coreHeat", (int) reactor.getReactorHeat());
			reader.setDouble("storage", (double) reactor.getEnergyStored());
			reader.setDouble("capacity", 1.0E7D);
			reader.setDouble("output", (double) reactor.getEnergyGeneratedLastTick());
			reader.setInt("rods", reactor.getFuelRodCount());
			reader.setInt("fuel", reactor.getFuelAmount());
			reader.setInt("waste", reactor.getWasteAmount());
			reader.setInt("fuelCapacity", reactor.getCapacity());
			reader.setDouble("consumption", (double) reactor.getFuelConsumedLastTick());
			CoordTriplet min = reactor.getMinimumCoord();
			CoordTriplet max = reactor.getMaximumCoord();
			reader.setString("size", String.format("%sx%sx%s",max.x - min.x + 1, max.y - min.y + 1, max.z - min.z + 1));
			return CardState.OK;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return CardState.NO_TARGET;
			
			reader.setInt("type", 2);
			reader.setBoolean("reactorPoweredB", turbine.getActive());
			reader.setString("system", "RF" /*turbine.getPowerSystem().unitOfMeasure*/);
			reader.setDouble("storage", (double) turbine.getEnergyStored());
			reader.setDouble("capacity", (double) turbine.getMaxEnergyStored(null));
			reader.setDouble("output", (double) turbine.getEnergyGeneratedLastTick());
			reader.setDouble("speed", (double) turbine.getRotorSpeed());
			reader.setDouble("speedMax", (double) turbine.getMaxRotorSpeed());
			reader.setDouble("efficiency", (double) turbine.getRotorEfficiencyLastTick());
			reader.setDouble("consumption", (double) turbine.getFluidConsumedLastTick());
			reader.setInt("blades", turbine.getNumRotorBlades());
			reader.setInt("mass", turbine.getRotorMass());
			CoordTriplet min = turbine.getMinimumCoord();
			CoordTriplet max = turbine.getMaximumCoord();
			reader.setString("size", String.format("%sx%sx%s",max.x - min.x + 1, max.y - min.y + 1, max.z - min.z + 1));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		String euType = reader.getString("system");
		switch (reader.getInt("type")) {
		case 1:
			if ((displaySettings & 2) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelCoreHeat", reader.getInt("heat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelCasingHeat", reader.getDouble("coreHeat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelPassiveCooling", reader.getBoolean("cooling").toString(), showLabels));
			}
			if ((displaySettings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((displaySettings & 8) > 0)
				result.add(
						new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("capacity"), showLabels));
			if ((displaySettings & 16) > 0)
				if (reader.getBoolean("cooling"))
					result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("output"), showLabels));
				else
					result.add(new PanelString("msg.ec.InfoPanelOutputmB", reader.getDouble("output"), showLabels));
			if ((displaySettings & 32) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelFuelmb", reader.getInt("fuel"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelWastemb", reader.getInt("waste"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelCapacitymB", reader.getInt("fuelCapacity"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelBurnupRatemb", reader.getDouble("consumption"), showLabels));
			}
			if ((displaySettings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelFuelRods", reader.getDouble("rods"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			}
			break;
		case 2:
			if ((displaySettings & 2) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelRotorSpeed", df.format(reader.getDouble("speed")), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelMaxSpeed", reader.getDouble("speedMax"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelRotorEfficiency", reader.getDouble("efficiency"), showLabels));
			}
			if ((displaySettings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((displaySettings & 8) > 0)
				result.add(
						new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("capacity"), showLabels));
			if ((displaySettings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("output"), showLabels));
			if ((displaySettings & 32) > 0)
				result.add(new PanelString("msg.ec.InfoPanelBurnupRatemb", reader.getDouble("consumption"), showLabels));
			if ((displaySettings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelBlades", reader.getInt("blades"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelRotorMass", reader.getInt("mass"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			}
			break;
		}
		if ((displaySettings & 1) > 0)
			addOnOff(result, reader.getBoolean("reactorPoweredB"));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelCapacity"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutputRF"), 16, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFuel"), 32, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelAdditionalInfo"), 64, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_BIG_REACTORS;
	}
}