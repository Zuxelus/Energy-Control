// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ProjectionSettingsTest {
 @Test void oldSaveKeepsOriginalFrontPlane(){var p=ProjectionSettings.load(new CompoundTag());assertEquals(.505,p.frontDepth(),.000001);assertEquals(ProjectionSettings.DEFAULT,p);}
 @Test void saveReloadPreservesSlopedReducedDepth(){var p=new ProjectionSettings(7,-28,35);var tag=new CompoundTag();p.save(tag);assertEquals(p,ProjectionSettings.load(tag));}
 @Test void malformedSaveCannotEscapeBounds(){var tag=new CompoundTag();tag.putInt("projectionDepth",Integer.MIN_VALUE);tag.putInt("projectionPitch",Integer.MAX_VALUE);tag.putInt("projectionYaw",-900);assertEquals(new ProjectionSettings(1,56,-56),ProjectionSettings.load(tag));}
 @Test void controlsCanCycleBackToOriginalWithoutReset(){var p=ProjectionSettings.DEFAULT;for(int i=0;i<17;i++)p=p.nextPitch().nextYaw();for(int i=0;i<16;i++)p=p.nextDepth();assertEquals(ProjectionSettings.DEFAULT,p);}
}
