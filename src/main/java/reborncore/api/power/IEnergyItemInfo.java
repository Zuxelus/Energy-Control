package reborncore.api.power;

import net.minecraft.item.ItemStack;

public interface IEnergyItemInfo {

	double getMaxPower(ItemStack stack);

	boolean canAcceptEnergy(ItemStack stack);

	boolean canProvideEnergy(ItemStack stack);

	double getMaxTransfer(ItemStack stack);

	int getStackTier(ItemStack stack);
}
