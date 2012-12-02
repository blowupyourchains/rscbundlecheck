package org.dyndns.fichtner.rsccheck.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * Methods copied from reflectk (www.reflectk.de)
 */
public final class Jdkutil {

	private static final Map<String, Class<?>> PRIMITVE_ARRAYS = createPrimitiveArrayMapping();

	private Jdkutil() {
		throw new IllegalStateException();
	}

	public static String constructClassname(final String classname,
			final int arrayDepth, final boolean vararg) {
		return arrayDepth == 0 ? vararg ? classname + "..." : classname : classname + getArrayString(arrayDepth); //$NON-NLS-1$
	}

	private static Class<? extends Object> createArray(final Class<?> clazz,
			final int depth) {
		return Array.newInstance(clazz,
				(int[]) Array.newInstance(int.class, depth)).getClass();
	}

	private static Map<String, Class<?>> createPrimitiveArrayMapping() {
		final Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		final Class<?>[] primitives = getPrimitives();
		for (int i = 0; i < primitives.length; i++) {
			final Class<?> clazz = primitives[i];
			if (clazz != Void.TYPE) {
				map.put(createArray(clazz, 1).getName(), clazz);
			}

		}
		return map;
	}

	public static String getArrayString(final int arrayDepth) {
		final char[] result = new char[arrayDepth * 2];
		for (int i = 0; i < result.length; i += 2) {
			result[i] = '[';
			result[i + 1] = ']';
		}
		return new String(result).intern();
	}

	public static String getExternalName(final String classname) {
		if (classname.length() >= 2 && classname.charAt(0) == '[') {
			final int arrayDepth = classname.lastIndexOf('[') + 1;
			final Class<?> primitiveType = PRIMITVE_ARRAYS.get(classname
					.substring(arrayDepth - 1));
			return constructClassname(primitiveType == null ? classname
					.substring(1 + arrayDepth, classname.length() - 1)
					: primitiveType.getName(), arrayDepth, false);
		}
		return classname;
	}

	public static String[] getExternalNames(final Class<?>[] classes) {
		final String[] result = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			result[i] = getExternalName(classes[i].getName());
		}
		return result;
	}

	public static Class<?>[] getPrimitives() {
		return new Class[] { Boolean.TYPE, Byte.TYPE, Character.TYPE,
				Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE,
				Void.TYPE };
	}

}
