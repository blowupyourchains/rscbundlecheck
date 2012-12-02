package org.dyndns.fichtner.rsccheck.engine;

import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;

public class ErrorEntry {

	private final RscBundleReader bundleReader;

	private final String message;

	private final Visitor visitor;

	private final Entry entry;

	public ErrorEntry(final Visitor visitor,
			final RscBundleReader bundleReader, final Entry entry,
			final String message) {
		this.visitor = visitor;
		this.bundleReader = bundleReader;
		this.entry = entry;
		this.message = message;
	}

	public Visitor getVisitor() {
		return this.visitor;
	}

	public RscBundleReader getBundleReader() {
		return this.bundleReader;
	}

	public Entry getEntry() {
		return this.entry;
	}

	public String getMessage() {
		return this.message;
	}

	public String toString() {
		return getVisitor().getName() + ": " + getMessage() + " ("
				+ getBundleReader().getIdentifier() + ":" + getEntry() + ")";
	}

}
