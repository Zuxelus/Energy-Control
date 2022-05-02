package com.zuxelus.hooklib.asm;

public enum ReturnCondition {
	NEVER(false), ALWAYS(false), ON_TRUE(true), ON_NULL(true), ON_NOT_NULL(true);

	public final boolean requiresCondition;

	ReturnCondition(boolean requiresCondition) {
		this.requiresCondition = requiresCondition;
	}
}
