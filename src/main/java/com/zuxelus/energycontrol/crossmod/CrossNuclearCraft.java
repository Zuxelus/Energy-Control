package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.OreHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import nc.config.NCConfig;
import nc.init.NCBlocks;
import nc.tile.energy.battery.TileBattery;
import nc.tile.energyFluid.TileEnergyFluid;
import nc.tile.generator.TileFissionController;
import nc.tile.internal.fluid.Tank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class CrossNuclearCraft extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof TileBattery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", ItemCardType.EU_RF);
			tag.setDouble("storage", ((TileBattery) te).getEnergyStored());
			tag.setDouble("maxStorage", ((TileBattery) te).getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public int getReactorHeat(World world, BlockPos pos) {
		TileEntity te;
		for (int xoffset = -1; xoffset < 2; xoffset++)
			for (int yoffset = -1; yoffset < 2; yoffset++)
				for (int zoffset = -1; zoffset < 2; zoffset++) {
					te = world.getTileEntity(pos.east(xoffset).up(yoffset).south(zoffset));
					if (te instanceof TileFissionController)
						return (int) Math.round(((TileFissionController) te).heat);
				}
		return -1;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (te instanceof TileEnergyFluid) {
			List<Tank> tanks = ((TileEnergyFluid) te).getTanks();
			List<IFluidTank> result = new ArrayList<>();
			for (Tank tank : tanks)
				result.add((IFluidTank) tank);
			return result;
		}
		return null;
	}

	@Override
	public void loadOreInfo() {
		for (int i = 0; i < 8; i++)
			if (NCConfig.ore_gen[i])
				EnergyControl.oreHelper.put(OreHelper.getId(NCBlocks.ore, i), new OreHelper(NCConfig.ore_min_height[i], NCConfig.ore_max_height[i],  NCConfig.ore_size[i] + 2, NCConfig.ore_rate[i]));
	}
}
