package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.slots.*;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.zlib.containers.slots.SlotChargeable;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemUpgrade extends Item {
	public static final int DAMAGE_RANGE = 0;
	public static final int DAMAGE_COLOR = 1;
	public static final int DAMAGE_TOUCH = 2;
	public static final int DAMAGE_WEB = 3;
	private IIcon[] icons = new IIcon[4];

	public ItemUpgrade() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) { // 1.7.10
		icons[0] = ir.registerIcon(EnergyControl.MODID + ":" + "upgrade_range");
		icons[1] = ir.registerIcon(EnergyControl.MODID + ":" + "upgrade_color");
		icons[2] = ir.registerIcon(EnergyControl.MODID + ":" + "upgrade_touch");
		icons[3] = ir.registerIcon(EnergyControl.MODID + ":" + "upgrade_web");

		SlotCard.slotIcon = ir.registerIcon(EnergyControl.MODID + ":slots/slot_card");
		SlotColor.slotIcon = ir.registerIcon(EnergyControl.MODID + ":slots/slot_color");
		SlotRange.slotIcon = ir.registerIcon(EnergyControl.MODID + ":slots/slot_range");
		SlotTouch.slotIcon = ir.registerIcon(EnergyControl.MODID + ":slots/slot_touch");
		SlotPower.slotIcon = ir.registerIcon(EnergyControl.MODID + ":slots/slot_power");
		SlotChargeable.slotIcon = ir.registerIcon("zlib:slots/slot_chargeable");
		SlotDischargeable.slotIcons = new IIcon[3];
		SlotDischargeable.slotIcons[0] = ir.registerIcon("zlib:slots/slot_dischargeable_0");
		SlotDischargeable.slotIcons[1] = ir.registerIcon("zlib:slots/slot_dischargeable_1");
		SlotDischargeable.slotIcons[2] = ir.registerIcon("zlib:slots/slot_dischargeable_2");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		switch (damage) {
		case DAMAGE_RANGE:
		default:
			return "item.upgrade_range";
		case DAMAGE_COLOR:
			return "item.upgrade_color";
		case DAMAGE_TOUCH:
			return "item.upgrade_touch";
		case DAMAGE_WEB:
			return "item.upgrade_web";
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) { // 1.7.10
		if (damage > 3)
			return icons[0];
		return icons[damage];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List items) {
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_RANGE));
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_COLOR));
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_TOUCH));
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_WEB));
	}
}
