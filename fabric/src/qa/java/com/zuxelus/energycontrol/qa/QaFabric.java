// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.reborn.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
public final class QaFabric implements ModInitializer {
    public void onInitialize() {
        EnergyStorage.SIDED.registerForBlockEntity((be,side)->new EnergyStorage() {
            public long getAmount(){ return be.getLevel().getGameTime()%1000*100; }
            public long getCapacity(){ return 100000; }
            public boolean supportsInsertion(){return false;}
            public boolean supportsExtraction(){return false;}
            public long insert(long amount,TransactionContext tx){return 0;}
            public long extract(long amount,TransactionContext tx){return 0;}
        },BlockEntityType.CHEST);
        QaServer.init();
    }
}
