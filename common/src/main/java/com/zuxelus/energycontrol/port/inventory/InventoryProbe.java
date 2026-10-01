// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.inventory;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import java.util.*;
@FunctionalInterface public interface InventoryProbe {
 Optional<InventorySnapshot> read(Level level,BlockPos pos,Direction side);
 static String name(Level level,BlockPos pos){var be=level.getBlockEntity(pos);return be instanceof BaseContainerBlockEntity container?container.getDisplayName().getString():level.getBlockState(pos).getBlock().getName().getString();}
 static Optional<InventorySnapshot> vanilla(Level level,BlockPos pos,Direction side){
  if(level.isClientSide||!level.hasChunkAt(pos))return Optional.empty();
  if(!(level.getBlockEntity(pos) instanceof Container inv))return Optional.empty();
  int[] slots=inv instanceof WorldlyContainer sided?sided.getSlotsForFace(side):java.util.stream.IntStream.range(0,inv.getContainerSize()).toArray();
  var c=new InventorySnapshot.Collector(name(level,pos),inv instanceof WorldlyContainer,true,slots.length);
  for(int i=0;i<Math.min(slots.length,InventorySnapshot.LIMIT);i++){if(slots[i]<0||slots[i]>=inv.getContainerSize())continue;var s=inv.getItem(slots[i]);c.add(s.getHoverName().getString(),s.getCount(),s.isEmpty());}
  return Optional.of(c.finish(slots.length>InventorySnapshot.LIMIT));
 }
}
