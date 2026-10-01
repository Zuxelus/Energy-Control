// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

/** Six-face solid case geometry, anchored at the mounting back of each block. */
public final class PanelCase {
    private PanelCase() {}
    public static int clamp(int thickness) { return Math.clamp(thickness, 1, 16); }
    public static double frontDepth(int thickness) { return clamp(thickness) / 16.0 - .5 + .005; }
    public static AABB box(Direction face, int thickness) {
        double t = clamp(thickness) / 16.0;
        return switch (face) {
            case NORTH -> new AABB(0, 0, 1-t, 1, 1, 1);
            case SOUTH -> new AABB(0, 0, 0, 1, 1, t);
            case EAST -> new AABB(0, 0, 0, t, 1, 1);
            case WEST -> new AABB(1-t, 0, 0, 1, 1, 1);
            case UP -> new AABB(0, 0, 0, 1, t, 1);
            case DOWN -> new AABB(0, 1-t, 0, 1, 1, 1);
        };
    }
}
