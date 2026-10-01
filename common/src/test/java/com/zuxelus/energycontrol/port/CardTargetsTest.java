package com.zuxelus.energycontrol.port;
import com.zuxelus.energycontrol.port.card.CardTargets;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class CardTargetsTest {
 @Test void repeatedFaceTogglesButAnotherFaceUpdatesWithoutDoubleCounting(){
  var tag=new CompoundTag();var a=new CardTargets.Target("minecraft:overworld",123,2);
  assertTrue(CardTargets.bind(tag,a,true));assertEquals(1,CardTargets.read(tag).size());
  CardTargets.bind(tag,new CardTargets.Target(a.dimension(),123,3),true);
  assertEquals(1,CardTargets.read(tag).size());assertEquals(3,CardTargets.read(tag).getFirst().side());
  CardTargets.bind(tag,new CardTargets.Target(a.dimension(),123,3),true);assertTrue(CardTargets.read(tag).isEmpty());
 }
 @Test void bindingCapacityCannotDiscardPriorTargets(){
  var tag=new CompoundTag();for(int i=0;i<16;i++)assertTrue(CardTargets.bind(tag,new CardTargets.Target("a",i,2),true));
  assertFalse(CardTargets.bind(tag,new CardTargets.Target("a",17,2),true));assertEquals(16,CardTargets.read(tag).size());
 }
 @Test void legacySingleTargetStillReadsAndRebindingDoesNotKeepOldArray(){
  var tag=new CompoundTag();tag.putString("dimension","a");tag.putLong("target",5);tag.putInt("side",2);
  assertEquals(5,CardTargets.read(tag).getFirst().position());
  CardTargets.bind(tag,new CardTargets.Target("b",10,3),false);assertEquals(1,CardTargets.read(tag).size());assertEquals("b",CardTargets.read(tag).getFirst().dimension());
 }
}
