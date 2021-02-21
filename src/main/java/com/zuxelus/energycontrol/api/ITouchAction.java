package com.zuxelus.energycontrol.api;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ITouchAction {

	boolean runTouchAction(World world, ICardReader reader, ItemStack stack);

	void renderImage(TextureManager manager, ICardReader reader);
}
