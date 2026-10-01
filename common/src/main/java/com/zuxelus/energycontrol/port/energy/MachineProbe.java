// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.energy;

import com.zuxelus.energycontrol.port.core.DisplayRow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Optional;

/** Optional, read-only specialized machine data, separate from storage capacity. */
@FunctionalInterface
public interface MachineProbe {
    Optional<List<DisplayRow>> read(Level level, BlockPos pos, Direction side);
}
