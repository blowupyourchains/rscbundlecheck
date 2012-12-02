package org.dyndns.fichtner.rsccheck.util;

import java.lang.annotation.Annotation;

public final class Predicates {

	private Predicates() {
		super();
	}

	public static <T> Predicate<T> not(final Predicate<T> delegate) {
		return new Predicate<T>() {
			public boolean apply(T t) {
				return !delegate.apply(t);
			}
		};
	}

	public static <T> Predicate<T> isAnnotated(
			final Class<? extends Annotation> annoClass) {
		return new Predicate<T>() {
			public boolean apply(T t) {
				return t.getClass().isAnnotationPresent(annoClass);
			}
		};
	}

}
