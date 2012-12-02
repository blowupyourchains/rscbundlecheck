package org.dyndns.fichtner.rsccheck.engine;

import java.util.ArrayList;
import java.util.List;

import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;

public abstract class AbstractRscBundleVisitor implements Visitor {

	protected Context context;

	private final List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	public boolean visitCollection(final RscBundleCollection rscBundles,
			final Context context) {
		this.errors.clear();
		this.context = context;
		for (final RscBundleReader rscBundleReader : rscBundles.getReaders()) {
			visitBundle(rscBundleReader, rscBundles
					.getRscBundleContent(rscBundleReader));
		}
		return true;
	}

	public boolean visitBundle(final RscBundleReader resourcebundle,
			final RscBundleContent content) {
		for (final String key : content.getKeys()) {
			if (!visitBundleKeyValue(resourcebundle, content, key, content
					.getEntry(key))) {
				return false;
			}
		}
		return true;
	}

	public boolean visitBundleKeyValue(final RscBundleReader resourcebundle,
			final RscBundleContent content, final String rscKey,
			final Entry rscEntry) {
		return true;
	}

	protected void addError(final Visitor visitor, final String message,
			final RscBundleReader resourcebundle, final Entry entry) {
		// log(message, Logadapter.MSG_WARN);
		this.errors
				.add(new ErrorEntry(visitor, resourcebundle, entry, message));
	}

	public List<ErrorEntry> getErrors() {
		return this.errors;
	}

	public String toString() {
		return getName();
	}
}
