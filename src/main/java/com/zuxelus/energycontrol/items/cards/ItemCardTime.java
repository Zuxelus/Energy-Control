package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import ic2.api.item.IC2Items;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardTime extends ItemCardBase {
	public ItemCardTime() {
		super(ItemCardType.CARD_TIME, "card_time");
		//addRecipe(new Object[] { " C ", "PWP", " C ", 'C', Items.CLOCK, 'P', Items.PAPER, 'W', IC2Items.getItem("cable", "type:tin,insulation:1") });
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		return CardState.OK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = new ArrayList<PanelString>(1);
		int time = (int) ((FMLClientHandler.instance().getClient().world.getWorldTime() + 6000) % 24000);
		int hours = time / 1000;
		int minutes = (time % 1000) * 6 / 100;
		String suffix = "";
		if ((displaySettings & 1) == 0) {
			suffix = hours < 12 ? "AM" : "PM";
			hours %= 12;
			if (hours == 0)
				hours += 12;
		}
		result.add(new PanelString("msg.ec.InfoPanelTime",String.format("%02d:%02d%s", hours, minutes, suffix),showLabels));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(1);
		result.add(new PanelSetting(I18n.format("msg.ec.cb24h"), 1, damage));
		return result;
	}

	@Override
	public boolean isRemoteCard(int damage) {
		return false;
	}
}