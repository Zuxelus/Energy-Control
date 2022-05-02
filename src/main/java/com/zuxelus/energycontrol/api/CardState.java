package com.zuxelus.energycontrol.api;

public enum CardState {
	OK(1),
	NO_TARGET(2),
	OUT_OF_RANGE(3),
	INVALID_CARD(4),
	CUSTOM_ERROR(5);

	private final int index;

	CardState(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public static CardState fromInteger(int value) {
		switch (value) {
		case 1:
			return OK;
		case 2:
			return NO_TARGET;
		case 3:
			return OUT_OF_RANGE;
		case 4:
			return INVALID_CARD;
		case 5:
			return CUSTOM_ERROR;
		}
		return CUSTOM_ERROR;
	}
}