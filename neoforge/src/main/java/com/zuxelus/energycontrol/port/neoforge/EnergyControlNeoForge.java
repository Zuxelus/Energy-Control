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
        EnergyControlPort.init((level,pos,side) -> {
            if(!level.hasChunkAt(pos)) return Optional.empty();
            var storage=level.getCapability(Capabilities.EnergyStorage.BLOCK,pos,side);
            return storage==null ? Optional.empty() : Optional.of(new EnergySnapshot(storage.getEnergyStored(),storage.getMaxEnergyStored(),"FE"));
        });
    }
}
