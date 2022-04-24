package com.zuxelus.hooklib.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.zuxelus.hooklib.asm.HookLogger.SystemOutLogger;

public class HookClassTransformer {

	public static HookLogger logger = new SystemOutLogger();
	protected HashMap<String, List<AsmHook>> hooksMap = new HashMap<String, List<AsmHook>>();
	private HookContainerParser containerParser = new HookContainerParser(this);
	protected ClassMetadataReader classMetadataReader = new ClassMetadataReader();

	public void registerHook(AsmHook hook) {
		if (hooksMap.containsKey(hook.getTargetClassName())) {
			hooksMap.get(hook.getTargetClassName()).add(hook);
		} else {
			List<AsmHook> list = new ArrayList<AsmHook>(2);
			list.add(hook);
			hooksMap.put(hook.getTargetClassName(), list);
		}
	}

	public void registerHookContainer(String className) {
		containerParser.parseHooks(className);
	}

	public void registerHookContainer(byte[] classData) {
		containerParser.parseHooks(classData);
	}

	public byte[] transform(String className, byte[] bytecode) {
		List<AsmHook> hooks = hooksMap.get(className);

		if (hooks != null) {
			Collections.sort(hooks);
			logger.debug("Injecting hooks into class " + className);
			try {
				int majorVersion = ((bytecode[6] & 0xFF) << 8) | (bytecode[7] & 0xFF);
				boolean java7 = majorVersion > 50;

				ClassReader cr = new ClassReader(bytecode);
				ClassWriter cw = createClassWriter(java7 ? ClassWriter.COMPUTE_FRAMES : ClassWriter.COMPUTE_MAXS);
				HookInjectorClassVisitor hooksWriter = createInjectorClassVisitor(cw, hooks);
				cr.accept(hooksWriter, java7 ? ClassReader.SKIP_FRAMES : ClassReader.EXPAND_FRAMES);
				bytecode = cw.toByteArray();
				for (AsmHook hook : hooksWriter.injectedHooks) {
					logger.debug("Patching method " + hook.getPatchedMethodName());
				}
				hooks.removeAll(hooksWriter.injectedHooks);
			} catch (Exception e) {
				logger.severe("A problem has occurred during transformation of class " + className + ".");
				logger.severe("Attached hooks:");
				for (AsmHook hook : hooks) {
					logger.severe(hook.toString());
				}
				logger.severe("Stack trace:", e);
			}

			for (AsmHook notInjected : hooks) {
				if (notInjected.isMandatory()) {
					throw new RuntimeException("Can not find target method of mandatory hook " + notInjected);
				} else {
					logger.warning("Can not find target method of hook " + notInjected);
				}
			}
		}
		return bytecode;
	}

	protected HookInjectorClassVisitor createInjectorClassVisitor(ClassWriter cw, List<AsmHook> hooks) {
		return new HookInjectorClassVisitor(this, cw, hooks);
	}

	protected ClassWriter createClassWriter(int flags) {
		return new SafeClassWriter(classMetadataReader, flags);
	}
}
