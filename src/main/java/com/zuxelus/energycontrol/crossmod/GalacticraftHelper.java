package com.zuxelus.energycontrol.crossmod;

import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.fluid.OxygenPressureProtocol;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCollector;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer;
import micdoodle8.mods.galacticraft.core.tile.TileEntityRefinery;
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityElectrolyzer;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityGasLiquefier;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer;
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayController;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class GalacticraftHelper {

	public static String getStatus(TileEntityOxygenCollector collector) {
		String returnValue = collector.getGUIstatus();
		if (returnValue.equals(EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.active.name")) && collector.lastOxygenCollected <= 0.0F)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingleaves.name");
		return returnValue;
	}

	public static String getThermalStatus(TileEntityOxygenSealer sealer) {
		IBlockState stateAbove = sealer.getWorld().getBlockState(sealer.getPos().up());
		Block blockAbove = stateAbove.getBlock();
		if (blockAbove == GCBlocks.breatheableAir || blockAbove == GCBlocks.brightBreatheableAir)
			if (blockAbove.getMetaFromState(stateAbove) == 1)
				return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.on.name");

		if (sealer.thermalControlEnabled())
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.not_available.name");

		return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.off.name");
	}

	public static String getStatus(TileEntityOxygenSealer sealer) {
		BlockPos blockPosAbove = sealer.getPos().up();
		Block blockAbove = sealer.getWorld().getBlockState(blockPosAbove).getBlock();
		IBlockState state = sealer.getWorld().getBlockState(blockPosAbove);

		if (!(blockAbove.isAir(state, sealer.getWorld(), blockPosAbove)) && !OxygenPressureProtocol.canBlockPassAir(sealer.getWorld(), blockAbove, blockPosAbove, EnumFacing.UP))
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.sealerblocked.name");

		if (sealer.disabled)
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");

		if (sealer.getEnergyStoredGC() == 0)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingpower.name");

		if (sealer.getEnergyStoredGC() < sealer.storage.getMaxExtract())
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.missingpower.name");

		if (sealer.getOxygenStored() < 1)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingoxygen.name");

		if (sealer.calculatingSealed)
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.checking_seal.name") + "...";

		int threadCooldown = sealer.getScaledThreadCooldown(25);
		if (threadCooldown < 15) {
			if (threadCooldown < 4) {
				String elipsis = "";
				for (int i = 0; i < (23 - threadCooldown) % 4; i++)
					elipsis += ".";
				return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.check_starting.name") + elipsis;
			}
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.check_pending.name");
		}
		if (!sealer.sealed)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.unsealed.name");
		return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.sealed.name");
	}

	public static String getStatus(TileEntityRefinery te) {
		String missingInput = null;
		if (te.oilTank.getFluid() == null || te.oilTank.getFluidAmount() == 0)
			missingInput = EnumColor.RED + GCCoreUtil.translate("gui.status.nooil.name");

		String activeString = te.canProcess() ? EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.refining.name") : null;
		return te.getGUIstatus(missingInput, activeString, false);
	}

	public static String getStatus(TileEntityElectrolyzer te) {
		String displayText = "";
		if (RedstoneUtil.isBlockReceivingRedstone(te.getWorld(), te.getPos()))
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.off.name");
		else if (!te.hasEnoughEnergyToRun)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.low_energy.name");
		else if (te.waterTank.getFluid() == null || te.waterTank.getFluidAmount() == 0)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.zero_water.name");
		else if (te.waterTank.getFluidAmount() > 0 && te.disabled)
			displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.ready.name");
		else if (te.canProcess())
			displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.running.name");
		else if (te.liquidTank.getFluidAmount() == te.liquidTank.getCapacity() && te.liquidTank2.getFluidAmount() == te.liquidTank2.getCapacity())
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.tanksfull.name");
		else
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.unknown.name");
		return displayText;
	}

	public static String getStatus(TileEntityMethaneSynthesizer te) {
		String displayText = "";
		if (RedstoneUtil.isBlockReceivingRedstone(te.getWorld(), te.getPos()))
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.off.name");
		else if (!te.hasEnoughEnergyToRun)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.low_energy.name");
		else if ((te.processTicks > -8 || te.canProcess()))
			displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.processing.name");
		else if (te.gasTank.getFluid() == null || te.gasTank.getFluidAmount() == 0)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.nogas.name");
		else if (te.gasTank.getFluidAmount() > 0 && te.disabled)
			displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.ready.name");
		else if (te.liquidTank.getFluidAmount() == te.liquidTank.getCapacity())
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.tankfull.name");
		else
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.needs_carbon.name");
		return displayText;
	}

	public static String getStatus(TileEntityGasLiquefier te) {
		String displayText = "";
		if (RedstoneUtil.isBlockReceivingRedstone(te.getWorld(), te.getPos()))
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.off.name");
		else if (!te.hasEnoughEnergyToRun)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.low_energy.name");
		else if ((te.processTicks > -10 || te.canProcess()))
			displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.liquefying.name");
		else if (te.gasTank.getFluid() == null || te.gasTank.getFluidAmount() <= 0)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.nogas.name");
		else if (te.gasTank.getFluidAmount() > 0 && te.disabled)
			displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.ready.name");
		else if (te.liquidTank.getFluidAmount() == te.liquidTank.getCapacity()
				&& te.liquidTank2.getFluidAmount() == te.liquidTank2.getCapacity())
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.tanksfull.name");
		else
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.unknown.name");
		return displayText;
	}

	public static String getStatus(TileEntitySolar te) {
		if (te.getDisabled(0))
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
		if (!te.getWorld().isDaytime())
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedfully.name");
		if (te.getWorld().isRaining() || te.getWorld().isThundering())
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.raining.name");
		if (te.solarStrength == 0)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedfully.name");
		if (te.solarStrength < 9)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedpartial.name");
		if (te.generateWatts > 0)
			return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.collectingenergy.name");
		return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.unknown.name");
	}

	public static String getStatus(TileEntityLaunchController te) {
		if (!te.frequencyValid)
			return EnumColor.RED + GCCoreUtil.translate("gui.message.invalid_freq.name");
		if (te.getEnergyStoredGC() <= 0.0F)
			return EnumColor.RED + GCCoreUtil.translate("gui.message.no_energy.name");
		if (te.getDisabled(0))
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
		return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.active.name");
	}

	public static String getStatus(TileEntitySolarArrayController te) {
		if (te.getDisabled(0))
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
		if (!te.getWorld().isDaytime())
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedfully.name");
		if (te.getWorld().isRaining() || te.getWorld().isThundering())
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.raining.name");
		float sunVisible = (float) Math.floor(te.getActualArraySize() / (float) te.getPossibleArraySize());
		if (sunVisible == 0)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedfully.name");
		if (sunVisible < 1.0F)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.blockedpartial.name");
		if (te.generateWatts > 0)
			return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.collectingenergy.name");
		return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.unknown.name");
	}
}
