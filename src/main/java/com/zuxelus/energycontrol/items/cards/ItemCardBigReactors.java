package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import net.minecraft.client.resources.I18n;
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
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return CardState.NO_TARGET;

			reader.setInt("type", 1);
			reader.setBoolean("reactorPoweredB", reactor.getActive());
			reader.setBoolean("cooling", reactor.isPassivelyCooled());
			reader.setString("system", reactor.getPowerSystem().unitOfMeasure);
			reader.setDouble("heat", (double) reactor.getFuelHeat());
			reader.setInt("coreHeat", (int) reactor.getReactorHeat());
			reader.setDouble("storage", (double) reactor.getEnergyStored());
			reader.setDouble("capacity", (double) reactor.getEnergyCapacity());
			reader.setDouble("output", (double) reactor.getEnergyGeneratedLastTick());
			reader.setInt("rods", reactor.getFuelRodCount());
			reader.setInt("fuel", reactor.getFuelAmount());
			reader.setInt("waste", reactor.getWasteAmount());
			reader.setInt("fuelCapacity", reactor.getCapacity());
			reader.setDouble("consumption", (double) reactor.getFuelConsumedLastTick());
			BlockPos min = reactor.getMinimumCoord();
			BlockPos max = reactor.getMaximumCoord();
			reader.setString("size", String.format("%sx%sx%s",max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1));
			return CardState.OK;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return CardState.NO_TARGET;
			
			reader.setInt("type", 2);
			reader.setBoolean("reactorPoweredB", turbine.getActive());
			reader.setString("system", turbine.getPowerSystem().unitOfMeasure);
			reader.setDouble("storage", (double) turbine.getEnergyStored());
			reader.setDouble("capacity", (double) turbine.getEnergyCapacity());
			reader.setDouble("output", (double) turbine.getEnergyGeneratedLastTick());
			reader.setDouble("speed", (double) turbine.getRotorSpeed());
			reader.setDouble("speedMax", (double) turbine.getMaxRotorSpeed());
			reader.setDouble("efficiency", (double) turbine.getRotorEfficiencyLastTick());
			reader.setDouble("consumption", (double) turbine.getFluidConsumedLastTick());
			reader.setInt("blades", turbine.getNumRotorBlades());
			reader.setInt("mass", turbine.getRotorMass());
			BlockPos min = turbine.getMinimumCoord();
			BlockPos max = turbine.getMaximumCoord();
			reader.setString("size", String.format("%sx%sx%s",max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		String euType = reader.getString("system");
		switch (reader.getInt("type")) {
		case 1:
			if ((settings & 2) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelCoreHeat", reader.getInt("heat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelCasingHeat", reader.getDouble("coreHeat"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelPassiveCooling", reader.getBoolean("cooling").toString(), showLabels));
			}
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 8) > 0)
				result.add(
						new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("capacity"), showLabels));
			if ((settings & 16) > 0)
				if (reader.getBoolean("cooling"))
					result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("output"), showLabels));
				else
					result.add(new PanelString("msg.ec.InfoPanelOutputmB", reader.getDouble("output"), showLabels));
			if ((settings & 32) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelFuelmb", reader.getInt("fuel"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelWastemb", reader.getInt("waste"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelCapacitymB", reader.getInt("fuelCapacity"), showLabels));
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
				result.add(new PanelString("msg.ec.InfoPanelRotorEfficiency", reader.getDouble("efficiency"), showLabels));
			}
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 8) > 0)
				result.add(
						new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("capacity"), showLabels));
			if ((settings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("output"), showLabels));
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
	public int getKitId() {
		return ItemCardType.KIT_BIG_REACTORS;
	}
}
