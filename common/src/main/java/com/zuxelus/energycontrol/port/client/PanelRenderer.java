// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.port.block.PanelBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.*;

/** Renders a single coherent surface over the core and its owned rectangle. */
public final class PanelRenderer implements BlockEntityRenderer<PanelBlockEntity> {
    private final Font font;
    public PanelRenderer(BlockEntityRendererProvider.Context context) { font=context.getFont(); }
    @Override public boolean shouldRenderOffScreen(PanelBlockEntity panel) { return !panel.isExtender(); }
    private static void fill(MultiBufferSource buffers,PoseStack pose,float left,float top,float right,float bottom,float depth,int color) {
        var quad=buffers.getBuffer(RenderType.textBackground());var matrix=pose.last().pose();
        quad.addVertex(matrix,left,top,depth).setColor(color).setLight(0xf000f0);
        quad.addVertex(matrix,left,bottom,depth).setColor(color).setLight(0xf000f0);
        quad.addVertex(matrix,right,bottom,depth).setColor(color).setLight(0xf000f0);
        quad.addVertex(matrix,right,top,depth).setColor(color).setLight(0xf000f0);
    }
    private static void renderCase(PanelBlockEntity panel,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var surface=panel.caseSurface();var facing=panel.facing();
        var points=new net.minecraft.world.phys.Vec3[8];
        double[][] uv={{0,0},{1,0},{1,1},{0,1}};
        for(int i=0;i<4;i++){points[i]=com.zuxelus.energycontrol.port.core.CaseSurface.point(facing,uv[i][0],uv[i][1],surface.depth(uv[i][0],uv[i][1]));points[i+4]=com.zuxelus.energycontrol.port.core.CaseSurface.point(facing,uv[i][0],uv[i][1],0);}
        // Local right/up/front basis is right-handed for all six orientations.
        int[][] faces={{0,1,2,3},{7,6,5,4},{4,5,1,0},{1,5,6,2},{3,2,6,7},{4,0,3,7}};
        for(int f=0;f<faces.length;f++){
            String texture=f==0?(panel.isExtender()?"extender_advanced_face":"panel_advanced_face"):(panel.isExtender()?"extender_advanced_all":"panel_advanced_all");
            var consumer=buffers.getBuffer(RenderType.entitySolid(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("energycontrol","textures/block/info_panel/"+texture+".png")));
            var ids=faces[f];var normal=points[ids[1]].subtract(points[ids[0]]).cross(points[ids[2]].subtract(points[ids[0]])).normalize();
            for(int i=0;i<4;i++){var p=points[ids[i]];consumer.addVertex(pose.last().pose(),(float)p.x,(float)p.y,(float)p.z).setColor(255,255,255,255).setUv((float)uv[i][0],1-(float)uv[i][1]).setOverlay(overlay).setLight(light).setNormal(pose.last(),(float)normal.x,(float)normal.y,(float)normal.z);}
        }
    }
    @Override public int getViewDistance() { return 96; }
    @Override public void render(PanelBlockEntity panel,float tick,PoseStack pose,MultiBufferSource buffers,int light,int overlay) {
        if(panel.advanced()&&!panel.holographic()&&panel.getBlockState().getValue(com.zuxelus.energycontrol.port.block.PanelBlock.SLOPED))renderCase(panel,pose,buffers,light,overlay);
        if(panel.isExtender()) return;
        var bounds=panel.bounds();
        pose.pushPose(); pose.translate(.5,.5,.5);
        switch(panel.facing()) {
            case NORTH -> pose.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> pose.mulPose(Axis.YP.rotationDegrees(90));
            case WEST -> pose.mulPose(Axis.YP.rotationDegrees(-90));
            case UP -> pose.mulPose(Axis.XP.rotationDegrees(-90));
            case DOWN -> pose.mulPose(Axis.XP.rotationDegrees(90));
            default -> { }
        }
        if(panel.facing().getAxis()==net.minecraft.core.Direction.Axis.Y) pose.mulPose(Axis.ZP.rotationDegrees(180));
        var projection=panel.holographic()?panel.projection():com.zuxelus.energycontrol.port.core.ProjectionSettings.DEFAULT;
        double cx=(bounds.minX()+bounds.maxX())/2.0,cy=(bounds.minY()+bounds.maxY())/2.0;
        var slope=panel.slope();
        pose.translate(cx,cy,panel.holographic()?projection.frontDepth():slope.depth(cx,cy)-.5+.005);
        if(!panel.holographic()&&panel.advanced())pose.mulPose(new org.joml.Matrix4f().m02((float)slope.dx()).m12((float)slope.dy()));
        pose.mulPose(Axis.YP.rotationDegrees(projection.yaw()));pose.mulPose(Axis.XP.rotationDegrees(projection.pitch()));
        pose.translate(-bounds.width()/2.0,bounds.height()/2.0,0);
        pose.scale(1/128f,-1/128f,1/128f);
        float width=bounds.width()*128-8, height=bounds.height()*128-8;
        if(!panel.holographic()){
        var quad=buffers.getBuffer(RenderType.textBackground()); var matrix=pose.last().pose();
        quad.addVertex(matrix,4,4,0).setColor(0xff000000|panel.background()).setLight(0xf000f0);
        quad.addVertex(matrix,4,height+4,0).setColor(0xff000000|panel.background()).setLight(0xf000f0);
        quad.addVertex(matrix,width+4,height+4,0).setColor(0xff000000|panel.background()).setLight(0xf000f0);
        quad.addVertex(matrix,width+4,4,0).setColor(0xff000000|panel.background()).setLight(0xf000f0);
        }
        if(panel.powered()) {
            var style=net.minecraft.network.chat.Style.EMPTY.withFont(net.minecraft.resources.ResourceLocation.withDefaultNamespace(panel.uniformFont()?"uniform":"default"));
            var rendered=panel.lines().stream().map(line->net.minecraft.network.chat.Component.literal(line).withStyle(style)).toList();
            int maxWidth=1; for(var line:rendered) maxWidth=Math.max(maxWidth,font.width(line));
            float scale=Math.min(2f*panel.scalePercent()/100f,Math.min((width-12)/maxWidth,(height-12)/Math.max(10,rendered.size()*10)));
            pose.translate(10,10,.5); pose.scale(scale,scale,scale);
            int y=0; int rowIndex=0; for(var line:rendered) {
                var row=panel.rows().get(rowIndex++);
                if(row.isBar()) {
                    float barWidth=(width-12)/scale;
                    fill(buffers,pose,0,y,barWidth,y+9,-.02f,0xff283c48);
                    if(row.fill()>0)fill(buffers,pose,0,y,barWidth*row.fill()/10000f,y+9,-.01f,0xff207c48);
                }
                float x=panel.alignment()*(Math.max(0,(width-12)/scale-font.width(line)))/2;
                font.drawInBatch(line,x,y,0xff000000|panel.color(),false,pose.last().pose(),buffers,Font.DisplayMode.POLYGON_OFFSET,0,0xf000f0);
                y+=10;
            }
        }
        pose.popPose();
    }
}
