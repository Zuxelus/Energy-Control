// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.card;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import java.util.List;

/** Component-based replacement for the old mutable ItemStack tag contract. */
public final class CardItem extends Item {
    public enum Kind { TEXT, ENERGY, TIME, REDSTONE }
    private final Kind kind;
    public CardItem(Kind kind) { super(new Properties().stacksTo(1)); this.kind = kind; }
    public Kind kind() { return kind; }
    public static CompoundTag data(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(); }
    public static void update(ItemStack stack, CompoundTag data) { stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data)); }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (kind != Kind.ENERGY && kind != Kind.REDSTONE) return InteractionResult.PASS;
        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) return InteractionResult.PASS;
        if (!context.getLevel().isClientSide) {
            CompoundTag data = data(context.getItemInHand());
            data.putLong("target", context.getClickedPos().asLong());
            data.putString("dimension", context.getLevel().dimension().location().toString());
            data.putInt("side", context.getClickedFace().get3DDataValue());
            update(context.getItemInHand(), data);
            context.getPlayer().displayClientMessage(Component.translatable("message.energycontrol.bound", context.getClickedPos().toShortString()), true);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.energycontrol.card." + kind.name().toLowerCase(java.util.Locale.ROOT)));
        var tag = data(stack);
        if (tag.contains("target")) tooltip.add(Component.literal(tag.getString("dimension") + " " + net.minecraft.core.BlockPos.of(tag.getLong("target")).toShortString()));
    }
}
