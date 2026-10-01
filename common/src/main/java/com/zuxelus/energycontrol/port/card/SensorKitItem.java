// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.card;
import com.zuxelus.energycontrol.port.EnergyControlPort;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.item.ItemEntity;
/** Upstream consume-one/drop-bound-card semantics, before container interaction. */
public final class SensorKitItem extends Item {
 public enum Kind { ENERGY, FLUID, INVENTORY, REDSTONE, MACHINE }
 private final Kind kind;
 public SensorKitItem(Kind kind){super(new Properties().stacksTo(16));this.kind=kind;}
 // NeoForge item extension override; harmless additional method on Fabric.
 public InteractionResult onItemUseFirst(ItemStack stack,UseOnContext context){return useOn(context);}
 @Override public InteractionResult useOn(UseOnContext c){
  var p=c.getPlayer();if(p==null||p.isSpectator()||c.getItemInHand().isEmpty())return InteractionResult.PASS;
  if(c.getLevel().isClientSide)return InteractionResult.SUCCESS;
  var level=c.getLevel();var pos=c.getClickedPos();var side=c.getClickedFace();
  if(!level.hasChunkAt(pos))return InteractionResult.PASS;
  boolean supported=switch(kind){
   case ENERGY->EnergyControlPort.energyProbe.read(level,pos,side).isPresent();
   case FLUID->EnergyControlPort.fluidProbe.read(level,pos,side).filter(v->!v.isEmpty()).isPresent();
   case INVENTORY->EnergyControlPort.inventoryProbe.read(level,pos,side).isPresent();
   case REDSTONE->!level.getBlockState(pos).isAir();
   case MACHINE->EnergyControlPort.machineProbe.read(level,pos,side).isPresent();
  };
  if(!supported)return InteractionResult.PASS;
  Item item=switch(kind){case ENERGY->EnergyControlPort.ENERGY.get();case FLUID->EnergyControlPort.FLUID.get();case INVENTORY->EnergyControlPort.INVENTORY.get();case REDSTONE->EnergyControlPort.REDSTONE.get();case MACHINE->EnergyControlPort.MACHINE.get();};
  var card=new ItemStack(item);var data=CardItem.data(card);CardTargets.bind(data,new CardTargets.Target(level.dimension().location().toString(),pos.asLong(),side.get3DDataValue()),false);CardItem.update(card,data);
  c.getItemInHand().shrink(1);var drop=new ItemEntity(level,p.getX(),p.getY(),p.getZ(),card);drop.setPickUpDelay(0);level.addFreshEntity(drop);return InteractionResult.SUCCESS;
 }
}
