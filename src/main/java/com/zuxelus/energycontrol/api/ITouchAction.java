package com.zuxelus.energycontrol.api;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Lets players interact with Info Panels, using them as touchscreen
 */
public interface ITouchAction {
	
	boolean enableTouch();
	
	/**
	 * Called when a player right-clicks a Panel screen. 
	 * If <code>true</code> is returned, an update packet will be sent to the client.
	 * 
	 * @return whether an action was performed
	 */
	boolean runTouchAction(Level world, ICardReader reader, ItemStack stack);

	/**
	 * Used to draw objects onto Info Panel displays
	 */
	void renderImage(ICardReader reader, PoseStack matrixStack);
}
