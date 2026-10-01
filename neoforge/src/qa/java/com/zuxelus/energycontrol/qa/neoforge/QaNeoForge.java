// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa.neoforge;
import com.zuxelus.energycontrol.qa.*;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.minecraft.world.level.block.entity.BlockEntityType;
@Mod("energycontrolqa")
public final class QaNeoForge {
    public QaNeoForge(IEventBus bus) {
        QaServer.init();
        bus.addListener((RegisterCapabilitiesEvent event)->event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,BlockEntityType.CHEST,(be,side)->new IEnergyStorage(){
            public int getEnergyStored(){return (int)(be.getLevel().getGameTime()%1000*100);}
            public int getMaxEnergyStored(){return 100000;}
            public int receiveEnergy(int amount,boolean simulate){return 0;}
            public int extractEnergy(int amount,boolean simulate){return 0;}
            public boolean canReceive(){return false;}
            public boolean canExtract(){return false;}
        }));
    }
}
