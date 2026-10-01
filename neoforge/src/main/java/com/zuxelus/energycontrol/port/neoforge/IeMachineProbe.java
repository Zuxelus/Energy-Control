// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.neoforge;

import com.zuxelus.energycontrol.port.core.DisplayRow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/** Read-only IE12.4 adapter. No IE class, source, assets or dependency is bundled. */
public final class IeMachineProbe {
 private IeMachineProbe() {}
 private static final ClassValue<Optional<Method>> AVERAGE = new ClassValue<>() {
  @Override protected Optional<Method> computeValue(Class<?> type) {
   if(!type.getName().equals("blusunrize.immersiveengineering.common.blocks.metal.EnergyMeterBlockEntity"))return Optional.empty();
   try{return Optional.of(type.getMethod("getAveragePower"));}catch(NoSuchMethodException e){return Optional.empty();}
  }
 };
 public static Optional<List<DisplayRow>> read(Level level,BlockPos pos,Direction side) {
  if(level.isClientSide || !level.hasChunkAt(pos))return Optional.empty();
  var be=level.getBlockEntity(pos);if(be==null)return Optional.empty();
  var id=BuiltInRegistries.BLOCK.getKey(be.getBlockState().getBlock());
  if(!id.getNamespace().equals("immersiveengineering"))return Optional.empty();
  var average=AVERAGE.get(be.getClass());
  if(average.isPresent()) {
   try{
    int power=((Number)average.get().invoke(be)).intValue();
    return Optional.of(power<0?List.of(DisplayRow.text("IE meter master unavailable")):
     List.of(DisplayRow.text("IE wire power (20-tick average)"),DisplayRow.text(power+" FE/t")));
   }catch(ReflectiveOperationException | ClassCastException e){return Optional.of(List.of(DisplayRow.text("IE meter API unavailable")));}
  }
  if(id.getPath().equals("thermoelectric_generator")) {
   // This exact spelling is the upstream12.4 persisted public save field.
   var data=be.saveWithoutMetadata(level.registryAccess());
   if(!data.contains("enegyOutput",net.minecraft.nbt.Tag.TAG_INT))return Optional.of(List.of(DisplayRow.text("IE generator data unavailable")));
   int potential=Math.max(0,data.getInt("enegyOutput"));
   return Optional.of(List.of(DisplayRow.text("IE thermoelectric potential"),DisplayRow.text(potential+" FE/t"),DisplayRow.text("Delivered output depends on demand")));
  }
  return Optional.empty();
 }
}
