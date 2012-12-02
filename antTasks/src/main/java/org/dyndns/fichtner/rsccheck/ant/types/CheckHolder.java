package org.dyndns.fichtner.rsccheck.ant.types;

import static org.dyndns.fichtner.rsccheck.engine.Visitors.isRunnableWithoutConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.dyndns.fichtner.rsccheck.ant.types.Check.CheckName;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.engine.VisitorFactory;
import org.dyndns.fichtner.rsccheck.util.BeanInfoAttributeSetter;

public class CheckHolder {

	public enum Mode {
		INCLUDE, EXCLUDE;
	}

	private final Mode mode;
	private final Check check;

	public CheckHolder(final Mode mode, final Check check) {
		this.mode = mode;
		this.check = check;
	}

	public boolean isInclude() {
		return this.mode == Mode.INCLUDE;
	}

	public boolean isExclude() {
		return this.mode == Mode.EXCLUDE;
	}

	public Check getCheck() {
		return this.check;
	}

	public String toString() {
		return getClass().getName() + " " + this.mode + " " + this.check;
	}

	public List<Visitor> getVisitors(VisitorFactory visitorFactory) {
		final List<Visitor> result = new ArrayList<Visitor>();
		if (this.check.isAllPlaceholder()) {
			for (final Visitor visitor : visitorFactory.getDefaultVisitors()) {
				handle(result, visitor);
			}
		} else {
			final Visitor visitorFound;
			if (this.check.getClassname() != null) {
				final Class<?> clazz = loadClass();
				if (!Visitor.class.isAssignableFrom(clazz)) {
					throw new IllegalStateException(clazz.getName()
							+ " not instanceof " + Visitor.class.getName());
				}
				visitorFound = (Visitor) createInstance(getDefaultConstructor(clazz));
			} else if (this.check.getName() != null) {
				final String name = this.check.getName().getName();
				visitorFound = visitorFactory.getByName(name);
				if (visitorFound == null) {
					throw new BuildException("Unknown visitor "
							+ name
							+ " (available "
							+ Arrays.toString(CheckName
									.getAllNames(visitorFactory)) + ")");
				}
			} else {
				throw new IllegalStateException(this.check
						+ " name and classname unset");
			}
			handle(result, visitorFound);
		}
		return result;
	}

	private Class<?> loadClass() {
		final String clazz = this.check.getClassname();
		try {
			return Class.forName(clazz, true, getClass().getClassLoader());
		} catch (final ClassNotFoundException e) {
			throw new IllegalStateException("Class " + clazz + " not found");
		}
	}

	private Object createInstance(final Constructor<?> constructor) {
		try {
			return constructor.newInstance(new Object[0]);
		} catch (final IllegalArgumentException e) {
			throw createEx(constructor, e);
		} catch (final InstantiationException e) {
			throw createEx(constructor, e);
		} catch (final IllegalAccessException e) {
			throw createEx(constructor, e);
		} catch (final InvocationTargetException e) {
			throw createEx(constructor, e);
		}
	}

	private IllegalStateException createEx(final Constructor<?> constructor,
			final Exception e) {
		return new IllegalStateException("Error calling constructor"
				+ constructor, e);
	}

	private Constructor<?> getDefaultConstructor(final Class<?> clazz) {
		try {
			return clazz.getConstructor(new Class[0]);
		} catch (final NoSuchMethodException e) {
			throw new IllegalStateException(clazz
					+ " has no default constructor");
		}
	}

	private void handle(final List<Visitor> result, final Visitor visitorFound) {
		if (isExclude()) {
			handleRemove(result, visitorFound);
		} else {
			handleAdd(result, visitorFound);
		}
	}

	private void handleRemove(final List<Visitor> result,
			final Visitor visitorFound) {
		result.remove(visitorFound);
	}

	private void handleAdd(final List<Visitor> result,
			final Visitor visitorFound) {
		if (this.check.getArguments().isEmpty()
				&& !isRunnableWithoutConfig(visitorFound)) {
			throw new BuildException(this.check.getName()
					+ " cannot be run unconfigured");
		}
		configureVisitor(visitorFound, this.check);
		result.add(visitorFound);
	}

	private void configureVisitor(final Visitor bean, final Check visitorEntry) {
		final BeanInfoAttributeSetter beanInfoAttributeSetter = new BeanInfoAttributeSetter();
		for (final CheckArguments args : visitorEntry.getArguments()) {
			for (final CheckArgument arg : args.getAll()) {
				beanInfoAttributeSetter.setAttribute(bean, arg.getName(),
						arg.getValue());
			}
		}
	}

}
