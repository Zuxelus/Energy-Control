// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.energy;

import com.zuxelus.energycontrol.port.core.EnergySnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import java.util.Optional;

/** Read-only lookup. Empty means unsupported; never report a missing capability as zero. */
@FunctionalInterface
public interface EnergyProbe {
    Optional<EnergySnapshot> read(Level level, BlockPos pos, Direction side);
}
