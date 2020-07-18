package com.zuxelus.energycontrol.api;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

public interface ITouchAction {

	void runTouchAction(World world, ICardReader reader);
	
	void renderImage(TextureManager manager, ICardReader reader);
}
