package com.zuxelus.energycontrol.api;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Lets players interact with Info Panels, using them as touchscreen
 */
public interface ITouchAction {
	
	boolean enableTouch(ItemStack stack);
	
	/**
	 * Called when a player right-clicks a Panel screen. 
	 * If <code>true</code> is returned, an update packet will be sent to the client.
	 * 
	 * @return whether an action was performed
	 */
	boolean runTouchAction(World world, ICardReader reader, ItemStack stack);

	/**
	 * Used to draw objects onto Info Panel displays
	 */
	void renderImage(TextureManager manager, ICardReader reader);
}
