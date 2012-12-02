package org.dyndns.fichtner.rsccheck.ant.types;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CheckArguments {

	private final Set<CheckArgument> arguments = new HashSet<CheckArgument>();

	public CheckArguments() {
		super();
	}

	public void addArgument(final CheckArgument argument) {
		this.arguments.add(argument);
	}

	public Collection<CheckArgument> getAll() {
		return Collections.unmodifiableSet(this.arguments);
	}

	public String toString() {
		return getClass().getName() + " " + this.arguments;
	}

}
