// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.inventory;
import com.zuxelus.energycontrol.port.core.DisplayRow;
import java.math.BigDecimal;
import java.util.*;
/** Bounded read-only inventory measurement. The first-six preview is not the total. */
public record InventorySnapshot(String name,boolean sided,boolean indexed,int declaredSlots,int scannedSlots,int usedSlots,BigDecimal items,List<String> preview,boolean truncated) {
 public InventorySnapshot {preview=List.copyOf(preview);}
 public static final int LIMIT=4096;
 public static int fields(net.minecraft.nbt.CompoundTag data){return data.contains("inventoryFields")?data.getInt("inventoryFields")&31:31;}
 public List<DisplayRow> rows(int fields,boolean labels,boolean bars){
  var out=new ArrayList<DisplayRow>();
  if((fields&1)!=0)out.add(DisplayRow.text((labels?"Inventory: ":"")+name));
  if((fields&2)!=0)out.add(DisplayRow.text((labels?"Total items: ":"")+items.toPlainString()));
  if((fields&4)!=0)out.add(DisplayRow.text((labels?(indexed?"Slots used: ":"Storage views used: "):"")+usedSlots+" / "+(declaredSlots<0?scannedSlots:declaredSlots)));
  if((fields&8)!=0)out.add(DisplayRow.text((labels?"Sided inventory: ":"")+sided));
  if((fields&16)!=0)for(int i=0;i<preview.size();i++)out.add(DisplayRow.text((indexed?"Slot ":"View ")+(i+1)+": "+preview.get(i)));
  if(bars)out.add(DisplayRow.bar("Occupied "+usedSlots+" / "+scannedSlots,BigDecimal.valueOf(usedSlots),BigDecimal.valueOf(scannedSlots)));
  if(truncated)out.add(DisplayRow.text("Partial inventory: first "+scannedSlots+" visible slots/views only"));
  return List.copyOf(out);
 }
 public static final class Collector {
  private final String name;private final boolean sided,indexed;private final int declared;private int scanned,used;private BigDecimal total=BigDecimal.ZERO;private final List<String> preview=new ArrayList<>();
  public Collector(String name,boolean sided,boolean indexed,int declared){this.name=name;this.sided=sided;this.indexed=indexed;this.declared=declared;}
  public boolean add(String name,long amount,boolean empty){
   if(scanned>=LIMIT)return false;scanned++;long count=empty?0:Math.max(0,amount);if(count>0){used++;total=total.add(BigDecimal.valueOf(count));}
   if(preview.size()<6)preview.add(count==0?"Empty":name+" x"+count);return true;
  }
  public InventorySnapshot finish(boolean more){return new InventorySnapshot(name,sided,indexed,declared,scanned,used,total,preview,more||declared>scanned);}
 }
}
