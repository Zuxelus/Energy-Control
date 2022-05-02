package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemComponent extends Item {
	public static final int ADVANCED_CIRCUIT = 0;
	public static final int BASIC_CIRCUIT = 1;
	public static final int MACHINE_CASING = 2;
	public static final int RADIO_TRANSMITTER = 3;
	public static final int STRONG_STRING = 4;
	private IIcon[] icons = new IIcon[5];

	public ItemComponent() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {
		icons[0] = ir.registerIcon(EnergyControl.MODID + ":" + "advanced_circuit");
		icons[1] = ir.registerIcon(EnergyControl.MODID + ":" + "basic_circuit");
		icons[2] = ir.registerIcon(EnergyControl.MODID + ":" + "machine_casing");
		icons[3] = ir.registerIcon(EnergyControl.MODID + ":" + "radio_transmitter");
		icons[4] = ir.registerIcon(EnergyControl.MODID + ":" + "strong_string");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		switch (damage) {
		case ADVANCED_CIRCUIT:
		default:
			return "item.advanced_circuit";
		case BASIC_CIRCUIT:
			return "item.basic_circuit";
		case MACHINE_CASING:
			return "item.machine_casing";
		case RADIO_TRANSMITTER:
			return "item.radio_transmitter";
		case STRONG_STRING:
			return "item.strong_string";
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		if (damage > 4)
			return icons[0];
		return icons[damage];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List items) {
		items.add(new ItemStack(ModItems.itemComponent, 1, ADVANCED_CIRCUIT));
		items.add(new ItemStack(ModItems.itemComponent, 1, BASIC_CIRCUIT));
		items.add(new ItemStack(ModItems.itemComponent, 1, MACHINE_CASING));
		items.add(new ItemStack(ModItems.itemComponent, 1, RADIO_TRANSMITTER));
		items.add(new ItemStack(ModItems.itemComponent, 1, STRONG_STRING));
	}
}
