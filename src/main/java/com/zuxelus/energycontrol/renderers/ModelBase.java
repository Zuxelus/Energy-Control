package com.zuxelus.energycontrol.renderers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.mojang.realmsclient.util.Pair;

import ic2.core.model.ModelUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IModel;

public abstract class ModelBase implements IModel {
    protected List<ResourceLocation> textures;

    public ModelBase(List<ResourceLocation> textures) {
        this.textures = textures;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return textures;
    }
} 