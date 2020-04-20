package com.zuxelus.energycontrol.items.cards;

import java.util.List;

import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemCardBase {
	private String textureName;
	protected int damage;
	
	public ItemCardBase(int damage, String textureName) {
		this.damage = damage;
		this.textureName = textureName;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public abstract String getUnlocalizedName();
		
	public CardState update(TileEntity panel, ItemCardReader reader, int range) {
		return update(panel.getWorld(), reader, range, panel.getPos());
	}
	
	public abstract CardState update(World world, ItemCardReader reader, int range, BlockPos pos);

	protected abstract List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels);

	protected abstract List<PanelSetting> getSettingsList(ItemStack stack);

	public ICardGui getSettingsScreen(ItemCardReader reader) {
		return null;
	}
	
	protected BlockPos getCoordinates(ItemCardReader reader, int cardNumber) {
		if (cardNumber >= reader.getCardCount())
			return null;
		return new BlockPos(reader.getInt(String.format("_%dx", cardNumber)),
				reader.getInt(String.format("_%dy", cardNumber)), reader.getInt(String.format("_%dz", cardNumber)));
	}	
}
