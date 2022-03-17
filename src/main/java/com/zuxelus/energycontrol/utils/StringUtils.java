package com.zuxelus.energycontrol.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
			return I18n.get(resourceName, value);
		return value;
	}

	public static String getFormatted(String resourceName, double value, boolean showLabels) {
		return getFormatted(resourceName, getFormatter().format(value), showLabels);
	}

	public static String getFormattedKey(String resourceName, Object... arguments) {
		return I18n.get(resourceName, arguments);
	}

	@OnlyIn(Dist.CLIENT)
	public static String getItemName(ItemStack stack) {
		List<Component> list = stack.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
		if (list.size() == 0)
			return stack.getItem().getDescriptionId();
		return list.get(0).getString();// .getFormattedText();
	}
}
