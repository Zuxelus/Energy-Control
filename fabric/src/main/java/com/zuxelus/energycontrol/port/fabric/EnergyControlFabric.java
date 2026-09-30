// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.fabric;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.core.EnergySnapshot;
import net.fabricmc.api.ModInitializer;
import team.reborn.energy.api.EnergyStorage;
import java.util.Optional;

public final class EnergyControlFabric implements ModInitializer {
    @Override public void onInitialize() {
        EnergyControlPort.init((level,pos,side) -> {
            if(!level.hasChunkAt(pos)) return Optional.empty();
            var storage=EnergyStorage.SIDED.find(level,pos,side);
            return storage==null ? Optional.empty() : Optional.of(new EnergySnapshot(storage.getAmount(),storage.getCapacity(),"E"));
        });
    }
}
