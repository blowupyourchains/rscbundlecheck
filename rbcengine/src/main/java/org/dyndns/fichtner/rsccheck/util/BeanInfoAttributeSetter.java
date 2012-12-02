package org.dyndns.fichtner.rsccheck.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeanInfoAttributeSetter implements AttributeSetter {

	private Method getSetter(final Object bean, final String attributeName)
			throws IntrospectionException {
		final PropertyDescriptor[] propertyDescriptors = getDescriptors(bean);
		for (int i = 0; i < propertyDescriptors.length; i++) {
			final PropertyDescriptor descriptor = propertyDescriptors[i];
			if (attributeName.equals(descriptor.getName())
					&& descriptor.getWriteMethod() != null
					&& hasStringArg(descriptor.getWriteMethod())) {
				return descriptor.getWriteMethod();
			}
		}
		return null;
	}

	private PropertyDescriptor[] getDescriptors(final Object bean)
			throws IntrospectionException {
		return Introspector.getBeanInfo(bean.getClass())
				.getPropertyDescriptors();
	}

	private boolean hasStringArg(final Method method) {
		return method.getParameterTypes().length == 1
				&& method.getParameterTypes()[0] == String.class;
	}

	public void setAttribute(final Object bean, final String attribName,
			final Object value) {
		if (bean == null) {
			throw new IllegalArgumentException("bean must not be null");
		}
		if (attribName == null) {
			throw new IllegalArgumentException("attribName must not be null");
		}
		try {
			final Method method = getSetter(bean, attribName);
			if (method == null) {
				throw new RuntimeException("Unable to set attribute "
						+ attribName + " (available: "
						+ Arrays.toString(getAvailableAttribs(bean)) + ")");
			}
			method.invoke(bean, new Object[] { value });
		} catch (final IllegalArgumentException e) {
			throw createEx(attribName, e);
		} catch (final IllegalAccessException e) {
			throw createEx(attribName, e);
		} catch (final InvocationTargetException e) {
			throw createEx(attribName, e.getTargetException());
		} catch (final IntrospectionException e) {
			throw createEx(attribName, e);
		}
	}

	private RuntimeException createEx(final String attribName, final Throwable t) {
		return new RuntimeException("Error setting " + attribName + ": "
				+ t.getMessage(), t);
	}

	private String[] getAvailableAttribs(final Object bean)
			throws IntrospectionException {
		final PropertyDescriptor[] descriptors = getDescriptors(bean);
		final List<String> result = new ArrayList<String>(descriptors.length);
		for (int i = 0; i < descriptors.length; i++) {
			final Method writeMethod = descriptors[i].getWriteMethod();
			if (writeMethod != null && hasStringArg(writeMethod)) {
				result.add(descriptors[i].getName());
			}
		}
		return result.toArray(new String[result.size()]);
	}

}
