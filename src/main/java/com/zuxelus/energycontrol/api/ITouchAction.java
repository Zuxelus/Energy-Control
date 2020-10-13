package com.zuxelus.energycontrol.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public interface ITouchAction {

	void runTouchAction(PlayerEntity player, World world, ICardReader reader);

	@Environment(EnvType.CLIENT)
	void renderImage(TextureManager manager, Matrix4f matrix4f, ICardReader reader);
}
