package com.zuxelus.energycontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.network.PacketAlarm;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

	@Inject(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("RETURN"))
	private void onPlayerConnect(ClientConnection conn, ServerPlayerEntity player, CallbackInfo ci) {
		EnergyControl.altPressed.put(player, false);
		NetworkHelper.sendToPlayer(player, new PacketAlarm(ConfigHandler.maxAlarmRange, ConfigHandler.allowedAlarms));
	}
}
