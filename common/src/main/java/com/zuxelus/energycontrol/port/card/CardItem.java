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
    public enum Kind { TEXT, ENERGY, TIME, REDSTONE, FLUID, ENERGY_ARRAY, FLUID_ARRAY, MACHINE }
    private final Kind kind;
    public CardItem(Kind kind) { super(new Properties().stacksTo(1)); this.kind = kind; }
    public boolean array(){return kind==Kind.ENERGY_ARRAY || kind==Kind.FLUID_ARRAY;}
    public Kind kind() { return kind; }
    public static CompoundTag data(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(); }
    public static void update(ItemStack stack, CompoundTag data) { stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data)); }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (kind == Kind.TEXT || kind == Kind.TIME) return InteractionResult.PASS;
        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) return InteractionResult.PASS;
        if (!context.getLevel().isClientSide) {
            CompoundTag data = data(context.getItemInHand());
            boolean changed=CardTargets.bind(data,new CardTargets.Target(context.getLevel().dimension().location().toString(),context.getClickedPos().asLong(),context.getClickedFace().get3DDataValue()),array());
            if(!changed){context.getPlayer().displayClientMessage(Component.literal("Array full (16 targets). Repeat a bound face to remove it."),true);return InteractionResult.SUCCESS;}
            update(context.getItemInHand(), data);
            context.getPlayer().displayClientMessage(Component.translatable("message.energycontrol.bound", context.getClickedPos().toShortString()), true);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.energycontrol.card." + kind.name().toLowerCase(java.util.Locale.ROOT)));
        var tag = data(stack);
        if(array())tooltip.add(Component.literal(CardTargets.read(tag).size()+" targets; repeat same face to remove; capacity upgrade enables more than 4."));
        if (tag.contains("target")) tooltip.add(Component.literal(tag.getString("dimension") + " " + net.minecraft.core.BlockPos.of(tag.getLong("target")).toShortString()));
    }
}
