package com.zuxelus.energycontrol.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class MediumBoxModel extends ModelBase {
	ModelRenderer box;

	public MediumBoxModel() {
		textureWidth = 128;
		textureHeight = 64;

		box = new ModelRenderer(this, 0, 0);
		box.addBox(2, 0, 2, 28, 14, 28);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		box.render(f5);
	}
}