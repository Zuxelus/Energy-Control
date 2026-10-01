// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
/** Counts are bounded even when loaded from old or manually edited item data. */
public final class UpgradePolicy {
 private static int count(int n){return Math.clamp(n,0,3);}
 public static int range(int count){return 64<<count(count);}
 public static int targets(int count){return 4*(1+count(count));}
 public static int decimals(int count){return count(count);}
}
