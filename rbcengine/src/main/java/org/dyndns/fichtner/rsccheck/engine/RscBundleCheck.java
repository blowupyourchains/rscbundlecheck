package org.dyndns.fichtner.rsccheck.engine;

import java.util.ArrayList;
import java.util.List;

public class RscBundleCheck {

	private final Context context;

	/**
	 * Creates a new RscBundleCheck with the given context. To execute the check
	 * the method {@link #execute()} has to been called.
	 * 
	 * @param context
	 *            the context to use for the check
	 */
	public RscBundleCheck(final Context context) {
		super();
		if (context == null) {
			throw new NullPointerException("Context must not be null");
		}
		this.context = context;
	}

	/**
	 * Executes the check.
	 * 
	 * @return List of ErrorEntries holding problems found in the bundles
	 * @throws Exception
	 *             on execution errors
	 */
	public List<ErrorEntry> execute() throws Exception {
		final RscBundleCollection rscBundles = new RscBundleCollection();
		for (final RscBundleReader bundleReader : this.context.getRscBundleReaders()) {
			rscBundles.add(bundleReader, new RscBundleContent(bundleReader));
		}

		final List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
		for (final Visitor visitor : this.context.getVisitors()) {
			rscBundles.accept(visitor, this.context);
			errors.addAll(visitor.getErrors());
		}
		return errors;
	}

}
