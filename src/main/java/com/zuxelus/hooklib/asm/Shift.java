package com.zuxelus.hooklib.asm;

public enum Shift {
	BEFORE, AFTER, INSTEAD;

	public static Shift valueOfNullable(String shift) {
		return shift == null ? Shift.AFTER : valueOf(shift);
	}
}
