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
        bus.addListener((RegisterCapabilitiesEvent event)->event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,BlockEntityType.CHEST,(be,side)->new net.neoforged.neoforge.fluids.capability.IFluidHandler(){
            public int getTanks(){return 1;}
            public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int tank){return new net.neoforged.neoforge.fluids.FluidStack(be.getBlockPos().getX()==6?net.minecraft.world.level.material.Fluids.LAVA:net.minecraft.world.level.material.Fluids.WATER,(int)(1000+be.getLevel().getGameTime()%1000));}
            public int getTankCapacity(int tank){return 4000;}
            public boolean isFluidValid(int tank,net.neoforged.neoforge.fluids.FluidStack fluid){return true;}
            public int fill(net.neoforged.neoforge.fluids.FluidStack fluid,FluidAction action){return 0;}
            public net.neoforged.neoforge.fluids.FluidStack drain(net.neoforged.neoforge.fluids.FluidStack fluid,FluidAction action){return net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
            public net.neoforged.neoforge.fluids.FluidStack drain(int amount,FluidAction action){return net.neoforged.neoforge.fluids.FluidStack.EMPTY;}
        }));
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
