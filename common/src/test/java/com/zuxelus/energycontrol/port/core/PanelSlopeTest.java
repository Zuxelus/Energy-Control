// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PanelSlopeTest {
 @Test void noSlopeMatchesAllSixteenFlatThicknesses(){for(int t=1;t<=16;t++){var s=new PanelSlope(t,0,0,new ScreenLayout.Bounds(-2,-1,3,2));assertEquals(t/16.0,s.depth(-2.5,-1.5),1e-9);assertEquals(t/16.0,s.depth(3.5,2.5),1e-9);}}
 @Test void fullHorizontalRecessUsesWholeGroupWidth(){var s=new PanelSlope(16,8,0,new ScreenLayout.Bounds(0,0,2,1));assertEquals(1,s.depth(-.5,-.5),1e-9);assertEquals(0,s.depth(2.5,1.5),1e-9);assertEquals(2/3.0,s.depth(.5,0),1e-9);}
 @Test void dualAxesShareRecessAndStayPlanar(){var s=new PanelSlope(8,8,-8,new ScreenLayout.Bounds(0,0,1,1));assertEquals(.5,s.depth(-.5,-.5),1e-9);assertEquals(.25,s.depth(1.5,-.5),1e-9);assertEquals(0,s.depth(1.5,1.5),1e-9);assertEquals(s.depth(.2,.4)+s.dx()*.3+s.dy()*.2,s.depth(.5,.6),1e-9);}
 @Test void everyControlAndThicknessKeepsSurfaceWithinBlockDepth(){for(int h=-8;h<=8;h++)for(int v=-8;v<=8;v++)for(int t=1;t<=16;t++){var s=new PanelSlope(t,h,v,new ScreenLayout.Bounds(0,0,0,0));for(double x:new double[]{-.5,0,.5})for(double y:new double[]{-.5,0,.5}){double d=s.depth(x,y);assertTrue(d>=0&&d<=t/16.0);}}}
 @Test void malformedValuesAreClamped(){var s=new PanelSlope(40,99,-99,new ScreenLayout.Bounds(0,0,0,0));assertEquals(16,s.thickness());assertEquals(8,s.horizontal());assertEquals(-8,s.vertical());assertEquals(-8,PanelSlope.next(8));}
}
