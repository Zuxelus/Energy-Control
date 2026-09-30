package com.zuxelus.energycontrol.port.core;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TargetPolicyTest {
    @Test void acceptsExactlySixtyFourBlocksButNotBeyond() {
        assertEquals(TargetPolicy.Result.READY,TargetPolicy.check(true,"overworld","overworld",64,0,0,()->true));
        assertEquals(TargetPolicy.Result.OUT_OF_RANGE,TargetPolicy.check(true,"overworld","overworld",64,1,0,()->{fail("Out-of-range target queried");return false;}));
    }
    @Test void rejectsOtherDimensionsBeforeQueryingChunks() {
        assertEquals(TargetPolicy.Result.OUT_OF_RANGE,TargetPolicy.check(true,"overworld","nether",0,0,0,()->{fail("Other dimension queried");return false;}));
    }
    @Test void doesNotConfuseUnboundWithOrigin() {
        assertEquals(TargetPolicy.Result.UNBOUND,TargetPolicy.check(false,"overworld","overworld",0,0,0,()->{fail("Unbound target queried");return false;}));
        assertEquals(TargetPolicy.Result.READY,TargetPolicy.check(true,"overworld","overworld",0,0,0,()->true));
    }
    @Test void unloadedChunkIsExplicitlyUnavailable() {
        assertEquals(TargetPolicy.Result.UNLOADED,TargetPolicy.check(true,"overworld","overworld",1,2,3,()->false));
    }
    @Test void extremeCoordinatesCannotOverflowIntoRange() {
        assertEquals(TargetPolicy.Result.OUT_OF_RANGE,TargetPolicy.check(true,"overworld","overworld",Long.MAX_VALUE,0,0,()->{fail("Overflow allowed lookup");return false;}));
        assertEquals(TargetPolicy.Result.OUT_OF_RANGE,TargetPolicy.check(true,"overworld","overworld",Long.MIN_VALUE,0,0,()->true));
    }
}
