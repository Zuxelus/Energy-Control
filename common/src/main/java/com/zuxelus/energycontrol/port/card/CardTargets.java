// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.card;
import net.minecraft.nbt.*;
import java.util.*;
public final class CardTargets {
 public record Target(String dimension,long position,int side){}
 public static List<Target> read(CompoundTag tag){
  var result=new ArrayList<Target>();
  if(tag.contains("targets",Tag.TAG_LIST)){
   var list=tag.getList("targets",Tag.TAG_COMPOUND);
   for(int i=0;i<Math.min(16,list.size());i++){
    var t=list.getCompound(i);if(!t.contains("dimension")||!t.contains("target"))continue;
    var target=new Target(t.getString("dimension"),t.getLong("target"),Math.clamp(t.getInt("side"),0,5));
    if(result.stream().noneMatch(v->v.dimension.equals(target.dimension)&&v.position==target.position))result.add(target);
   }
  }else if(tag.contains("target")&&tag.contains("dimension"))result.add(new Target(tag.getString("dimension"),tag.getLong("target"),Math.clamp(tag.getInt("side"),0,5)));
  return List.copyOf(result);
 }
 public static boolean bind(CompoundTag tag,Target target,boolean array){
  if(!array){tag.remove("targets");tag.putString("dimension",target.dimension);tag.putLong("target",target.position);tag.putInt("side",target.side);return true;}
  var targets=new ArrayList<>(read(tag));int found=-1;
  for(int i=0;i<targets.size();i++)if(targets.get(i).dimension.equals(target.dimension)&&targets.get(i).position==target.position){found=i;break;}
  if(found>=0){if(targets.get(found).side==target.side)targets.remove(found);else targets.set(found,target);}
  else {if(targets.size()>=16)return false;targets.add(target);}
  var list=new ListTag();for(var t:targets){var n=new CompoundTag();n.putString("dimension",t.dimension);n.putLong("target",t.position);n.putInt("side",t.side);list.add(n);}
  tag.remove("target");tag.remove("dimension");tag.remove("side");tag.put("targets",list);return true;
 }
}
