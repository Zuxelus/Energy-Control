package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.crossmod.ModIDs;
import ic2.api.util.Keys;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

public class KeyboardUtil {
    
    public static boolean isAltKeyDown(EntityPlayer player) {
        return Loader.isModLoaded(ModIDs.IC2) && Keys.instance.isAltKeyDown(player);
    }
}
