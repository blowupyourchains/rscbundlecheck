package org.dyndns.fichtner.rsccheck.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

import de.reflectk.ContentReader;
import de.reflectk.Inspect4J;

// TODO Suche nach deklarierten Strings? Problem. Suche nach Keys die nicht per Stack verfolgt werden koennen (siehe HashMap Beispiel) 
/**
 * Collect all calls to NlsAccess-methods or NlsAccess-constructors.
 */
public class NlsReferenceCheck {

	public static class NlsAccess {

		private final String firstArg;
		private final int paramCount;

		public NlsAccess(String firstArg, int paramCount) {
			this.firstArg = firstArg;
			this.paramCount = paramCount;
		}

		public String getFirstArg() {
			return this.firstArg;
		}

		public int getParamCount() {
			return this.paramCount;
		}

	}

	private final Set<NlsAccess> nlsKeys;
	private final Member inspected;
	private final String desc;
	private final boolean isMethod;
	private final boolean isStatic;
	private final int parCount;

	private String classnameUnderTest;

	public NlsReferenceCheck(final Member inspected) {
		super();
		if (inspected instanceof Constructor<?>) {
			final Constructor<?> constructor = (Constructor<?>) inspected;
			this.desc = Type.getConstructorDescriptor(constructor);
			this.isMethod = false;
			this.isStatic = false;
			this.parCount = constructor.getParameterTypes().length;
		} else if (inspected instanceof Method) {
			final Method method = (Method) inspected;
			this.desc = Type.getMethodDescriptor(method);
			this.isMethod = true;
			this.isStatic = Modifier.isStatic(method.getModifiers());
			this.parCount = method.getParameterTypes().length;
		} else {
			throw new IllegalArgumentException(
					"member must not be null and of type method or constructor");
		}
		this.inspected = inspected;
		this.nlsKeys = new HashSet<NlsAccess>();
	}

	public String getClassnameUnderTest() {
		return this.classnameUnderTest;
	}

	public void check(final String classpath) {
		// check all classes on classpath
		Inspect4J.readContent(classpath, classpath, new ContentReader() {
			public void readContent(final String classname, final byte[] bytes) {
				check(classname, bytes);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void check(String classname, final byte[] bytes) {
		NlsReferenceCheck.this.classnameUnderTest = classname;
		final ClassNode clazz = new ClassNode();
		new ClassReader(bytes).accept(clazz, ClassReader.EXPAND_FRAMES);
		// check all methods
		for (final MethodNode meth : (List<MethodNode>) clazz.methods) {
			// TODO Add @Action(name) support
			final Analyzer analyzer = new Analyzer(new SourceInterpreter());
			try {
				analyzer.analyze(clazz.name, meth);
				final Frame[] frames = analyzer.getFrames();
				for (int i = 0; i < meth.instructions.size(); i++) {
					if (meth.instructions.get(i) instanceof MethodInsnNode) {
						final int op = meth.instructions.get(i).getOpcode();
						if ((this.isMethod && this.isStatic && op == Opcodes.INVOKESTATIC)
								|| (this.isMethod && !this.isStatic && Opcodes.INVOKEVIRTUAL == op)
								|| (Opcodes.INVOKESPECIAL == op && !this.isMethod)) {
							doIt1(frames, i, (MethodInsnNode) meth.instructions
									.get(i));
						}
					}
				}
			} catch (final AnalyzerException e) {
				throw new RuntimeException("Error analyzing " + clazz.name, e);
			}
		}
	}

	private void doIt1(final Frame[] frames, final int i,
			final MethodInsnNode inst) {
		if (convert(this.inspected.getDeclaringClass().getName()).equals(
				inst.owner)
				&& (this.inspected.getName().equals(inst.name) || "<init>"
						.equals(inst.name)) && this.desc.equals(inst.desc)) {
			SourceValue tmp = null;
			for (int j = 0; j < this.parCount; j++) {
				tmp = (SourceValue) frames[i].pop();
			}
			if (tmp != null)
				doIt2(frames, i, tmp);
		}
	}

	@SuppressWarnings("unchecked")
	private void doIt2(final Frame[] frames, final int i,
			final SourceValue sourceValue) {
		for (final Iterator<AbstractInsnNode> iter = sourceValue.insns
				.iterator(); iter.hasNext();) {
			doIt3(frames, i, iter.next());
		}
	}

	@SuppressWarnings("unchecked")
	private void doIt3(final Frame[] frames, final int i, AbstractInsnNode inst) {
		if (inst instanceof LdcInsnNode) {
			// TODO Anzahl Parameter versorgen (Achtung! Arrays beachten!
			// getText("foo {0}", "bar", "bar") --> 2, getText("foo {0}", new
			// Object[] { "foo", "bar"}) --> 2)
			this.nlsKeys.add(new NlsAccess(String
					.valueOf(((LdcInsnNode) inst).cst), 0));
			return;
		} else if (inst instanceof VarInsnNode) {
			// TODO: Hier koennte noch mehr Logik zur Erkennung von
			// Sonderfaellen einbauen ...
			final VarInsnNode vtmp = (VarInsnNode) inst;
			if (Opcodes.ALOAD == vtmp.getOpcode()) {
				// TODO Waere hier ein "while previous instanceof LdcInsnNode"
				// sinnvoll?
				for (final Iterator<AbstractInsnNode> iter2 = ((SourceValue) frames[i]
						.getLocal(vtmp.var)).insns.iterator(); iter2.hasNext();) {
					inst = iter2.next();
					if (inst instanceof VarInsnNode
							&& Opcodes.ASTORE == inst.getOpcode()) {
						final AbstractInsnNode previous = inst.getPrevious();
						if (previous instanceof LdcInsnNode) {
							// TODO Anzahl Parameter versorgen (Achtung! Arrays
							// beachten! getText("foo {0}", "bar", "bar") --> 2,
							// getText("foo {0}", new Object[] { "foo", "bar"})
							// --> 2)
							this.nlsKeys.add(new NlsAccess(String
									.valueOf(((LdcInsnNode) previous).cst), 0));
						}
						return;
						// } else {
						// do warn: unable to track reference
					}
				}
			}
		} else if (inst instanceof FieldInsnNode) {
			// TODO: Hier wäre Field-Support ganz nett
		}
	}

	private static String convert(final String clazz) {
		return clazz.replace('.', '/');
	}

	public Set<NlsAccess> getCollectedKeys() {
		return this.nlsKeys;
	}

}
