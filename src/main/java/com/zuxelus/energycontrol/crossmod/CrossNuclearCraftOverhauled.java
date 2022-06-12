package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.OreHelper;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardNuclearCraft;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.items.kits.ItemKitNuclearCraft;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import nc.config.NCConfig;
import nc.init.NCBlocks;
import nc.multiblock.battery.tile.TileBattery;
import nc.multiblock.fission.FissionReactor;
import nc.multiblock.fission.FissionReactorLogic;
import nc.multiblock.fission.salt.MoltenSaltFissionLogic;
import nc.multiblock.fission.salt.tile.TileSaltFissionController;
import nc.multiblock.fission.solid.SolidFuelFissionLogic;
import nc.multiblock.fission.solid.tile.TileSolidFissionController;
import nc.tile.energyFluid.TileEnergyFluid;
import nc.tile.generator.TileDecayGenerator;
//import nc.tile.generator.TileFissionController;
import nc.tile.generator.TileSolarPanel;
import nc.tile.internal.fluid.Tank;
import nc.tile.processor.TileFluidProcessor;
import nc.tile.processor.TileItemProcessor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.IFluidTank;

import java.util.ArrayList;
import java.util.List;

public class CrossNuclearCraftOverhauled extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof TileBattery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString(DataHelper.EUTYPE, "RF");
			tag.setDouble(DataHelper.ENERGY, ((TileBattery) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileBattery) te).getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public int getHeat(World world, BlockPos pos) {
		TileEntity te;
		for (int xoffset = -1; xoffset < 2; xoffset++)
			for (int yoffset = -1; yoffset < 2; yoffset++)
				for (int zoffset = -1; zoffset < 2; zoffset++) {
					te = world.getTileEntity(pos.east(xoffset).up(yoffset).south(zoffset));
					/*if (te instanceof TileFissionController)
						return (int) Math.round(((TileFissionController) te).heat);*/
				}
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (te instanceof TileEnergyFluid) {
			List<Tank> tanks = ((TileEnergyFluid) te).getTanks();
			List<FluidInfo> result = new ArrayList<>();
			for (Tank tank : tanks)
				result.add(new FluidInfo(tank));
			return result;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileDecayGenerator) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 1);
			tag.setInteger(DataHelper.ENERGY, ((TileDecayGenerator) te).getEnergyStored());
			tag.setInteger(DataHelper.CAPACITY, ((TileDecayGenerator) te).getMaxEnergyStored());
			tag.setInteger("output", ((TileDecayGenerator) te).getGenerated());
			tag.setDouble("radiation", ((TileDecayGenerator) te).getRadiation());
			return tag;
		}
		if (te instanceof TileItemProcessor) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 2);
			tag.setInteger(DataHelper.ENERGY, ((TileItemProcessor) te).getEnergyStored());
			tag.setInteger(DataHelper.CAPACITY, ((TileItemProcessor) te).getMaxEnergyStored());
			tag.setInteger("power", ((TileItemProcessor) te).getProcessPower());
			tag.setDouble("speedM", ((TileItemProcessor) te).getSpeedMultiplier());
			tag.setDouble("powerM", ((TileItemProcessor) te).getPowerMultiplier());
			tag.setInteger("time", ((TileItemProcessor) te).getProcessTime());
			return tag;
		}
		if (te instanceof TileFluidProcessor) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 2);
			tag.setInteger(DataHelper.ENERGY, ((TileFluidProcessor) te).getEnergyStored());
			tag.setInteger(DataHelper.CAPACITY, ((TileFluidProcessor) te).getMaxEnergyStored());
			tag.setInteger("power", ((TileFluidProcessor) te).getProcessPower());
			tag.setDouble("speedM", ((TileFluidProcessor) te).getSpeedMultiplier());
			tag.setDouble("powerM", ((TileFluidProcessor) te).getPowerMultiplier());
			tag.setInteger("time", ((TileFluidProcessor) te).getProcessTime());
			return tag;
		}
		if (te instanceof TileSolarPanel) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 4);
			tag.setInteger(DataHelper.ENERGY, ((TileSolarPanel) te).getEnergyStored());
			tag.setInteger(DataHelper.CAPACITY, ((TileSolarPanel) te).getMaxEnergyStored());
			tag.setInteger("output", ((TileSolarPanel) te).getGenerated());
			return tag;
		}
		if (te instanceof TileSolidFissionController) {
			FissionReactor reactor = ((TileSolidFissionController) te).getMultiblock();
			SolidFuelFissionLogic logic = (SolidFuelFissionLogic) reactor.getLogic();
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 8);
			tag.setBoolean("active", reactor.isReactorOn);
			tag.setString("size", reactor.getInteriorLengthX() + "*" + reactor.getInteriorLengthY() + "*" + reactor.getInteriorLengthZ());
			tag.setInteger("clusterCount", reactor.clusterCount);
			tag.setDouble("meanEfficiency", reactor.meanEfficiency * 100);
			tag.setDouble("outputRate", logic.heatingOutputRateFP);
			tag.setDouble("sparsityEfficiencyMult", reactor.sparsityEfficiencyMult);
			tag.setInteger("temp", logic.getTemperature());
			tag.setLong("rawHeating", reactor.rawHeating);
			tag.setLong("heat", logic.getNetClusterHeating());
			tag.setString("level", String.format("%s / %s H",logic.heatBuffer.getHeatStored(), logic.heatBuffer.getHeatCapacity()));
			tag.setLong("cooling", -reactor.cooling);
			/*tag.setLong("totalHeatMult", reactor.totalHeatMult);
			tag.setDouble("meanHeatMult", reactor.meanHeatMult);
			tag.setInteger("fuelComponentCount", reactor.fuelComponentCount);
			tag.setLong("usefulPartCount", reactor.usefulPartCount);
			tag.setDouble("totalEfficiency", reactor.totalEfficiency);*/
			return tag;
		}
		if (te instanceof TileSaltFissionController) {
			FissionReactor reactor = ((TileSaltFissionController) te).getMultiblock();
			MoltenSaltFissionLogic logic = (MoltenSaltFissionLogic) reactor.getLogic();
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 9);
			tag.setBoolean("active", reactor.isReactorOn);
			tag.setString("size", reactor.getInteriorLengthX() + "*" + reactor.getInteriorLengthY() + "*" + reactor.getInteriorLengthZ());
			tag.setInteger("clusterCount", reactor.clusterCount);
			tag.setDouble("meanEfficiency", reactor.meanEfficiency * 100);
			tag.setDouble("sparsityEfficiencyMult", reactor.sparsityEfficiencyMult);
			tag.setInteger("temp", logic.getTemperature());
			tag.setLong("rawHeating", reactor.rawHeating);
			tag.setLong("heat", logic.getNetClusterHeating());
			tag.setString("level", String.format("%s / %s H",logic.heatBuffer.getHeatStored(), logic.heatBuffer.getHeatCapacity()));
			tag.setLong("cooling", -reactor.cooling);
			return tag;
		}
		return null;
	}

	@Override
	public void registerItems(Register<Item> event) {
		ItemKitMain.register(ItemKitNuclearCraft::new);
		ItemCardMain.register(ItemCardNuclearCraft::new);
	}

	@Override
	public void loadOreInfo() {
		for (int i = 0; i < 8; i++)
			if (NCConfig.ore_gen[i])
				EnergyControl.oreHelper.put(OreHelper.getId(NCBlocks.ore, i), new OreHelper(NCConfig.ore_min_height[i], NCConfig.ore_max_height[i],  NCConfig.ore_size[i] + 2, NCConfig.ore_rate[i]));
	}
}
