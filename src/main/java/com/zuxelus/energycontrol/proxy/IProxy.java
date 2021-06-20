package com.zuxelus.energycontrol.proxy;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.io.File;

public interface IProxy extends IGuiHandler {
    
    void loadConfig(FMLPreInitializationEvent event);
    
    void registerSpecialRenderers();
    
    void registerEventHandlers();
    
    void importSound(File configFolder);
    
    String getItemName(ItemStack stack);
}
