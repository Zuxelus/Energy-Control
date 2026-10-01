// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import java.util.function.BooleanSupplier;

/** Bounds validation occurs before touching the target chunk. */
public final class TargetPolicy {
    private TargetPolicy() {}
    public enum Result { READY, UNBOUND, OUT_OF_RANGE, UNLOADED }
    public static Result check(boolean bound,String currentDimension,String targetDimension,long dx,long dy,long dz,BooleanSupplier loaded) {
        return check(bound,currentDimension,targetDimension,dx,dy,dz,64,loaded);
    }
    public static Result check(boolean bound,String currentDimension,String targetDimension,long dx,long dy,long dz,int range,BooleanSupplier loaded) {
        if (!bound) return Result.UNBOUND;
        if (!java.util.Objects.equals(currentDimension,targetDimension)) return Result.OUT_OF_RANGE;
        int r=Math.clamp(range,1,512);
        if (dx < -r || dx > r || dy < -r || dy > r || dz < -r || dz > r || dx*dx+dy*dy+dz*dz>(long)r*r) return Result.OUT_OF_RANGE;
        return loaded.getAsBoolean() ? Result.READY : Result.UNLOADED;
    }
}
