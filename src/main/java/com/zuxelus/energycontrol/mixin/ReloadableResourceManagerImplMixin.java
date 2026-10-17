package com.zuxelus.energycontrol.mixin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.zuxelus.energycontrol.utils.SoundHelper;

@Mixin(ReloadableResourceManager.class)
public class ReloadableResourceManagerImplMixin {
	@Shadow @Final
	private PackType type;

	// Adds the custom alarms folder on top of the client resource packs
	@ModifyVariable(method = "createReload", at = @At("HEAD"), argsOnly = true)
	private List<PackResources> addAlarmsPack(List<PackResources> packs) {
		File alarms = SoundHelper.getAlarmsFolder();
		if (type != PackType.CLIENT_RESOURCES || alarms == null)
			return packs;

		List<PackResources> list = new ArrayList<>(packs);
		list.add(new PathPackResources(new PackLocationInfo("energycontrol_alarms", Component.literal("energycontrol_alarms"), PackSource.DEFAULT, Optional.empty()), alarms.toPath()));
		return list;
	}
}
