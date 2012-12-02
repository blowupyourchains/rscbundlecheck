package org.dyndns.fichtner.rsccheck.engine;

import java.util.Collection;

public interface VisitorFactory extends NameBasedFactory<Visitor> {

	/**
	 * Returns all Visitors this factory knows.
	 * 
	 * @return all Visitors this factory knows
	 */
	Collection<Visitor> getVisitors();

	/**
	 * Returns all Visitors that should be used bw default.
	 * 
	 * @return all Visitors that should be used bw default.
	 */
	Collection<Visitor> getDefaultVisitors();

}
