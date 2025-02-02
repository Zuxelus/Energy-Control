/*package com.zuxelus.energycontrol.containers.slots;

import com.zuxelus.energycontrol.EnergyControl;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

public class SlotHandler extends SpriteSourceProvider {
    public SlotHandler(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper, EnergyControl.MODID);
    }

    @Override
    protected void addSources() {
        SourceList blocks = atlas(InventoryMenu.BLOCK_ATLAS);
        blocks.addSource(new SingleFile(
                new ResourceLocation(EnergyControl.MODID + ":slots/slot_card"), Optional.empty()
        ));
        blocks.addSource(new SingleFile(
                new ResourceLocation(EnergyControl.MODID + ":slots/slot_color"), Optional.empty()
        ));
        blocks.addSource(new SingleFile(
                new ResourceLocation(EnergyControl.MODID + ":slots/slot_range"), Optional.empty()
        ));
        blocks.addSource(new SingleFile(
                new ResourceLocation(EnergyControl.MODID + ":slots/slot_touch"), Optional.empty()
        ));
        blocks.addSource(new SingleFile(
                new ResourceLocation(EnergyControl.MODID + ":slots/slot_power"), Optional.empty()
        ));
        blocks.addSource(new SingleFile(
                new ResourceLocation("zlib:slots/slot_dischargeable"), Optional.empty()
        ));
        blocks.addSource(new SingleFile(
                new ResourceLocation("zlib:slots/slot_transformer"), Optional.empty()
        ));
    }
}*/
