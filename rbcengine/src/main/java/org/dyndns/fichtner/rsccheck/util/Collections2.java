package org.dyndns.fichtner.rsccheck.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Collections2 {

	private Collections2() {
		super();
	}

	public static <T> Collection<T> filter(Collection<T> unfiltered,
			Predicate<? super T> predicate) {
		List<T> filtered = new ArrayList<T>();
		for (T t : unfiltered) {
			if (predicate.apply(t)) {
				filtered.add(t);
			}
		}
		return filtered;
	}

}
