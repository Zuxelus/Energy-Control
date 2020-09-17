package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemUpgrade extends Item {
	public static final int DAMAGE_RANGE = 0;
	public static final int DAMAGE_COLOR = 1;
	public static final int DAMAGE_TOUCH = 2;
	private IIcon iconRange;
	private IIcon iconColor;
	private IIcon iconTouch;

	public ItemUpgrade() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		iconRange = iconRegister.registerIcon(EnergyControl.MODID + ":" + "upgrade_range");
		iconColor = iconRegister.registerIcon(EnergyControl.MODID + ":" + "upgrade_color");
		iconTouch = iconRegister.registerIcon(EnergyControl.MODID + ":" + "upgrade_touch");
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		switch (damage) {
		case DAMAGE_RANGE:
		default:
			return "item.upgrade_range";
		case DAMAGE_COLOR:
			return "item.upgrade_color";
		case DAMAGE_TOUCH:
			return "item.upgrade_touch";
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		switch (damage) {
		case DAMAGE_RANGE:
		default:
			return iconRange;
		case DAMAGE_COLOR:
			return iconColor;
		case DAMAGE_TOUCH:
			return iconTouch;
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List items) {
		items.add(new ItemStack(ItemHelper.itemUpgrade, 1, DAMAGE_RANGE));
		items.add(new ItemStack(ItemHelper.itemUpgrade, 1, DAMAGE_COLOR));
		items.add(new ItemStack(ItemHelper.itemUpgrade, 1, DAMAGE_TOUCH));
	}
}
