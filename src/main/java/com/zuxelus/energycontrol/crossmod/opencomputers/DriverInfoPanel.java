package com.zuxelus.energycontrol.crossmod.opencomputers;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class DriverInfoPanel extends DriverSidedTileEntity {
	public static final String NAME = "info_panel";

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
			super(tileentity, NAME);
		}

		@Override
		public String preferredName() {
			return NAME;
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
			List<PanelString> joinedData = tileEntity.getPanelStringList(false);
			List<String> list = NonNullList.create();
			if (joinedData == null || joinedData.size() == 0)
				return new Object[] { list };

			for (PanelString panelString : joinedData) {
				if (panelString.textLeft != null)
					list.add(panelString.textLeft);
				if (panelString.textCenter != null)
					list.add(panelString.textCenter);
				if (panelString.textRight != null)
					list.add(panelString.textRight);
			}
			return new Object[] { list };
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
			int newColor = args.checkInteger(0);
			if (newColor >= 0 && newColor < 16)
				tileEntity.setColorBackground(newColor);
			return null;
		}

		@Callback(doc = "function(number) -- Set font color.")
		public Object[] setColorText(final Context context, final Arguments args) {
			int newColor = args.checkInteger(0);
			if (newColor >= 0 && newColor < 16)
				tileEntity.setColorText(newColor);
			return null;
		}
	}
}