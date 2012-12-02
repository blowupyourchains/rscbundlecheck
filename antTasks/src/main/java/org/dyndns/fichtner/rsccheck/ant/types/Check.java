package org.dyndns.fichtner.rsccheck.ant.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.engine.VisitorFactory;

public class Check {

	public static final String ALL_PLACEHOLDER = "*";

	public static class CheckName extends EnumeratedAttribute {

		private final VisitorFactory visitorFactory;

		public CheckName(VisitorFactory visitorFactory) {
			super();
			this.visitorFactory = visitorFactory;
		}

		public CheckName(final String name, VisitorFactory visitorFactory) {
			this.visitorFactory = visitorFactory;
			setValue(name);
		}

		public String getName() {
			return getValue();
		}

		@Override
		public String[] getValues() {
			return getAllNames(this.visitorFactory);
		}

		/**
		 * Returns all available names (including the placeholder
		 * {@value Check#ALL_PLACEHOLDER}).
		 * 
		 * @param visitorFactory
		 *            the factory to get the available {@link Visitor}s from
		 * @return all available names including the all placeholder
		 * @see Check#ALL_PLACEHOLDER
		 */
		public static String[] getAllNames(VisitorFactory visitorFactory) {
			final Collection<Visitor> visitors = visitorFactory.getVisitors();
			final String[] result = new String[visitors.size() + 1];
			result[0] = ALL_PLACEHOLDER;
			int i = 1;
			for (final Visitor visitor : visitors) {
				result[i++] = visitor.getName();
			}
			return result;
		}

	}

	private final List<CheckArguments> arguments = new ArrayList<CheckArguments>();

	private CheckName checkName;

	private String classname;

	public Check() {
		super();
	}

	public CheckName getName() {
		return this.checkName;
	}

	public String getClassname() {
		return this.classname;
	}

	public void setName(final CheckName checkName) {
		if (this.classname != null) {
			throw new IllegalStateException(
					"Do not set both classname and name");
		}
		this.checkName = checkName;
	}

	public void setClassname(String classname) {
		if (this.checkName != null) {
			throw new IllegalStateException(
					"Do not set both classname and name");
		}
		this.classname = classname;
	}

	public void addArguments(final CheckArguments arguments) {
		this.arguments.add(arguments);
	}

	public List<CheckArguments> getArguments() {
		return Collections.unmodifiableList(this.arguments);
	}

	public String toString() {
		return getClass().getName() + " " + this.checkName + " "
				+ this.arguments;
	}

	public boolean isAllPlaceholder() {
		return this.checkName != null
				&& ALL_PLACEHOLDER.equals(this.checkName.getValue());
	}

}