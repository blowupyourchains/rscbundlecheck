package org.dyndns.fichtner.rsccheck.engine;

import java.util.Collection;

public interface Context {

	/**
	 * Returns the configured RscBundleReaders.
	 * 
	 * @return RscBundleReaders
	 */
	Collection<RscBundleReader> getRscBundleReaders();

	/**
	 * Returns the configured Visitors.
	 * 
	 * @return Visitors
	 */
	Collection<Visitor> getVisitors();

	/**
	 * Returns the visitor if available or <code>null</code>.
	 * 
	 * @param clazz
	 *            the visitor searched
	 * @return visitor instance if registered else <code>null</code>
	 */
	<T extends Visitor> T getVisitor(final Class<T> clazz);

}