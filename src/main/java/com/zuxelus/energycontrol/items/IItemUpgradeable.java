package com.zuxelus.energycontrol.items;

public interface IItemUpgradeable {

	int getDefaultMaxCharge();

	int getDefaultTier();

	int getDefaultTransferLimit();

	int getItemTier();

	int getMaxUpgradeableCharge();

	int getMaxUpgradeableTransfer();
}
