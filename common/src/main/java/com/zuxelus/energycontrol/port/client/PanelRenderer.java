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
    @Override public int getViewDistance() { return 96; }
    @Override public void render(PanelBlockEntity panel,float tick,PoseStack pose,MultiBufferSource buffers,int light,int overlay) {
        if(panel.isExtender()) return;
        var bounds=panel.bounds();
        pose.pushPose(); pose.translate(.5,.5,.5);
        switch(panel.facing()) {
            case SOUTH -> pose.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> pose.mulPose(Axis.YP.rotationDegrees(90));
            case EAST -> pose.mulPose(Axis.YP.rotationDegrees(-90));
            case UP -> pose.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN -> pose.mulPose(Axis.XP.rotationDegrees(-90));
            default -> { }
        }
        pose.translate(bounds.minX()-.5,bounds.maxY()+.5,-.502);
        pose.scale(1/128f,-1/128f,-1/128f);
        float width=bounds.width()*128-8, height=bounds.height()*128-8;
        var quad=buffers.getBuffer(RenderType.gui()); var matrix=pose.last().pose();
        quad.addVertex(matrix,4,4,0).setColor(0xff080b0c);
        quad.addVertex(matrix,4,height+4,0).setColor(0xff080b0c);
        quad.addVertex(matrix,width+4,height+4,0).setColor(0xff080b0c);
        quad.addVertex(matrix,width+4,4,0).setColor(0xff080b0c);
        if(panel.powered()) {
            int maxWidth=1; for(String line:panel.lines()) maxWidth=Math.max(maxWidth,font.width(line));
            float scale=Math.min(2f,Math.min((width-12)/maxWidth,(height-12)/Math.max(10,panel.lines().size()*10)));
            pose.translate(10,10,.1); pose.scale(scale,scale,scale);
            int y=0; for(String line:panel.lines()) {
                font.drawInBatch(line,0,y,0xff000000|panel.color(),false,pose.last().pose(),buffers,Font.DisplayMode.NORMAL,0,0xf000f0);
                y+=10;
            }
        }
        pose.popPose();
    }
}
