// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;

/** Exact read-only energy measurement; units are not implicitly converted. */
public record EnergySnapshot(long stored, long capacity, String unit) {
    public EnergySnapshot {
        if (stored < 0 || capacity < 0) throw new IllegalArgumentException("Negative energy measurement");
        if (unit == null || unit.isBlank()) throw new IllegalArgumentException("Missing energy unit");
    }
    public double fraction() { return capacity == 0 ? 0 : Math.min(1, (double) stored / capacity); }
}
