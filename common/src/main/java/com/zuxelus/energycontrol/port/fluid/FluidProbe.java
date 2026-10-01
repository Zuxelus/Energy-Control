// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.fluid;
import com.zuxelus.energycontrol.port.core.Measurement;
import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import java.util.*;
@FunctionalInterface public interface FluidProbe {
 Optional<List<Measurement>> read(Level level,BlockPos pos,Direction side);
}
