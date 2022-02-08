package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import li.cil.oc.integration.ManagedTileEntityEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class DriverInfoPanel extends DriverSidedTileEntity {

	@Override
	public Class<?> getTileEntityClass() {
		return TileEntityInfoPanel.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(final World world, final BlockPos pos, final EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityAdvancedInfoPanel)
			return null;
		return new Environment((TileEntityInfoPanel) world.getTileEntity(pos));
	}

	public static final class Environment extends ManagedTileEntityEnvironment<TileEntityInfoPanel> implements NamedBlock {
		public Environment(final TileEntityInfoPanel tileentity) {
			super(tileentity, TileEntityInfoPanel.NAME);
		}

		@Override
		public String preferredName() {
			return TileEntityInfoPanel.NAME;
		}

		@Override
		public int priority() {
			return 0;
		}

		@Callback(doc = "function():boolean -- Check if panel has color upgrade.")
		public Object[] hasColorUpgrade(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getColored() };
		}

		@Callback(doc = "function():boolean -- Check if panel is active.")
		public Object[] isActive(final Context context, final Arguments args) {
			return new Object[] { tileEntity.powered };
		}

		@Callback(doc = "function():number -- Get panel range.")
		public Object[] getRange(final Context context, final Arguments args) {
			ItemStack itemStack = tileEntity.getStackInSlot(tileEntity.getSlotUpgradeRange());
			int upgradeCountRange = 0;
			if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
				upgradeCountRange = itemStack.getCount();
			return new Object[] { ItemCardMain.LOCATION_RANGE * (int) Math.pow(2, Math.min(upgradeCountRange, 7)) };
		}

		@Callback(doc = "function():list<string> -- Get card data.")
		public Object[] getCardData(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getPanelStringList(false) };
		}

		@Callback(doc = "function():list<string> -- Get raw card data.")
		public Object[] getCardDataRaw(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getPanelStringList(true) };
		}

		@Callback(doc = "function():number -- Get background color.")
		public Object[] getColorBack(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getColorBackground() };
		}

		@Callback(doc = "function():number -- Get font color.")
		public Object[] getColorText(final Context context, final Arguments args) {
			return new Object[] { tileEntity.getColorText() };
		}

		@Callback(doc = "function(number) -- Set background color.")
		public Object[] setColorBack(final Context context, final Arguments args) {
			int value = args.checkInteger(0);
			if (value >= 0 && value < 16)
				tileEntity.setColorBackground(value);
			return null;
		}

		@Callback(doc = "function(number) -- Set font color.")
		public Object[] setColorText(final Context context, final Arguments args) {
			int value = args.checkInteger(0);
			if (value >= 0 && value < 16)
				tileEntity.setColorText(value);
			return null;
		}

		@Callback(doc = "function():string -- Get card title.")
		public Object[] getCardTitle(final Context context, final Arguments args) {
			ItemStack stack = tileEntity.getStackInSlot(0);
			if (!ItemCardMain.isCard(stack))
				return new Object[] { "" }; 
			return new Object[] { new ItemCardReader(stack).getTitle() };
		}

		@Callback(doc = "function(string) -- Set card title.")
		public Object[] setCardTitle(final Context context, final Arguments args) {
			String title = args.checkString(0);
			ItemStack stack = tileEntity.getStackInSlot(0);
			if (ItemCardMain.isCard(stack))
				new ItemCardReader(stack).setTitle(title);
			return null;
		}
	}
}
