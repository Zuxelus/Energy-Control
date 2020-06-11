package com.zuxelus.energycontrol.crossmod.computercraft;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ArgumentHelper {
	private static String CCARG = "dan200.computercraft.core.apis.ArgumentHelper";
	private static String CCTVARG = "dan200.computercraft.api.lua.ArgumentHelper";

	public static int getInt(Object[] args, int index) {
		Class c = null;
		try {
			c = Class.forName(CCARG);
		} catch (ClassNotFoundException e) { }
		if (c == null)
			try {
				c = Class.forName(CCTVARG);
			} catch (ClassNotFoundException e) { }
		if (c == null)
			return -1;
		try {
			Method m = c.getDeclaredMethod("getInt", Object[].class, int.class);
			return (int) m.invoke(c, args, index);
		} catch (ReflectiveOperationException e) {
			return -1;
		}
	}

	public static String getString(Object[] args, int index) {
		Class c = null;
		try {
			c = Class.forName(CCARG);
		} catch (ClassNotFoundException e) { }
		if (c == null)
			try {
				c = Class.forName(CCTVARG);
			} catch (ClassNotFoundException e) { }
		if (c == null)
			return null;
		try {
			Method m = c.getDeclaredMethod("getString", Object[].class, int.class);
			return (String) m.invoke(c, args, index);
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}
}
