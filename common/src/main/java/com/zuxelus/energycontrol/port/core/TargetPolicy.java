// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import java.util.function.BooleanSupplier;

/** Bounds validation occurs before touching the target chunk. */
public final class TargetPolicy {
    private TargetPolicy() {}
    public enum Result { READY, UNBOUND, OUT_OF_RANGE, UNLOADED }
    public static Result check(boolean bound,String currentDimension,String targetDimension,long dx,long dy,long dz,BooleanSupplier loaded) {
        if (!bound) return Result.UNBOUND;
        if (!java.util.Objects.equals(currentDimension,targetDimension)) return Result.OUT_OF_RANGE;
        if (dx < -64 || dx > 64 || dy < -64 || dy > 64 || dz < -64 || dz > 64 || dx*dx+dy*dy+dz*dz>4096) return Result.OUT_OF_RANGE;
        return loaded.getAsBoolean() ? Result.READY : Result.UNLOADED;
    }
}
