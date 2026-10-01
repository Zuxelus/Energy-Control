// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.card;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.core.*;
import net.minecraft.core.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.math.BigDecimal;
import java.util.*;

/** Shared server-only evaluation. Numeric bars come from measurements, not parsed strings. */
public final class CardDisplay {
 public static List<String> read(Level level, BlockPos origin, ItemStack stack, int range, int maxTargets, int decimals) {
  return rows(level, origin, stack, range, maxTargets, decimals).stream().map(DisplayRow::text).toList();
 }
 public static List<DisplayRow> rows(Level level, BlockPos origin, ItemStack stack, int range, int maxTargets, int decimals) {
  var output=new ArrayList<DisplayRow>(); if(!(stack.getItem() instanceof CardItem card))return output;
  var data=CardItem.data(stack);String title=data.getString("title");if(!title.isBlank())output.add(DisplayRow.text(title.substring(0,Math.min(128,title.length()))));
  boolean labels=!data.getBoolean("hideLabels");
  if(card.kind()==CardItem.Kind.TEXT){output.addAll(DisplayText.lines(data.getString("text")).stream().map(DisplayRow::text).toList());return output;}
  if(card.kind()==CardItem.Kind.TIME){long m=Math.floorMod(level.getDayTime()+6000,24000)*60/1000;output.add(DisplayRow.text(String.format(Locale.ROOT,(labels?"Time: ":"")+"%02d:%02d",m/60,m%60)));return output;}
  var targets=CardTargets.read(data);if(targets.isEmpty()){output.add(DisplayRow.text("Unbound card"));return output;}
  var readings=new ArrayList<Measurement>();int active=Math.min(targets.size(),card.array()?maxTargets:1);
  for(int i=0;i<active;i++){
   var t=targets.get(i);var pos=BlockPos.of(t.position());
   var status=TargetPolicy.check(true,level.dimension().location().toString(),t.dimension(),(long)pos.getX()-origin.getX(),(long)pos.getY()-origin.getY(),(long)pos.getZ()-origin.getZ(),range,()->level.hasChunkAt(pos));
   String prefix=card.array()?"#"+(i+1)+" ":"";
   if(status!=TargetPolicy.Result.READY){output.add(DisplayRow.text(prefix+(status==TargetPolicy.Result.OUT_OF_RANGE?"Out of range ("+range+" blocks / dimension)":"Target chunk unloaded")));continue;}
   if(card.kind()==CardItem.Kind.REDSTONE){output.add(DisplayRow.text((labels?"Redstone: ":"")+level.getBestNeighborSignal(pos)));continue;}
   var side=Direction.from3DDataValue(t.side());
   if(card.kind()==CardItem.Kind.MACHINE){
    output.addAll(EnergyControlPort.machineProbe.read(level,pos,side).orElse(List.of(DisplayRow.text("No supported machine data"))));continue;
   }
   List<Measurement> values;
   if(card.kind()==CardItem.Kind.FLUID||card.kind()==CardItem.Kind.FLUID_ARRAY){
    var found=EnergyControlPort.fluidProbe.read(level,pos,side);if(found.isEmpty()){output.add(DisplayRow.text(prefix+"No compatible fluid storage"));continue;}values=found.get();
    if(values.isEmpty())output.add(DisplayRow.text(prefix+"No visible tanks"));
   }else{
    var found=EnergyControlPort.energyProbe.read(level,pos,side);if(found.isEmpty()){output.add(DisplayRow.text(prefix+"No compatible energy storage"));continue;}
    var e=found.get();values=List.of(new Measurement("energy","Energy",BigDecimal.valueOf(e.stored()),BigDecimal.valueOf(e.capacity()),e.unit()));
   }
   readings.addAll(values);
   if(card.array()&&data.getBoolean("showEach"))for(var m:values){
    output.add(DisplayRow.text(prefix+m.name()+": "+m.amount(decimals)+" / "+m.maximum(decimals)+" "+m.unit()));
    if(data.getBoolean("showBars"))output.add(bar(m,prefix,decimals));
   }
  }
  for(var m:Measurement.combine(readings)){
   output.add(DisplayRow.text((labels?(card.array()?"Total ":"")+m.name()+": ":"")+m.amount(decimals)+" / "+m.maximum(decimals)+" "+m.unit()));
   if(!data.getBoolean("hidePercent"))output.add(DisplayRow.text((labels?"Stored: ":"")+m.percent(Math.max(1,decimals))+"%"));
   if(data.getBoolean("showBars"))output.add(bar(m,labels?(card.array()?"Total ":""):"",decimals));
  }
  if(targets.size()>active)output.add(DisplayRow.text((targets.size()-active)+" inactive targets (capacity upgrade)"));
  return output;
 }
 private static DisplayRow bar(Measurement m,String prefix,int decimals){
  return DisplayRow.bar(prefix+m.name()+" "+m.percent(Math.max(1,decimals))+"%",m.stored(),m.capacity());
 }
 public static void edit(ItemStack stack,String text){
  if(!(stack.getItem() instanceof CardItem card)||text.length()>512)return;
  var data=CardItem.data(stack);text=text.replace("\r","");
  if(card.kind()==CardItem.Kind.TEXT)data.putString("text",text);
  else {text=text.replace("\n"," ");data.putString("title",text.substring(0,Math.min(128,text.length())));}
  CardItem.update(stack,data);
 }
}
