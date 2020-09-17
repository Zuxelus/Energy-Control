package com.zuxelus.energycontrol.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityTechArrow extends EntityArrow {
	private boolean explosive = false;
	private float explosionPower = 3.0f;

	public EntityTechArrow(World world) {
		super(world);
	}

	public EntityTechArrow(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityTechArrow(World world, EntityLivingBase shooter, float velocity) {
		super(world, shooter, velocity);
	}

	public void setExplosive(boolean value) {
		explosive = value;
	}

	public void setExplosionPower(float value) {
		explosionPower = value;
	}

	@Override
	protected void func_145775_I() {
		super.func_145775_I();

		if ((isDead || arrowShake > 0) && explosive) {
			worldObj.newExplosion(null, posX, posY, posZ, explosionPower, false, false);
			setDead();
		}
	}
}