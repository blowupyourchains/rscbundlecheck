package org.dyndns.fichtner.rsccheck.engine;

public interface RscBundleReader {

	public static final RscBundleReader DUMMY = new RscBundleReader() {

		public void fill(final RscBundleContent content) throws Exception {
			throw new UnsupportedOperationException();
		}

		public String getIdentifier() {
			return "all";
		}

	};

	/**
	 * Fill in the content from the underlying source (e.g. a property file)
	 * into the passed RscBundleContent.
	 * 
	 * @param content
	 *            the RscBundleContent to fill
	 * @throws Exception
	 */
	void fill(RscBundleContent content) throws Exception;

	String getIdentifier();

}
