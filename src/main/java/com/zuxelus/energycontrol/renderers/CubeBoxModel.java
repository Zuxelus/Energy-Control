package com.zuxelus.energycontrol.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class CubeBoxModel extends ModelBase {
	ModelRenderer box;

	public CubeBoxModel() {
		textureWidth = 128;
		textureHeight = 64;

		box = new ModelRenderer(this, 0, 0);
		box.addBox(0, 0, 0, 32, 32, 32);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		box.render(f5);
	}
}
