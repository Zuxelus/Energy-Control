// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
/** Upstream RotationOffset's grouped wedge, expressed in normalized front depth.
 * Horizontal/vertical controls are -8..8 (GUI labels multiply by7). Both axes
 * share the available recess when used together. The mounting back stays fixed.
 */
public record PanelSlope(int thickness,int horizontal,int vertical,ScreenLayout.Bounds bounds) {
 public PanelSlope {thickness=PanelCase.clamp(thickness);horizontal=Math.clamp(horizontal,-8,8);vertical=Math.clamp(vertical,-8,8);}
 public double depth(double x,double y){
  double u=(x-bounds.minX()+.5)/bounds.width(),v=(y-bounds.minY()+.5)/bounds.height();
  double h=Math.abs(horizontal)/8.0*(horizontal<0?1-u:u)*(vertical==0?1:.5);
  double t=Math.abs(vertical)/8.0*(vertical<0?v:1-v)*(horizontal==0?1:.5);
  return Math.clamp(thickness/16.0*(1-h-t),0,thickness/16.0);
 }
 public double dx(){return -horizontal/8.0*(vertical==0?1:.5)*thickness/16.0/bounds.width();}
 public double dy(){return vertical/8.0*(horizontal==0?1:.5)*thickness/16.0/bounds.height();}
 public boolean sloped(){return horizontal!=0||vertical!=0;}
 public static int next(int v){return v>=8?-8:v+1;}
}
