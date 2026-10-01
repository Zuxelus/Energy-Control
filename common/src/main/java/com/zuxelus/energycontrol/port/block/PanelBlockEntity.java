// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.block;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.card.CardItem;
import com.zuxelus.energycontrol.port.core.ScreenLayout;
import com.zuxelus.energycontrol.port.core.TargetPolicy;
import com.zuxelus.energycontrol.port.menu.PanelMenu;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

/** Port of panel ownership, card inventory and rectangular ScreenManager semantics. */
public final class PanelBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
    private ScreenLayout.Bounds bounds = new ScreenLayout.Bounds(0,0,0,0);
    private BlockPos owner;
    private List<String> lines = List.of();
    private int color = 0x55ff55;
    private boolean powered = true;
    private int background = 0x080b0c, scalePercent = 100, alignment, powerMode = 2, refreshTicks = 20;
    private boolean uniformFont;
    public int background() { return background; }
    public int scalePercent() { return scalePercent; }
    public int alignment() { return alignment; }
    public int powerMode() { return powerMode; }
    public int refreshTicks() { return refreshTicks; }
    public boolean uniformFont() { return uniformFont; }
    public PanelBlockEntity(BlockPos pos, BlockState state) { super(EnergyControlPort.PANEL_ENTITY.get(),pos,state); }
    public boolean advanced() { return ((PanelBlock)getBlockState().getBlock()).advanced(); }
    public boolean isExtender() { return ((PanelBlock)getBlockState().getBlock()).extender(); }
    public Direction facing() { return getBlockState().getValue(PanelBlock.FACING); }
    public BlockPos owner() { return owner; }
    public ScreenLayout.Bounds bounds() { return bounds; }
    public List<String> lines() { return lines; }
    public int color() { return color; }
    public boolean powered() { return powered; }
    @Override public int getContainerSize() { return isExtender() ? 0 : advanced() ? 8 : 1; }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override protected Component getDefaultName() { return Component.translatable(advanced() ? "block.energycontrol.info_panel_advanced" : "block.energycontrol.info_panel"); }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) { return new PanelMenu(id,inventory,this); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot < getContainerSize() && stack.getItem() instanceof CardItem; }

    public Direction right() {
        return switch(facing()) { case SOUTH -> Direction.EAST; case NORTH, UP, DOWN -> Direction.WEST; case EAST -> Direction.NORTH; case WEST -> Direction.SOUTH; };
    }
    public Direction up() { return switch(facing()) { case UP -> Direction.SOUTH; case DOWN -> Direction.NORTH; default -> Direction.UP; }; }
    public BlockPos at(int x, int y) { return worldPosition.relative(right(),x).relative(up(),y); }
    public boolean contains(BlockPos pos) {
        var delta = pos.subtract(worldPosition);
        int x = delta.getX()*right().getStepX()+delta.getY()*right().getStepY()+delta.getZ()*right().getStepZ();
        int y = delta.getX()*up().getStepX()+delta.getY()*up().getStepY()+delta.getZ()*up().getStepZ();
        return at(x,y).equals(pos) && x >= bounds.minX() && x <= bounds.maxX() && y >= bounds.minY() && y <= bounds.maxY();
    }
    private boolean validExtender(int x, int y) {
        BlockPos pos = at(x,y);
        if (level == null || !level.hasChunkAt(pos)) return false;
        if (!(level.getBlockEntity(pos) instanceof PanelBlockEntity other) || !other.isExtender()
                || other.advanced() != advanced() || other.facing() != facing()) return false;
        if (other.owner == null || other.owner.equals(worldPosition)) return true;
        // Never steal a part whose owner is in an unloaded chunk.
        if (!level.hasChunkAt(other.owner)) return false;
        return !(level.getBlockEntity(other.owner) instanceof PanelBlockEntity core) || core.isExtender() || !core.contains(pos);
    }
    public void releaseExtenders() {
        if (level == null || isExtender()) return;
        each(bounds, (pos) -> {
            if (level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof PanelBlockEntity other && worldPosition.equals(other.owner)) {
                other.owner = null; other.sync();
            }
        });
    }
    private void each(ScreenLayout.Bounds rectangle, java.util.function.Consumer<BlockPos> action) {
        for(int x=rectangle.minX(); x<=rectangle.maxX(); x++) for(int y=rectangle.minY(); y<=rectangle.maxY(); y++) {
            if(x != 0 || y != 0) action.accept(at(x,y));
        }
    }
    private void regroup() {
        var next = ScreenLayout.grow(this::validExtender);
        if (!bounds.equals(next)) { releaseExtenders(); bounds = next; sync(); }
        each(next, pos -> {
            if (level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof PanelBlockEntity other && !worldPosition.equals(other.owner)) {
                other.owner = worldPosition.immutable(); other.sync();
            }
        });
    }
    public static void tick(Level level, BlockPos pos, BlockState state, PanelBlockEntity panel) {
        if (level.isClientSide || panel.isExtender()) return;
        if (level.getGameTime() % 20 == 0) panel.regroup();
        boolean power = switch(panel.powerMode) {
            case 0 -> level.hasNeighborSignal(pos); case 1 -> !level.hasNeighborSignal(pos); case 3 -> false; default -> true;
        };
        if (panel.powered != power) { panel.powered = power; panel.sync(); }
        if (level.getGameTime() % panel.refreshTicks == 0) panel.refreshCards();
    }
    private void refreshCards() {
        List<String> next = new ArrayList<>();
        for(int slot=0; slot<getContainerSize(); slot++) {
            ItemStack stack = items.get(slot);
            if (!(stack.getItem() instanceof CardItem card)) continue;
            CompoundTag data = CardItem.data(stack);
            String title=data.getString("title");
            if(!title.isBlank()) next.add(title.substring(0,Math.min(128,title.length())));
            switch(card.kind()) {
                case TEXT -> next.addAll(com.zuxelus.energycontrol.port.core.DisplayText.lines(data.getString("text")));
                case TIME -> {
                    long minutes = Math.floorMod(level.getDayTime()+6000,24000)*60/1000;
                    next.add(String.format(Locale.ROOT,(data.getBoolean("hideLabels")?"":"Time: ")+"%02d:%02d",minutes/60,minutes%60));
                }
                case ENERGY, REDSTONE -> readTarget(card.kind(), data, next);
            }
        }
        if (next.size()>32) next = new ArrayList<>(next.subList(0,32));
        if (!next.equals(lines)) { lines = List.copyOf(next); sync(); }
    }
    private void readTarget(CardItem.Kind kind, CompoundTag data, List<String> output) {
        BlockPos target = BlockPos.of(data.getLong("target"));
        var status = TargetPolicy.check(data.contains("target") && data.contains("dimension"),level.dimension().location().toString(),data.getString("dimension"),
                (long)target.getX()-worldPosition.getX(),(long)target.getY()-worldPosition.getY(),(long)target.getZ()-worldPosition.getZ(),()->level.hasChunkAt(target));
        if (status != TargetPolicy.Result.READY) {
            output.add(switch(status) { case UNBOUND -> "Unbound card"; case OUT_OF_RANGE -> "Out of range (64 blocks)"; default -> "Target chunk unloaded"; });
            return;
        }
        if (kind == CardItem.Kind.REDSTONE) { output.add((data.getBoolean("hideLabels")?"":"Redstone: ") + level.getBestNeighborSignal(target)); return; }
        var reading = EnergyControlPort.energyProbe.read(level,target,Direction.from3DDataValue(data.getInt("side")));
        reading.ifPresentOrElse(value -> {
            output.add((data.getBoolean("hideLabels")?"":"Energy: ") + value.stored() + " / " + value.capacity() + " " + value.unit());
            if(!data.getBoolean("hidePercent")) output.add(String.format(Locale.ROOT,(data.getBoolean("hideLabels")?"":"Stored: ")+"%.1f%%",100*value.fraction()));
        }, () -> output.add("No compatible energy storage"));
    }
    public void setText(int slot, String text) {
        if (slot < 0 || slot >= getContainerSize() || text.length() > 512) return;
        ItemStack stack = items.get(slot);
        if (!(stack.getItem() instanceof CardItem card)) return;
        var data = CardItem.data(stack);
        if(card.kind()==CardItem.Kind.TEXT) data.putString("text", text.replace("\r", ""));
        else data.putString("title",text.replace("\r", "").replace("\n", " ").substring(0,Math.min(128,text.replace("\r", "").replace("\n", " ").length())));
        CardItem.update(stack,data);
        setChanged(); refreshCards();
    }
    public void cycleColor() { color = switch(color) { case 0x55ff55 -> 0xffffff; case 0xffffff -> 0xffaa00; default -> 0x55ff55; }; sync(); }
    public void togglePower() { powerMode = (powerMode + 1) % 4; sync(); }
    public boolean configure(int id) {
        if(id>=100 && id<100+getContainerSize()*2) {
            var stack=items.get((id-100)/2);
            if(!(stack.getItem() instanceof CardItem)) return false;
            var data=CardItem.data(stack);String key=id%2==0?"hideLabels":"hidePercent";
            data.putBoolean(key,!data.getBoolean(key));CardItem.update(stack,data);refreshCards();sync();return true;
        }
        switch(id) {
            case 2 -> scalePercent = scalePercent >= 200 ? 50 : scalePercent + 25;
            case 3 -> alignment = (alignment + 1) % 3;
            case 4 -> uniformFont = !uniformFont;
            case 5 -> refreshTicks = switch(refreshTicks) { case 5 -> 10; case 10 -> 20; case 20 -> 40; default -> 5; };
            case 6 -> background = background == 0x080b0c ? 0x303840 : 0x080b0c;
            default -> { return false; }
        }
        sync(); return true;
    }
    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);
        ContainerHelper.saveAllItems(tag,items,registries);
        tag.putIntArray("bounds",new int[]{bounds.minX(),bounds.minY(),bounds.maxX(),bounds.maxY()});
        if(owner != null) tag.putLong("owner",owner.asLong());
        tag.putInt("color",color); tag.putBoolean("powered",powered);
        tag.putInt("background",background); tag.putInt("scalePercent",scalePercent); tag.putInt("alignment",alignment);
        tag.putInt("powerMode",powerMode); tag.putInt("refreshTicks",refreshTicks); tag.putBoolean("uniformFont",uniformFont);
        ListTag list = new ListTag(); lines.forEach(line -> list.add(StringTag.valueOf(line))); tag.put("lines",list);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag,registries);
        items = NonNullList.withSize(8,ItemStack.EMPTY); ContainerHelper.loadAllItems(tag,items,registries);
        int[] b = tag.getIntArray("bounds");
        bounds = b.length == 4 && b[0]>=-20 && b[1]>=-20 && b[2]<=20 && b[3]<=20 && b[0]<=0 && b[1]<=0 && b[2]>=0 && b[3]>=0
                ? new ScreenLayout.Bounds(b[0],b[1],b[2],b[3]) : new ScreenLayout.Bounds(0,0,0,0);
        owner = tag.contains("owner") ? BlockPos.of(tag.getLong("owner")) : null;
        color = tag.contains("color") ? tag.getInt("color") & 0xffffff : 0x55ff55;
        powered = !tag.contains("powered") || tag.getBoolean("powered");
        background = tag.contains("background") ? tag.getInt("background") & 0xffffff : 0x080b0c;
        scalePercent = tag.contains("scalePercent") ? Math.clamp(tag.getInt("scalePercent"),50,200) : 100;
        alignment = Math.clamp(tag.getInt("alignment"),0,2);
        powerMode = tag.contains("powerMode") ? Math.clamp(tag.getInt("powerMode"),0,3) : powered ? 2 : 3;
        refreshTicks = tag.contains("refreshTicks") ? Math.clamp(tag.getInt("refreshTicks"),5,100) : 20;
        uniformFont = tag.getBoolean("uniformFont");
        var list = tag.getList("lines",Tag.TAG_STRING); var result = new ArrayList<String>();
        for(int i=0;i<Math.min(32,list.size());i++) { String s=list.getString(i); result.add(s.substring(0,Math.min(s.length(),256))); }
        lines = List.copyOf(result);
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
