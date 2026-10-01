// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.neoforge;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.core.EnergySnapshot;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import java.util.Optional;

@Mod(EnergyControlPort.ID)
public final class EnergyControlNeoForge {
    public EnergyControlNeoForge(IEventBus bus) {
        EnergyControlPort.machineProbe=IeMachineProbe::read;
        EnergyControlPort.inventoryProbe=(level,pos,side)->{
            if(level.isClientSide||!level.hasChunkAt(pos))return Optional.empty();
            var storage=level.getCapability(Capabilities.ItemHandler.BLOCK,pos,side);
            if(storage==null)return com.zuxelus.energycontrol.port.inventory.InventoryProbe.vanilla(level,pos,side);
            var c=new com.zuxelus.energycontrol.port.inventory.InventorySnapshot.Collector(com.zuxelus.energycontrol.port.inventory.InventoryProbe.name(level,pos),level.getBlockEntity(pos) instanceof net.minecraft.world.WorldlyContainer,true,storage.getSlots());
            for(int i=0;i<Math.min(storage.getSlots(),com.zuxelus.energycontrol.port.inventory.InventorySnapshot.LIMIT);i++){var stack=storage.getStackInSlot(i);c.add(stack.getHoverName().getString(),stack.getCount(),stack.isEmpty());}
            return Optional.of(c.finish(storage.getSlots()>com.zuxelus.energycontrol.port.inventory.InventorySnapshot.LIMIT));
        };
        EnergyControlPort.fluidProbe=(level,pos,side)->{
            if(!level.hasChunkAt(pos))return Optional.empty();
            var storage=level.getCapability(Capabilities.FluidHandler.BLOCK,pos,side);
            if(storage==null)return Optional.empty();
            var values=new java.util.ArrayList<com.zuxelus.energycontrol.port.core.Measurement>();
            for(int i=0;i<Math.min(256,storage.getTanks());i++){
                var fluid=storage.getFluidInTank(i);var id=net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString();
                values.add(com.zuxelus.energycontrol.port.core.Measurement.fluid(id+fluid.getComponentsPatch(),fluid.isEmpty()?"Empty tank":id,fluid.getAmount(),storage.getTankCapacity(i),1000));
            }
            return Optional.of(java.util.List.copyOf(values));
        };
        EnergyControlPort.init((level,pos,side) -> {
            if(!level.hasChunkAt(pos)) return Optional.empty();
            var storage=level.getCapability(Capabilities.EnergyStorage.BLOCK,pos,side);
            return storage==null ? Optional.empty() : Optional.of(new EnergySnapshot(storage.getEnergyStored(),storage.getMaxEnergyStored(),"FE"));
        });
    }
}
