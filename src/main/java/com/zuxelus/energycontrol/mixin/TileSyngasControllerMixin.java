package com.zuxelus.energycontrol.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.zuxelus.energycontrol.crossmod.CrossAdvGenerators;

import net.bdew.generators.controllers.syngas.TileSyngasController;

@Mixin(TileSyngasController.class)
public class TileSyngasControllerMixin {

	@Inject(at = @At("HEAD"), method = "serverTick")
	private void init(CallbackInfoReturnable info) {
		TileSyngasController te = (TileSyngasController)(Object) this;
		if (!CrossAdvGenerators.map.containsKey(te) || te.getLevel().isClientSide)
			return;

		double heat = (Double) te.heat().value();
		double steamBuffer = (Double) te.steamBuffer().value();
		int heatingChambers = (Integer) te.heatingChambers().value();
		int internalTankCapacity = (Integer) te.cfg().internalTankCapacity().apply();
		double maxHeat = (Double) te.cfg().maxHeat().apply();
		double waterSteamRatio = (Double) te.cfg().waterSteamRatio().apply();
		double heatingChamberThroughput = (Double) te.cfg().heatingChamberThroughput().apply();
		double addSteam = 0;

		if (heat > (Double) te.cfg().workHeat().apply() && te.waterTank().getFluidAmount() > 0 &&
			steamBuffer < internalTankCapacity && heatingChambers > 0)
			addSteam = Math.min(
					Math.min(te.waterTank().getFluidAmount() * waterSteamRatio, internalTankCapacity - steamBuffer),
					heatingChambers * heatingChamberThroughput * (heat / maxHeat));

		ArrayList<Double> values = new ArrayList<>();
		values.add(addSteam);
		values.add(Math.ceil(addSteam / waterSteamRatio));
		CrossAdvGenerators.map.put(te, values);
	}
}
