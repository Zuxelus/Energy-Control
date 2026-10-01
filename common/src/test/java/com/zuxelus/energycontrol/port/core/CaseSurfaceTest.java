// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class CaseSurfaceTest {
 @Test void flatShapeMatchesPriorBoxOnEveryFace(){for(var f:Direction.values())for(int t=1;t<=16;t++)assertEquals(PanelCase.box(f,t),new CaseSurface(t/16.0,0,0).shape(f).bounds());}
 @Test void mountingAndFrontCoordinatesFollowAllSixFaces(){for(var f:Direction.values()){var back=CaseSurface.point(f,.5,.5,0);var front=CaseSurface.point(f,.5,.5,1);assertEquals(f.getStepX(),front.x-back.x,1e-9);assertEquals(f.getStepY(),front.y-back.y,1e-9);assertEquals(f.getStepZ(),front.z-back.z,1e-9);}}
 @Test void wedgeUsesMultipleBoxesAndStaysInsideBlock(){for(var f:Direction.values()){var s=new CaseSurface(.5,-.5,.5).shape(f);assertTrue(s.toAabbs().size()>1);for(var b:s.toAabbs()){assertTrue(b.minX>=0&&b.minY>=0&&b.minZ>=0);assertTrue(b.maxX<=1&&b.maxY<=1&&b.maxZ<=1);}}}
 @Test void invalidSavedSurfaceCannotProduceNaNGeometry(){var s=new CaseSurface(Double.NaN,0,0);assertEquals(1,s.center());assertEquals(0,s.dx());assertFalse(s.shape(Direction.NORTH).isEmpty());}
}
