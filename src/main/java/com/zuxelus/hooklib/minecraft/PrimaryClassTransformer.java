package com.zuxelus.hooklib.minecraft;

import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.zuxelus.hooklib.asm.AsmHook;
import com.zuxelus.hooklib.asm.HookClassTransformer;
import com.zuxelus.hooklib.asm.HookInjectorClassVisitor;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class PrimaryClassTransformer extends HookClassTransformer implements IClassTransformer {

	static PrimaryClassTransformer instance = new PrimaryClassTransformer();
	boolean registeredSecondTransformer;

	public PrimaryClassTransformer() {
		this.classMetadataReader = HookLoader.getDeobfuscationMetadataReader();

		if (instance != null) {
			this.hooksMap.putAll(PrimaryClassTransformer.instance.getHooksMap());
			PrimaryClassTransformer.instance.getHooksMap().clear();
		} else {
			registerHookContainer(SecondaryTransformerHook.class.getName());
		}
		instance = this;
	}

	@Override
	public byte[] transform(String oldName, String newName, byte[] bytecode) {
		return transform(newName, bytecode);
	}

	@Override
	protected HookInjectorClassVisitor createInjectorClassVisitor(ClassWriter cw, List<AsmHook> hooks) {
		return new HookInjectorClassVisitor(this, cw, hooks) {
			@Override
			protected boolean isTargetMethod(AsmHook hook, String name, String desc) {
				return super.isTargetMethod(hook, name, mapDesc(desc));
			}
		};
	}

	HashMap<String, List<AsmHook>> getHooksMap() {
		return hooksMap;
	}

	static String mapDesc(String desc) {
		if (!HookLibPlugin.getObfuscated())
			return desc;

		Type methodType = Type.getMethodType(desc);
		Type mappedReturnType = map(methodType.getReturnType());
		Type[] argTypes = methodType.getArgumentTypes();
		Type[] mappedArgTypes = new Type[argTypes.length];
		for (int i = 0; i < mappedArgTypes.length; i++) {
			mappedArgTypes[i] = map(argTypes[i]);
		}
		return Type.getMethodDescriptor(mappedReturnType, mappedArgTypes);
	}

	static Type map(Type type) {
		if (!HookLibPlugin.getObfuscated())
			return type;

		// void or primitive
		if (type.getSort() < 9)
			return type;

		// array
		if (type.getSort() == 9) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < type.getDimensions(); i++) {
				sb.append("[");
			}
			boolean isPrimitiveArray = type.getSort() < 9;
			if (!isPrimitiveArray)
				sb.append("L");
			sb.append(map(type.getElementType()).getInternalName());
			if (!isPrimitiveArray)
				sb.append(";");
			return Type.getType(sb.toString());
		} else if (type.getSort() == 10) {
			String unmappedName = FMLDeobfuscatingRemapper.INSTANCE.map(type.getInternalName());
			return Type.getType("L" + unmappedName + ";");
		} else {
			throw new IllegalArgumentException("Can not map method type!");
		}
	}
}
