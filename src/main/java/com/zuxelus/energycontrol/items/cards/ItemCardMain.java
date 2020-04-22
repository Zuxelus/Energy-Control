package com.zuxelus.energycontrol.items.cards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardMain extends Item {
	public static Map<Integer, ItemCardBase> cards;

	public ItemCardMain() {
		super();
		setMaxStackSize(1);
		canRepair = false;
		setCreativeTab(EnergyControl.creativeTab);
		cards = new HashMap<Integer, ItemCardBase>();
		register(new ItemCardEnergy());
		register(new ItemCardCounter());
		register(new ItemCardLiquid());
		register(new ItemCardGenerator());
		register(new ItemCardGeneratorKinetic());
		register(new ItemCardGeneratorHeat());
		register(new ItemCardReactor());
		register(new ItemCardReactor5x5());
		register(new ItemCardLiquidAdvanced());
		register(new ItemCardText());
		register(new ItemCardTime());
		register(new ItemCardEnergyArray());
		register(new ItemCardLiquidArray());
		register(new ItemCardGeneratorArray());
	}
	
	public void register(ItemCardBase item) {
		cards.put(item.getDamage(), item);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (cards.containsKey(damage))
			return cards.get(damage).getUnlocalizedName();
		return "";
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab))
			return;
		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (cards.containsKey(i))
				items.add(new ItemStack(this, 1, i));
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ItemCardReader reader = new ItemCardReader(stack);
		String title = reader.getTitle();
		if (title != null && !title.isEmpty())
			tooltip.add(title);
		switch (stack.getItemDamage()) {
		case ItemCardType.CARD_TEXT:
		case ItemCardType.CARD_TIME:
			return;
		case ItemCardType.CARD_ENERGY_ARRAY:
		case ItemCardType.CARD_LIQUID_ARRAY:
		case ItemCardType.CARD_GENERATOR_ARRAY:
			tooltip.add(I18n.format("msg.ec.cards", reader.getCardCount()));
			return;
		}
		
		BlockPos target = reader.getTarget();
		if (target != null)
			tooltip.add(String.format("x: %d, y: %d, z: %d", target.getX(), target.getY(), target.getZ()));
	}

	public List<PanelString> getStringData(int damage, int settings, ItemCardReader reader, boolean showLabels) {
		if (cards.containsKey(damage))
			return cards.get(damage).getStringData(settings, reader, showLabels);
		return null;
	}

	public List<PanelSetting> getSettingsList(ItemStack stack) {
		int damage = stack.getItemDamage();
		if (cards.containsKey(damage))
			return cards.get(damage).getSettingsList(stack);
		return null;
	}

	public CardState update(int damage, TileEntity panel, ItemCardReader reader, int range) {
		if (cards.containsKey(damage))
			return cards.get(damage).update(panel, reader, range);
		return null;
	}

	public Object getSettingsScreen(int damage, ItemCardReader reader) {
		if (damage != ItemCardType.CARD_TEXT)
			return null;
		return cards.get(ItemCardType.CARD_TEXT).getSettingsScreen(reader);
	}
}
