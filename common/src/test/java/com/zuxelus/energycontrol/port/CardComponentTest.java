package com.zuxelus.energycontrol.port;

import com.zuxelus.energycontrol.port.card.CardItem;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Loads vanilla registries only; no world, server or client process is launched. */
class CardComponentTest {
    @BeforeAll static void bootstrapRegistries() { SharedConstants.tryDetectVersion(); Bootstrap.bootStrap(); }
    @Test void editingDetachedTagRequiresExplicitComponentCommit() {
        ItemStack stack=new ItemStack(Items.PAPER);
        var tag=CardItem.data(stack); tag.putString("text","Control room");
        assertEquals("",CardItem.data(stack).getString("text"));
        CardItem.update(stack,tag);
        assertEquals("Control room",CardItem.data(stack).getString("text"));
        tag.putString("text","Changed outside stack");
        assertEquals("Control room",CardItem.data(stack).getString("text"));
    }
    @Test void copiedCardKeepsIndependentDataAndDimension() {
        ItemStack original=new ItemStack(Items.PAPER);
        var tag=CardItem.data(original); tag.putString("dimension","minecraft:the_nether"); tag.putLong("target",12345); tag.putString("text","A");
        CardItem.update(original,tag);
        ItemStack copy=original.copy(); var changed=CardItem.data(copy); changed.putString("text","B"); CardItem.update(copy,changed);
        assertEquals("A",CardItem.data(original).getString("text"));
        assertEquals("B",CardItem.data(copy).getString("text"));
        assertEquals("minecraft:the_nether",CardItem.data(copy).getString("dimension"));
        assertEquals(12345,CardItem.data(copy).getLong("target"));
    }
}
