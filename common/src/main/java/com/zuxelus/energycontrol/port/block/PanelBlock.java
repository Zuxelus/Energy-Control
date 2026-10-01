// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.block;

import com.mojang.serialization.MapCodec;
import com.zuxelus.energycontrol.port.EnergyControlPort;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;

public final class PanelBlock extends BaseEntityBlock {
    public static final IntegerProperty THICKNESS = IntegerProperty.create("thickness", 1, 16);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private final boolean advanced, extender, holographic;
    public PanelBlock(boolean advanced, boolean extender) {this(advanced,extender,false);}
    public PanelBlock(boolean advanced,boolean extender,boolean holographic) {
        super(holographic?Properties.of().strength(1.0F,3.0F).sound(SoundType.METAL).noOcclusion().noCollission():Properties.of().strength(1.0F,3.0F).sound(SoundType.METAL).noOcclusion());
        this.advanced=advanced;this.extender=extender;this.holographic=holographic;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(THICKNESS, 16));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return MapCodec.unit(this); }
    public boolean holographic(){return holographic;}
    public boolean advanced() { return advanced; }
    public boolean extender() { return extender; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(FACING, THICKNESS); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()); }
    @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter world, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        if (!advanced || holographic) return super.getShape(state, world, pos, context);
        return net.minecraft.world.phys.shapes.Shapes.create(com.zuxelus.energycontrol.port.core.PanelCase.box(state.getValue(FACING), state.getValue(THICKNESS)));
    }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new PanelBlockEntity(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, EnergyControlPort.PANEL_ENTITY.get(), PanelBlockEntity::tick);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof PanelBlockEntity panel)) return InteractionResult.PASS;
        if (panel.isExtender()) {
            if (panel.owner() == null || !level.hasChunkAt(panel.owner()) || !(level.getBlockEntity(panel.owner()) instanceof PanelBlockEntity core)) return InteractionResult.PASS;
            panel = core;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            BlockPos corePos = panel.getBlockPos();
            MenuRegistry.openExtendedMenu(serverPlayer, panel, buffer -> buffer.writeBlockPos(corePos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof PanelBlockEntity panel) {
            if (!level.isClientSide) { panel.releaseExtenders(); Containers.dropContents(level,pos,panel); }
        }
        super.onRemove(state,level,pos,next,moving);
    }
}
