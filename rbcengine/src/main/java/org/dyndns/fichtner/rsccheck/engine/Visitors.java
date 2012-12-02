package org.dyndns.fichtner.rsccheck.engine;

import static org.dyndns.fichtner.rsccheck.util.Collections2.filter;
import static org.dyndns.fichtner.rsccheck.util.Predicates.isAnnotated;
import static org.dyndns.fichtner.rsccheck.util.Predicates.not;

import java.util.Collection;

import org.dyndns.fichtner.rsccheck.engine.visitors.anno.ExcludeFromDefaults;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.dyndns.fichtner.rsccheck.util.Predicate;

public final class Visitors {

	public static final Predicate<Object> isRunnableWithoutConfig = isAnnotated(RunnableWithoutConfig.class);

	public static final Predicate<Object> isNotExcludedFromDefaults = not(isAnnotated(ExcludeFromDefaults.class));

	private Visitors() {
		super();
	}

	// ----------------------------------------------------------------------------

	public static boolean isRunnableWithoutConfig(final Visitor visitor) {
		return isRunnableWithoutConfig.apply(visitor);
	}

	public static boolean isNotExcludedFromDefaults(final Visitor visitor) {
		return isNotExcludedFromDefaults.apply(visitor);
	}

	// ----------------------------------------------------------------------------

	public static Collection<Visitor> purgeExcludedFromDefaults(
			Collection<Visitor> visitors) {
		return filter(visitors, isNotExcludedFromDefaults);
	}

	public static Collection<Visitor> purgeNotRunnableWithoutConfig(
			Collection<Visitor> visitors) {
		return filter(visitors, isRunnableWithoutConfig);
	}

}
