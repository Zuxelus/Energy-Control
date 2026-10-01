// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.nbt.CompoundTag;

/** Typed display data: text can never manufacture a numeric bar. */
public record DisplayRow(String text, int fill) {
    public DisplayRow {
        text = text == null ? "" : text.substring(0, Math.min(256, text.length()));
        fill = Math.clamp(fill, -1, 10000);
    }
    public static DisplayRow text(String text) { return new DisplayRow(text, -1); }
    public boolean isBar() { return fill >= 0; }
    public static DisplayRow bar(String label, BigDecimal amount, BigDecimal capacity) {
        int fraction = capacity.signum() <= 0 ? 0 : amount.max(BigDecimal.ZERO).min(capacity)
                .multiply(BigDecimal.valueOf(10000)).divide(capacity, 0, RoundingMode.HALF_UP).intValue();
        return new DisplayRow(label, fraction);
    }
    public CompoundTag save() {
        var tag = new CompoundTag(); tag.putString("text", text); tag.putInt("fill", fill); return tag;
    }
    public static DisplayRow load(CompoundTag tag) {
        return new DisplayRow(tag.getString("text"), tag.contains("fill") ? tag.getInt("fill") : -1);
    }
}
