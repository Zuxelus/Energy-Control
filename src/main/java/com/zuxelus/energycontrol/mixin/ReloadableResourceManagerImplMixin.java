package com.zuxelus.energycontrol.mixin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.zuxelus.energycontrol.utils.SoundHelper;

import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {
	@Shadow @Final
	private ResourceType type;

	// Adds the custom alarms folder on top of the client resource packs
	@ModifyVariable(method = "reload", at = @At("HEAD"), argsOnly = true)
	private List<ResourcePack> addAlarmsPack(List<ResourcePack> packs) {
		File alarms = SoundHelper.getAlarmsFolder();
		if (type != ResourceType.CLIENT_RESOURCES || alarms == null)
			return packs;

		List<ResourcePack> list = new ArrayList<>(packs);
		list.add(new DirectoryResourcePack("energycontrol_alarms", alarms.toPath(), false));
		return list;
	}
}
