// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.card;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import java.util.List;
public final class UpgradeItem extends Item {
 public enum Kind { RANGE, CAPACITY, PRECISION }
 private final Kind kind;
 public UpgradeItem(Kind kind){super(new Properties().stacksTo(3));this.kind=kind;}
 public Kind kind(){return kind;}
 @Override public void appendHoverText(ItemStack s,TooltipContext c,List<Component> t,TooltipFlag f){t.add(Component.translatable("tooltip.energycontrol.upgrade."+kind.name().toLowerCase(java.util.Locale.ROOT)));}
}
