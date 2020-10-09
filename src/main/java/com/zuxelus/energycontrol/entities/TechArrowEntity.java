package com.zuxelus.energycontrol.entities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class TechArrowEntity extends ArrowEntity {
	private boolean explosive = false;
	private float explosionPower = 3.0f;

	public TechArrowEntity(World world, LivingEntity shooter) {
		super(world, shooter);
	}

	public void setExplosive(boolean value) {
		explosive = value;
	}

	public void setExplosionPower(float value) {
		explosionPower = value;
	}

	@Override
	protected ItemStack asItemStack() {
		return ItemStack.EMPTY;
	}

	@Override
	protected void checkBlockCollision() {
		super.checkBlockCollision();

		if ((removed || shake > 0) && explosive) {
			Vec3d pos = getPos();
			world.createExplosion(null, pos.x, pos.y, pos.z, explosionPower, DestructionType.NONE);
			remove();
		}
	}
}