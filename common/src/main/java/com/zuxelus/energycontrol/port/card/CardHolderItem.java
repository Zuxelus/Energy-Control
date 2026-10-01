// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.card;
import com.zuxelus.energycontrol.port.menu.CardHolderMenu;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
public final class CardHolderItem extends Item {
 public CardHolderItem(){super(new Properties().stacksTo(1));}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
  var stack=player.getItemInHand(hand);if(player.isShiftKeyDown())return InteractionResultHolder.pass(stack);
  if(player instanceof ServerPlayer sp&&!player.isSpectator())MenuRegistry.openExtendedMenu(sp,new SimpleMenuProvider((id,inv,p)->new CardHolderMenu(id,inv,hand),Component.translatable("item.energycontrol.card_holder")),b->b.writeEnum(hand));
  return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
 }
}
