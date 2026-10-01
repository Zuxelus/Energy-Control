// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import net.minecraft.nbt.CompoundTag;
/** Hologram plane depth in sixteenths, pitch/yaw in degrees, relative to its block face. */
public record ProjectionSettings(int depth,int pitch,int yaw) {
 public static final ProjectionSettings DEFAULT=new ProjectionSettings(16,0,0);
 public ProjectionSettings {depth=Math.clamp(depth,1,16);pitch=Math.clamp(pitch,-56,56);yaw=Math.clamp(yaw,-56,56);}
 public double frontDepth(){return depth/16.0-.5+.005;}
 public static int nextAngle(int value){return value>=56?-56:value+7;}
 public ProjectionSettings nextPitch(){return new ProjectionSettings(depth,nextAngle(pitch),yaw);}
 public ProjectionSettings nextYaw(){return new ProjectionSettings(depth,pitch,nextAngle(yaw));}
 public ProjectionSettings nextDepth(){return new ProjectionSettings(depth==1?16:depth-1,pitch,yaw);}
 public void save(CompoundTag tag){tag.putInt("projectionDepth",depth);tag.putInt("projectionPitch",pitch);tag.putInt("projectionYaw",yaw);}
 public static ProjectionSettings load(CompoundTag tag){return new ProjectionSettings(tag.contains("projectionDepth")?tag.getInt("projectionDepth"):16,tag.getInt("projectionPitch"),tag.getInt("projectionYaw"));}
}
