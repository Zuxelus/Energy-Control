package com.zuxelus.energycontrol.items;

public abstract interface IItemUpgradeable {

	public int getDefaultMaxCharge();

	public int getDefaultTier();

	public int getDefaultTransferLimit();

	public int getItemTier();

	public int getMaxUpgradeableCharge();

	public int getMaxUpgradeableTransfer();
}
