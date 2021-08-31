package com.zuxelus.energycontroladdon;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(modid = EnergyControlAddon.MODID, name = EnergyControlAddon.NAME, version = EnergyControlAddon.VERSION, dependencies = "required-after:energycontrol")
@EventBusSubscriber
public class EnergyControlAddon {
	public static final String MODID = "energycontroladdon";
	public static final String NAME = "Energy Control Addon";
	public static final String VERSION = "@VERSION@";
	
	@ObjectHolder(MODID + ":kit_furnace")
	public static final Item KIT_FURNACE = null;
	
	@ObjectHolder(MODID + ":card_furnace")
	public static final Item CARD_FURNACE = null;

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new KitFurnace().setRegistryName(new ResourceLocation(MODID, "kit_furnace")),
				new CardFurnace().setRegistryName(new ResourceLocation(MODID, "card_furnace"))
		);
	}
}
