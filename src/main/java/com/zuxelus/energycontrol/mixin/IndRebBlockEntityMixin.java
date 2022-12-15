package com.zuxelus.energycontrol.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.maciej916.indreb.common.api.blockentity.IndRebBlockEntity;
import com.maciej916.indreb.common.block.impl.battery_box.BlockEntityBatteryBox;
import com.zuxelus.energycontrol.crossmod.CrossIndustrialReborn;

@Mixin(IndRebBlockEntity.class)
public class IndRebBlockEntityMixin {

	@Inject(at = @At("HEAD"), method = "tickServer")
	private void init(CallbackInfo info) {
		IndRebBlockEntity te = (IndRebBlockEntity)(Object) this;
		if (!CrossIndustrialReborn.map.containsKey(te) || te.getLevel().isClientSide)
			return;

		if (te instanceof BlockEntityBatteryBox) {
			ArrayList<Double> values = CrossIndustrialReborn.map.get(te);
			if (values != null && values.size() > 0) {
				for (int i = 20; i > 0; i--)
					values.set(i, values.get(i - 1));
				values.set(0, (double) te.getEnergyStorage().energyStored());
			} else {
				values = new ArrayList<>();
				for (int i = 0; i < 21; i++)
					values.add((double) te.getEnergyStorage().energyStored());
				CrossIndustrialReborn.map.put(te, values);
			}
		}
	}
}
