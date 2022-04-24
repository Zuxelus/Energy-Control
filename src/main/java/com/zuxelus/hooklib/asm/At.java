package com.zuxelus.hooklib.asm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface At {

	public InjectionPoint point();

	public Shift shift() default Shift.AFTER;

	public String target() default "";

	public int ordinal() default -1;
}
