package org.dyndns.fichtner.rsccheck.engine;

public interface NameBasedFactory<T> {

	T getByName(String name);

}
