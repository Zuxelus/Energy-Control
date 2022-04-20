package com.zuxelus.hooklib.asm;

import static com.zuxelus.hooklib.asm.InjectionPoint.HEAD;
import static com.zuxelus.hooklib.asm.InjectionPoint.METHOD_CALL;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import com.zuxelus.hooklib.minecraft.HookLibPlugin;
import com.zuxelus.hooklib.minecraft.MinecraftClassTransformer;

public abstract class HookInjectorMethodVisitor extends AdviceAdapter {

	protected final AsmHook hook;
	protected final HookInjectorClassVisitor cv;
	public final String methodName;
	public final Type methodType;
	public final boolean isStatic;

	protected HookInjectorMethodVisitor(MethodVisitor mv, int access, String name, String desc, AsmHook hook,
			HookInjectorClassVisitor cv) {
		super(Opcodes.ASM5, mv, access, name, desc);
		this.hook = hook;
		this.cv = cv;
		isStatic = (access & Opcodes.ACC_STATIC) != 0;
		methodName = name;
		methodType = Type.getMethodType(desc);
	}

	protected final void visitHook() {
		if (!cv.visitingHook) {
			cv.visitingHook = true;
			hook.inject(this);
			cv.visitingHook = false;
		}
	}

	MethodVisitor getBasicVisitor() {
		return mv;
	}

	public static class ByAnchor extends HookInjectorMethodVisitor {

		private Integer ordinal;

		public ByAnchor(MethodVisitor mv, int access, String name, String desc, AsmHook hook,
				HookInjectorClassVisitor cv) {
			super(mv, access, name, desc, hook, cv);

			ordinal = hook.getAnchorOrdinal();
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			String targetName = HookLibPlugin.getObfuscated()
					? MinecraftClassTransformer.instance.getMethodNames()
							.getOrDefault(MinecraftClassTransformer.getMethodId(name), name)
					: name;
			if (hook.getAnchorPoint() == METHOD_CALL && hook.getAnchorTarget().equals(targetName))
				switch (hook.getShift()) {

				case BEFORE:
					visitOrderedHook();
					super.visitMethodInsn(opcode, owner, name, desc, itf);
					break;
				case AFTER:
					super.visitMethodInsn(opcode, owner, name, desc, itf);
					visitOrderedHook();
					break;
				case INSTEAD:
					if (visitOrderedHook())
						for (int i = 0; i < Type.getArgumentTypes(desc).length + 1; i++)
							visitInsn(Opcodes.POP);
					else
						super.visitMethodInsn(opcode, owner, name, desc, itf);
					break;
				}
			else
				super.visitMethodInsn(opcode, owner, name, desc, itf);

		}

		protected void onMethodEnter() {
			if (hook.getAnchorPoint() == HEAD)
				visitHook();
		}

		protected void onMethodExit(int opcode) {
			if (hook.getAnchorPoint() == InjectionPoint.RETURN && opcode != Opcodes.ATHROW)
				visitOrderedHook();

		}

		private boolean visitOrderedHook() {
			if (ordinal == 0) {
				visitHook();
				ordinal = -2;
				return true;
			} else if (ordinal == -1) {
				visitHook();
				return true;
			} else if (ordinal > 0)
				ordinal -= 1;
			return false;
		}
	}

	public static class MethodEnter extends HookInjectorMethodVisitor {

		public MethodEnter(MethodVisitor mv, int access, String name, String desc, AsmHook hook,
				HookInjectorClassVisitor cv) {
			super(mv, access, name, desc, hook, cv);
		}

		@Override
		protected void onMethodEnter() {
			visitHook();
		}

	}

	public static class MethodExit extends HookInjectorMethodVisitor {

		public MethodExit(MethodVisitor mv, int access, String name, String desc, AsmHook hook,
				HookInjectorClassVisitor cv) {
			super(mv, access, name, desc, hook, cv);
		}

		@Override
		protected void onMethodExit(int opcode) {
			if (opcode != Opcodes.ATHROW) {
				visitHook();
			}
		}
	}

	public static class LineNumber extends HookInjectorMethodVisitor {

		private int lineNumber;

		public LineNumber(MethodVisitor mv, int access, String name, String desc, AsmHook hook,
				HookInjectorClassVisitor cv, int lineNumber) {
			super(mv, access, name, desc, hook, cv);
			this.lineNumber = lineNumber;
		}

		@Override
		public void visitLineNumber(int line, Label start) {
			super.visitLineNumber(line, start);
			if (lineNumber == line)
				visitHook();
		}
	}

}
