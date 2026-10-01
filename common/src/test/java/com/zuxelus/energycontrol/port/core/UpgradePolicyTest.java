package com.zuxelus.energycontrol.port.core;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class UpgradePolicyTest {
 @Test void distanceUpgradeEnablesDistantTargetBeforeChunkLookup(){
  assertEquals(TargetPolicy.Result.OUT_OF_RANGE,TargetPolicy.check(true,"a","a",100,0,0,UpgradePolicy.range(0),()->{throw new AssertionError("must not load");}));
  assertEquals(TargetPolicy.Result.READY,TargetPolicy.check(true,"a","a",100,0,0,UpgradePolicy.range(1),()->true));
 }
 @Test void capacityAndPrecisionAreBoundedEvenForMalformedCounts(){
  assertEquals(4,UpgradePolicy.targets(-1));assertEquals(16,UpgradePolicy.targets(99));
  assertEquals(0,UpgradePolicy.decimals(-1));assertEquals(3,UpgradePolicy.decimals(99));
  assertEquals(512,UpgradePolicy.range(99));
 }
}
