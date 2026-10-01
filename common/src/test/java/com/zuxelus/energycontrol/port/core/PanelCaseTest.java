// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PanelCaseTest {
 @Test void sixFacesUseCorrectBackAnchor(){for(var face:Direction.values()){var b=PanelCase.box(face,4);assertEquals(.25,b.max(face.getAxis())-b.min(face.getAxis()),1e-9);if(face.getAxisDirection()==Direction.AxisDirection.POSITIVE)assertEquals(0,b.min(face.getAxis()),1e-9);else assertEquals(1,b.max(face.getAxis()),1e-9);}}
 @Test void everyThicknessMatchesVisibleFrontPlane(){for(int t=1;t<=16;t++){assertEquals(t/16.0,PanelCase.frontDepth(t)+.5-.005,1e-9);for(var face:Direction.values())assertEquals(t/16.0,PanelCase.box(face,t).max(face.getAxis())-PanelCase.box(face,t).min(face.getAxis()),1e-9);}}
 @Test void malformedValuesStayInsideBlock(){assertEquals(1,PanelCase.clamp(-900));assertEquals(16,PanelCase.clamp(900));assertEquals(.505,PanelCase.frontDepth(16),1e-9);}
}
