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
    private NonNullList<ItemStack> items = NonNullList.withSize(11, ItemStack.EMPTY);
    private ScreenLayout.Bounds bounds = new ScreenLayout.Bounds(0,0,0,0);
    private BlockPos owner;
    private List<com.zuxelus.energycontrol.port.core.DisplayRow> rows = List.of();
    private int color = 0x55ff55;
    private boolean powered = true;
    private int background = 0x080b0c, scalePercent = 100, alignment, powerMode = 2, refreshTicks = 20;
    private boolean uniformFont;
    private int page,pageSize=32,pageCount=1,pageTicks;
    private com.zuxelus.energycontrol.port.core.ProjectionSettings projection=com.zuxelus.energycontrol.port.core.ProjectionSettings.DEFAULT;
    public int page(){return page;} public int pageSize(){return pageSize;} public int pageCount(){return pageCount;} public int pageTicks(){return pageTicks;}
    public com.zuxelus.energycontrol.port.core.ProjectionSettings projection(){return projection;}
    public int background() { return background; }
    public int scalePercent() { return scalePercent; }
    public int alignment() { return alignment; }
    public int powerMode() { return powerMode; }
    public int refreshTicks() { return refreshTicks; }
    public boolean uniformFont() { return uniformFont; }
    public PanelBlockEntity(BlockPos pos, BlockState state) { super(EnergyControlPort.PANEL_ENTITY.get(),pos,state); }
    public boolean holographic(){return ((PanelBlock)getBlockState().getBlock()).holographic();}
    public boolean advanced() { return ((PanelBlock)getBlockState().getBlock()).advanced(); }
    public boolean isExtender() { return ((PanelBlock)getBlockState().getBlock()).extender(); }
    public Direction facing() { return getBlockState().getValue(PanelBlock.FACING); }
    public BlockPos owner() { return owner; }
    public ScreenLayout.Bounds bounds() { return bounds; }
    public List<String> lines() { return rows.stream().map(com.zuxelus.energycontrol.port.core.DisplayRow::text).toList(); }
    public List<com.zuxelus.energycontrol.port.core.DisplayRow> rows() { return rows; }
    public int thickness() { return advanced() && !holographic() ? getBlockState().getValue(PanelBlock.THICKNESS) : 16; }
    public int color() { return color; }
    public boolean powered() { return powered; }
    public int cardSlots(){return isExtender()?0:advanced()?8:1;}
    @Override public int getContainerSize(){return isExtender()?0:cardSlots()+3;}
    public int upgrades(com.zuxelus.energycontrol.port.card.UpgradeItem.Kind kind){var s=items.get(cardSlots()+kind.ordinal());return s.getItem() instanceof com.zuxelus.energycontrol.port.card.UpgradeItem u && u.kind()==kind?Math.min(3,s.getCount()):0;}
    public int range(){return com.zuxelus.energycontrol.port.core.UpgradePolicy.range(upgrades(com.zuxelus.energycontrol.port.card.UpgradeItem.Kind.RANGE));}
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override protected Component getDefaultName() { return Component.translatable(holographic()?"block.energycontrol.holo_panel":advanced() ? "block.energycontrol.info_panel_advanced" : "block.energycontrol.info_panel"); }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) { return new PanelMenu(id,inventory,this); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot>=0 && (slot<cardSlots()?stack.getItem() instanceof CardItem:slot<getContainerSize() && stack.getItem() instanceof com.zuxelus.energycontrol.port.card.UpgradeItem u && u.kind().ordinal()==slot-cardSlots()); }

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
                || other.advanced() != advanced() || other.holographic()!=holographic() || other.facing() != facing()) return false;
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
            if (level.getBlockEntity(pos) instanceof PanelBlockEntity other && advanced() && !holographic()
                    && other.getBlockState().getValue(PanelBlock.THICKNESS) != thickness())
                level.setBlockAndUpdate(pos, other.getBlockState().setValue(PanelBlock.THICKNESS, thickness()));
        });
    }
    private void setThickness(int value) {
        if(level == null || !advanced() || holographic() || isExtender()) return;
        int bounded = com.zuxelus.energycontrol.port.core.PanelCase.clamp(value);
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PanelBlock.THICKNESS, bounded));
        regroup(); sync();
    }
    public static void tick(Level level, BlockPos pos, BlockState state, PanelBlockEntity panel) {
        if (level.isClientSide || panel.isExtender()) return;
        if (level.getGameTime() % 20 == 0) panel.regroup();
        boolean power = switch(panel.powerMode) {
            case 0 -> level.hasNeighborSignal(pos); case 1 -> !level.hasNeighborSignal(pos); case 3 -> false; default -> true;
        };
        if (panel.powered != power) { panel.powered = power; panel.sync(); }
        if (level.getGameTime() % panel.refreshTicks == 0) panel.refreshCards();
        int nextPage=com.zuxelus.energycontrol.port.core.DisplayPages.next(panel.page,panel.pageCount,level.getGameTime(),panel.pageTicks);
        if(nextPage!=panel.page){panel.page=nextPage;panel.refreshCards();panel.sync();}
    }
    private void refreshCards() {
        List<com.zuxelus.energycontrol.port.core.DisplayRow> next=new ArrayList<>();
        int capacity=com.zuxelus.energycontrol.port.core.UpgradePolicy.targets(upgrades(com.zuxelus.energycontrol.port.card.UpgradeItem.Kind.CAPACITY));
        int precision=com.zuxelus.energycontrol.port.core.UpgradePolicy.decimals(upgrades(com.zuxelus.energycontrol.port.card.UpgradeItem.Kind.PRECISION));
        for(int i=0;i<cardSlots();i++)next.addAll(com.zuxelus.energycontrol.port.card.CardDisplay.rows(level,worldPosition,items.get(i),range(),capacity,precision));
        if(next.size()>256){int omitted=next.size()-255;next=new ArrayList<>(next.subList(0,255));next.add(com.zuxelus.energycontrol.port.core.DisplayRow.text(omitted+" more lines (display limit)"));}
        int oldCount=pageCount,oldPage=page;pageCount=com.zuxelus.energycontrol.port.core.DisplayPages.count(next.size(),pageSize);page=Math.clamp(page,0,pageCount-1);
        var visible=com.zuxelus.energycontrol.port.core.DisplayPages.slice(next,page,pageSize);
        if(!visible.equals(rows)||oldCount!=pageCount||oldPage!=page){rows=visible;sync();}
    }
    public void setText(int slot,String text){
        if(slot<0||slot>=cardSlots()||text.length()>512)return;
        com.zuxelus.energycontrol.port.card.CardDisplay.edit(items.get(slot),text);setChanged();refreshCards();
    }
    public void cycleColor() { color = switch(color) { case 0x55ff55 -> 0xffffff; case 0xffffff -> 0xffaa00; default -> 0x55ff55; }; sync(); }
    public void togglePower() { powerMode = (powerMode + 1) % 4; sync(); }
    public boolean configure(int id) {
        if(id>=200 && id<200+cardSlots()){
            var stack=items.get(id-200);if(!(stack.getItem() instanceof CardItem))return false;
            var data=CardItem.data(stack);data.putBoolean("showEach",!data.getBoolean("showEach"));CardItem.update(stack,data);refreshCards();sync();return true;
        }
        if(id>=100 && id<100+cardSlots()*2) {
            var stack=items.get((id-100)/2);
            if(!(stack.getItem() instanceof CardItem)) return false;
            var data=CardItem.data(stack);String key=id%2==0?"hideLabels":"hidePercent";
            data.putBoolean(key,!data.getBoolean(key));CardItem.update(stack,data);refreshCards();sync();return true;
        }
        if(id>=300 && id<300+cardSlots()) {
            var stack=items.get(id-300); if(!(stack.getItem() instanceof CardItem))return false;
            var data=CardItem.data(stack);data.putBoolean("showBars",!data.getBoolean("showBars"));CardItem.update(stack,data);refreshCards();sync();return true;
        }
        if(id>=15 && id<=17) {
            if(!advanced() || holographic())return false;
            setThickness(id==16?16:id==17?thickness()+1:thickness()-1);return true;
        }
        if(id>=11 && id<=14 && !holographic())return false;
        switch(id) {
            case 7 -> page=(page+1)%pageCount;
            case 8 -> page=Math.floorMod(page-1,pageCount);
            case 9 -> {pageSize=switch(pageSize){case 4->8;case 8->16;case 16->32;default->4;};page=0;}
            case 10 -> pageTicks=switch(pageTicks){case 0->40;case 40->100;case 100->200;default->0;};
            case 11 -> projection=projection.nextPitch();
            case 12 -> projection=projection.nextYaw();
            case 13 -> projection=projection.nextDepth();
            case 14 -> projection=com.zuxelus.energycontrol.port.core.ProjectionSettings.DEFAULT;
            case 2 -> scalePercent = scalePercent >= 200 ? 50 : scalePercent + 25;
            case 3 -> alignment = (alignment + 1) % 3;
            case 4 -> uniformFont = !uniformFont;
            case 5 -> refreshTicks = switch(refreshTicks) { case 5 -> 10; case 10 -> 20; case 20 -> 40; default -> 5; };
            case 6 -> background = background == 0x080b0c ? 0x303840 : 0x080b0c;
            default -> { return false; }
        }
        refreshCards();sync(); return true;
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
        tag.putInt("page",page);tag.putInt("pageSize",pageSize);tag.putInt("pageCount",pageCount);tag.putInt("pageTicks",pageTicks);projection.save(tag);
        tag.putInt("powerMode",powerMode); tag.putInt("refreshTicks",refreshTicks); tag.putBoolean("uniformFont",uniformFont);
        ListTag list = new ListTag(); rows.forEach(row -> list.add(row.save())); tag.put("displayRows",list);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag,registries);
        items = NonNullList.withSize(11,ItemStack.EMPTY); ContainerHelper.loadAllItems(tag,items,registries);
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
        pageSize=tag.contains("pageSize")?com.zuxelus.energycontrol.port.core.DisplayPages.size(tag.getInt("pageSize")):32;
        pageCount=Math.clamp(tag.getInt("pageCount"),1,64);page=Math.clamp(tag.getInt("page"),0,pageCount-1);
        pageTicks=tag.getInt("pageTicks");if(pageTicks!=40&&pageTicks!=100&&pageTicks!=200)pageTicks=0;
        projection=com.zuxelus.energycontrol.port.core.ProjectionSettings.load(tag);
        var result = new ArrayList<com.zuxelus.energycontrol.port.core.DisplayRow>();
        if(tag.contains("displayRows",Tag.TAG_LIST)) {
            var list=tag.getList("displayRows",Tag.TAG_COMPOUND);
            for(int i=0;i<Math.min(32,list.size());i++)result.add(com.zuxelus.energycontrol.port.core.DisplayRow.load(list.getCompound(i)));
        } else {
            var list=tag.getList("lines",Tag.TAG_STRING);
            for(int i=0;i<Math.min(32,list.size());i++)result.add(com.zuxelus.energycontrol.port.core.DisplayRow.text(list.getString(i)));
        }
        rows = List.copyOf(result);
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
