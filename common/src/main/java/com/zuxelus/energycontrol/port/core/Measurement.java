// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import java.math.*;
import java.util.*;
/** Exact sums avoid long overflow; fluid variants and units never merge accidentally. */
public record Measurement(String key,String name,BigDecimal stored,BigDecimal capacity,String unit) {
 public Measurement { capacity=capacity.max(BigDecimal.ZERO);stored=stored.max(BigDecimal.ZERO).min(capacity); }
 public static Measurement fluid(String key,String name,long stored,long capacity,long unitsPerBucket){
  if(unitsPerBucket<=0)throw new IllegalArgumentException("Invalid fluid unit");
  var ratio=BigDecimal.valueOf(1000).divide(BigDecimal.valueOf(unitsPerBucket),18,RoundingMode.HALF_UP);
  return new Measurement(key,name,BigDecimal.valueOf(stored).multiply(ratio),BigDecimal.valueOf(capacity).multiply(ratio),"mB");
 }
 private static int places(int n){return Math.clamp(n,0,3);}
 public String amount(int decimals){return stored.setScale(places(decimals),RoundingMode.HALF_UP).toPlainString();}
 public String maximum(int decimals){return capacity.setScale(places(decimals),RoundingMode.HALF_UP).toPlainString();}
 public String percent(int decimals){return (capacity.signum()==0?BigDecimal.ZERO:stored.multiply(BigDecimal.valueOf(100)).divide(capacity,places(decimals),RoundingMode.HALF_UP)).setScale(places(decimals),RoundingMode.HALF_UP).toPlainString();}
 public static List<Measurement> combine(List<Measurement> readings){
  Map<List<String>,Measurement> totals=new LinkedHashMap<>();
  for(var m:readings)totals.merge(List.of(m.key,m.unit),m,(a,b)->new Measurement(a.key,a.name,a.stored.add(b.stored),a.capacity.add(b.capacity),a.unit));
  return List.copyOf(totals.values());
 }
}
