// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.fabric;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.core.EnergySnapshot;
import net.fabricmc.api.ModInitializer;
import team.reborn.energy.api.EnergyStorage;
import java.util.Optional;

public final class EnergyControlFabric implements ModInitializer {
    @Override public void onInitialize() {
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,level,hand,hit)->{
            var stack=player.getItemInHand(hand);return stack.getItem() instanceof com.zuxelus.energycontrol.port.card.SensorKitItem kit?kit.useOn(new net.minecraft.world.item.context.UseOnContext(player,hand,hit)):net.minecraft.world.InteractionResult.PASS;
        });
        EnergyControlPort.inventoryProbe=(level,pos,side)->{
            if(level.isClientSide||!level.hasChunkAt(pos))return Optional.empty();
            var storage=net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(level,pos,side);
            if(storage==null)return com.zuxelus.energycontrol.port.inventory.InventoryProbe.vanilla(level,pos,side);
            boolean indexed=storage instanceof net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage<?>;
            int size=indexed?((net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage<?>)storage).getSlotCount():-1;
            var c=new com.zuxelus.energycontrol.port.inventory.InventorySnapshot.Collector(com.zuxelus.energycontrol.port.inventory.InventoryProbe.name(level,pos),level.getBlockEntity(pos) instanceof net.minecraft.world.WorldlyContainer,indexed,size);
            boolean more=false;
            for(var view:storage){var v=view.getResource();if(!c.add(v.isBlank()?"Empty":v.toStack().getHoverName().getString(),view.getAmount(),v.isBlank())){more=true;break;}}
            return Optional.of(c.finish(more));
        };
        EnergyControlPort.fluidProbe=(level,pos,side)->{
            if(!level.hasChunkAt(pos))return Optional.empty();
            var storage=net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.SIDED.find(level,pos,side);
            if(storage==null)return Optional.empty();
            var values=new java.util.ArrayList<com.zuxelus.energycontrol.port.core.Measurement>();
            int scanned=0;
            for(var tank:storage){
                if(scanned++>=256)break;
                var variant=tank.getResource();var id=net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(variant.getFluid()).toString();
                String name=variant.isBlank()?"Empty tank":id;
                values.add(com.zuxelus.energycontrol.port.core.Measurement.fluid(id+variant.getComponents(),name,tank.getAmount(),tank.getCapacity(),net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET));
            }
            return Optional.of(java.util.List.copyOf(values));
        };
        EnergyControlPort.init((level,pos,side) -> {
            if(!level.hasChunkAt(pos)) return Optional.empty();
            var storage=EnergyStorage.SIDED.find(level,pos,side);
            return storage==null ? Optional.empty() : Optional.of(new EnergySnapshot(storage.getAmount(),storage.getCapacity(),"E"));
        });
    }
}
