package com.zuxelus.hooklib.asm;

import org.objectweb.asm.MethodVisitor;

public abstract class HookInjectorFactory {

	protected boolean isPriorityInverted = false;

	abstract HookInjectorMethodVisitor createHookInjector(MethodVisitor mv, int access, String name, String desc, AsmHook hook, HookInjectorClassVisitor cv);

	static class ByAnchor extends HookInjectorFactory {
		public static final ByAnchor INSTANCE = new ByAnchor();

		private ByAnchor() {
		}

		public HookInjectorMethodVisitor createHookInjector(MethodVisitor mv, int access, String name, String desc, AsmHook hook, HookInjectorClassVisitor cv) {
			return new HookInjectorMethodVisitor.ByAnchor(mv, access, name, desc, hook, cv);
		}
	}

	static class MethodEnter extends HookInjectorFactory {
		public static final MethodEnter INSTANCE = new MethodEnter();

		private MethodEnter() {
		}

		@Override
		public HookInjectorMethodVisitor createHookInjector(MethodVisitor mv, int access, String name, String desc, AsmHook hook, HookInjectorClassVisitor cv) {
			return new HookInjectorMethodVisitor.MethodEnter(mv, access, name, desc, hook, cv);
		}

	}

	static class MethodExit extends HookInjectorFactory {
		public static final MethodExit INSTANCE = new MethodExit();

		private MethodExit() {
			isPriorityInverted = true;
		}

		@Override
		public HookInjectorMethodVisitor createHookInjector(MethodVisitor mv, int access, String name, String desc, AsmHook hook, HookInjectorClassVisitor cv) {
			return new HookInjectorMethodVisitor.MethodExit(mv, access, name, desc, hook, cv);
		}
	}

	static class LineNumber extends HookInjectorFactory {
		private int lineNumber;

		public LineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		@Override
		public HookInjectorMethodVisitor createHookInjector(MethodVisitor mv, int access, String name, String desc, AsmHook hook, HookInjectorClassVisitor cv) {
			return new HookInjectorMethodVisitor.LineNumber(mv, access, name, desc, hook, cv, lineNumber);
		}
	}

}
