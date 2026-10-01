package com.zuxelus.energycontrol.port.core;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class MeasurementTest {
 @Test void fabricDropletsBecomeMillibucketsWithoutTruncatingSmallVolumes(){
  assertEquals("1000.000",Measurement.fluid("water","Water",81000,162000,81000).amount(3));
  assertEquals("0.012",Measurement.fluid("water","Water",1,81,81000).amount(3));
 }
 @Test void totalsKeepDifferentFluidsAndUnitsSeparate(){
  var totals=Measurement.combine(List.of(Measurement.fluid("water","Water",40500,81000,81000),Measurement.fluid("water","Water",500,1000,1000),Measurement.fluid("lava","Lava",250,1000,1000)));
  assertEquals(2,totals.size());assertEquals("1000",totals.get(0).amount(0));assertEquals("2000",totals.get(0).maximum(0));
 }
 @Test void energyArrayCannotOverflowSignedLong(){
  var m=new Measurement("energy","Energy",BigDecimal.valueOf(Long.MAX_VALUE),BigDecimal.valueOf(Long.MAX_VALUE),"FE");
  assertEquals("18446744073709551614",Measurement.combine(List.of(m,m)).getFirst().amount(0));
 }
 @Test void badProviderAmountsClampAndZeroCapacityDoesNotDivide(){
  assertEquals("0.0",Measurement.fluid("water","Water",-10,-1,1000).percent(1));
  assertEquals("100.0",Measurement.fluid("water","Water",2000,1000,1000).percent(1));
 }
}
