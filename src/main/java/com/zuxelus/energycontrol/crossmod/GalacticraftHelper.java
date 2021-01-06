package com.zuxelus.energycontrol.crossmod;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.oxygen.OxygenPressureProtocol;
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
import net.minecraft.block.Block;

public class GalacticraftHelper {

	public static String getStatus(TileEntityOxygenCollector collector) {
		String returnValue = collector.getGUIstatus();
		if (returnValue.equals(EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.active.name")) && collector.lastOxygenCollected <= 0.0F)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingleaves.name");
		return returnValue;
	}

	public static String getThermalStatus(TileEntityOxygenSealer sealer) {
		Block blockAbove = sealer.getWorldObj().getBlock(sealer.xCoord, sealer.yCoord + 1, sealer.zCoord);
		int metadata = sealer.getWorldObj().getBlockMetadata(sealer.xCoord, sealer.yCoord + 1, sealer.zCoord);
		if (blockAbove == GCBlocks.breatheableAir || blockAbove == GCBlocks.brightBreatheableAir)
			if (metadata == 1)
				return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.on.name");

		if (sealer.thermalControlEnabled())
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.notAvailable.name");

		return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.off.name");
	}

	public static String getStatus(TileEntityOxygenSealer sealer) {
		Block blockAbove = sealer.getWorldObj().getBlock(sealer.xCoord, sealer.yCoord + 1, sealer.zCoord);

		if (blockAbove != null
				&& !(blockAbove.isAir(sealer.getWorldObj(), sealer.xCoord, sealer.yCoord + 1, sealer.zCoord))
				&& !OxygenPressureProtocol.canBlockPassAir(sealer.getWorldObj(), blockAbove, new BlockVec3(sealer.xCoord, sealer.yCoord + 1, sealer.zCoord), 1))
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.sealerblocked.name");

		if (sealer.disabled)
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");

		if (sealer.getEnergyStoredGC() == 0)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingpower.name");

		if (sealer.getEnergyStoredGC() < sealer.storage.getMaxExtract())
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.missingpower.name");

		if (sealer.storedOxygen < 1)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingoxygen.name");

		if (sealer.calculatingSealed)
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.checkingSeal.name") + "...";

		int threadCooldown = sealer.getScaledThreadCooldown(25);
		if (threadCooldown < 15) {
			if (threadCooldown < 4) {
				String elipsis = "";
				for (int i = 0; i < (23 - threadCooldown) % 4; i++)
					elipsis += ".";
				return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.checkStarting.name") + elipsis;
			}
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.checkPending.name");
		}
		if (!sealer.sealed)
			return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.unsealed.name");
		return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.sealed.name");
	}

	public static String getStatus(TileEntityRefinery te) {
		String displayText = "";
		if (te.oilTank.getFluid() == null || te.oilTank.getFluidAmount() == 0)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.nooil.name");
		else if (te.oilTank.getFluidAmount() > 0 && te.disabled)
			displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.ready.name");
		else if (te.canProcess())
			displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.refining.name");
		else
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.unknown.name");
		return displayText;
	}

	public static String getStatus(TileEntityElectrolyzer te) {
		String displayText = "";
		if (RedstoneUtil.isBlockReceivingRedstone(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord))
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.off.name");
		else if (!te.hasEnoughEnergyToRun)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.lowEnergy.name");
		else if (te.waterTank.getFluid() == null || te.waterTank.getFluidAmount() == 0)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.zeroWater.name");
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
		if (RedstoneUtil.isBlockReceivingRedstone(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord))
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.off.name");
		else if (!te.hasEnoughEnergyToRun)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.lowEnergy.name");
		else if ((te.processTicks > -8 || te.canProcess()))
			displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.processing.name");
		else if (te.gasTank.getFluid() == null || te.gasTank.getFluidAmount() == 0)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.nogas.name");
		else if (te.gasTank.getFluidAmount() > 0 && te.disabled)
			displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.ready.name");
		else if (te.liquidTank.getFluidAmount() == te.liquidTank.getCapacity())
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.tankfull.name");
		else
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.needsCarbon.name");
		return displayText;
	}

	public static String getStatus(TileEntityGasLiquefier te) {
		String displayText = "";
		if (RedstoneUtil.isBlockReceivingRedstone(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord))
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.off.name");
		else if (!te.hasEnoughEnergyToRun)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.lowEnergy.name");
		else if ((te.processTicks > -10 || te.canProcess()))
			displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.liquefying.name");
		else if (te.gasTank.getFluid() == null || te.gasTank.getFluidAmount() <= 0)
			displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.nogas.name");
		else if (te.gasTank.getFluidAmount() > 0 && te.disabled)
			displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.ready.name");
		else if (te.liquidTank.getFluidAmount() == te.liquidTank.getCapacity() && te.liquidTank2.getFluidAmount() == te.liquidTank2.getCapacity())
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
			return EnumColor.RED + GCCoreUtil.translate("gui.message.invalidFreq.name");
		if (te.getEnergyStoredGC() <= 0.0F)
			return EnumColor.RED + GCCoreUtil.translate("gui.message.noEnergy.name");
		if (te.getDisabled(0))
			return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
		return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.active.name");
	}
}
