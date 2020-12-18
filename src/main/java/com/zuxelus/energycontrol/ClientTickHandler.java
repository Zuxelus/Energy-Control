package com.zuxelus.energycontrol;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientTickHandler {
	public final static ClientTickHandler instance = new ClientTickHandler();

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event)
	{
		if (EnergyControl.oreHelper == null)
			return;
		ItemStack stack = event.getItemStack();
		if (stack.isEmpty())
			return;
		OreHelper ore = EnergyControl.oreHelper.get(OreHelper.getId(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage()));
		if (ore != null)
			event.getToolTip().add(1, ore.getDescription());
	}
}
