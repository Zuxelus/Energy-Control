// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;

/** Loader-independent port of Zuxelus' ScreenManager rectangular growth contract. */
public final class ScreenLayout {
    private ScreenLayout() {}
    public record Cell(int x, int y) {}
    public record Bounds(int minX, int minY, int maxX, int maxY) {
        public int width() { return maxX - minX + 1; }
        public int height() { return maxY - minY + 1; }
    }
    @FunctionalInterface public interface Extender { boolean matches(int x, int y); }
    public static Bounds grow(Extender extender) {
        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        while (minX > -20 && extender.matches(minX - 1, 0)) minX--;
        while (maxX < 20 && extender.matches(maxX + 1, 0)) maxX++;
        while (minY > -20 && completeRow(extender, minX, maxX, minY - 1)) minY--;
        while (maxY < 20 && completeRow(extender, minX, maxX, maxY + 1)) maxY++;
        return new Bounds(minX, minY, maxX, maxY);
    }
    private static boolean completeRow(Extender extender, int minX, int maxX, int y) {
        for (int x = minX; x <= maxX; x++) if (!extender.matches(x, y)) return false;
        return true;
    }
}
