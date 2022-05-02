package com.zuxelus.energycontrol.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class StringUtils {
	private static DecimalFormat formatter;

	private static DecimalFormat getFormatter() {
		if (formatter == null) {
			DecimalFormat lFormatter = new DecimalFormat("#,###.###");
			DecimalFormatSymbols smb = new DecimalFormatSymbols();
			smb.setGroupingSeparator(' ');
			lFormatter.setDecimalFormatSymbols(smb);
			formatter = lFormatter;
		}
		return formatter;
	}

	public static String getFormatted(String resourceName, String value, boolean showLabels) {
		if (showLabels)
			return StatCollector.translateToLocalFormatted(resourceName, value);
		return value;
	}

	public static String getFormatted(String resourceName, double value, boolean showLabels) {
		return getFormatted(resourceName, getFormatter().format(value), showLabels);
	}

	public static String getFormattedKey(String resourceName, Object... arguments) {
		return StatCollector.translateToLocalFormatted(resourceName, arguments);
	}

	public static String getItemId(ItemStack stack) {
		return Item.itemRegistry.getNameForObject(stack.getItem()).toString();
	}

	@SideOnly(Side.CLIENT)
	public static String getItemName(ItemStack stack) {
		List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
		if (list.size() == 0)
			return stack.getItem().getUnlocalizedName();
		return list.get(0);
	}
}
