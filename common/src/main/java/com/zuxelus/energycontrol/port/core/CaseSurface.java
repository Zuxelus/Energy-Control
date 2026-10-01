// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
/** Local planar front with fixed mounting back. Shapes conservatively approximate
 * the plane with at most16 strips per sloping axis; the rendered plane is exact. */
public record CaseSurface(double center,double dx,double dy) {
 public CaseSurface {if(!Double.isFinite(center)||!Double.isFinite(dx)||!Double.isFinite(dy)){center=1;dx=dy=0;}center=Math.clamp(center,0,1);dx=Math.clamp(dx,-1,1);dy=Math.clamp(dy,-1,1);}
 public double depth(double u,double v){return Math.clamp(center+dx*(u-.5)+dy*(v-.5),0,1);}
 public static Vec3 point(Direction face,double u,double v,double d){
  Direction right=switch(face){case SOUTH->Direction.EAST;case NORTH,UP,DOWN->Direction.WEST;case EAST->Direction.NORTH;case WEST->Direction.SOUTH;};
  Direction up=switch(face){case UP->Direction.SOUTH;case DOWN->Direction.NORTH;default->Direction.UP;};
  return new Vec3(.5+right.getStepX()*(u-.5)+up.getStepX()*(v-.5)+face.getStepX()*(d-.5),.5+right.getStepY()*(u-.5)+up.getStepY()*(v-.5)+face.getStepY()*(d-.5),.5+right.getStepZ()*(u-.5)+up.getStepZ()*(v-.5)+face.getStepZ()*(d-.5));
 }
 public VoxelShape shape(Direction face){
  int nx=Math.abs(dx)<1e-10?1:16,ny=Math.abs(dy)<1e-10?1:16;VoxelShape result=Shapes.empty();
  for(int x=0;x<nx;x++)for(int y=0;y<ny;y++){
   double u=x/(double)nx,v=y/(double)ny,U=(x+1)/(double)nx,V=(y+1)/(double)ny;
   double front=Math.max(Math.max(depth(u,v),depth(U,v)),Math.max(depth(u,V),depth(U,V)));
   if(front<1e-10)continue;
   var a=point(face,u,v,0);var b=point(face,U,V,front);
   result=Shapes.joinUnoptimized(result,Shapes.create(new AABB(a,b)),BooleanOp.OR);
  }return result.optimize();
 }
}
