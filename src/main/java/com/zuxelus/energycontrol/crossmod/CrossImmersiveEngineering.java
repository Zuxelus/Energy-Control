package com.zuxelus.energycontrol.crossmod;

import blusunrize.immersiveengineering.common.blocks.metal.DynamoTileEntity;
import blusunrize.immersiveengineering.common.blocks.wooden.WatermillTileEntity;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class CrossImmersiveEngineering extends CrossModBase {

	@Override
	public CompoundNBT getCardData(TileEntity te) {
		if (te instanceof WatermillTileEntity) {
			WatermillTileEntity mill = (WatermillTileEntity) te;
			CompoundNBT tag = new CompoundNBT();
			tag.putDouble("rotation", mill.rotation);
			tag.putDouble("per_tick", (IEServerConfig.MACHINES.dynamo_output.get()).doubleValue() * mill.rotation);
			return tag;
		}
		if (te instanceof DynamoTileEntity) {
			
		}
		return null;
	}
}
