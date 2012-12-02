package org.dyndns.fichtner.rsccheck.engine.visitors;

import java.util.HashMap;
import java.util.Map;

import org.dyndns.fichtner.rsccheck.engine.AbstractRscBundleVisitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;

/**
 * An abstract superclass to compare different attributes of the resourcebundles
 * (e.g. placholder counts, placeholder Strings, linebrak counts, etc.).
 * 
 * @author Peter Fichtner
 * 
 * @param <T>
 *            type of the attribute to be compared
 */
public abstract class AbstractRefActCmpVisitor<T> extends
		AbstractRscBundleVisitor {

	private final Map<String, T> data = new HashMap<String, T>();

	@Override
	public boolean visitBundleKeyValue(RscBundleReader resourcebundle,
			RscBundleContent content, String rscKey, Entry rscEntry) {
		boolean result = super.visitBundleKeyValue(resourcebundle, content,
				rscKey, rscEntry);
		if (rscEntry.getValue().length() != 0
				&& !check(resourcebundle, rscKey, rscEntry)) {
			addError(this, "inconsistent " + getErrorText() + " for key "
					+ rscKey + " (expected " + getReferenz(rscKey)
					+ ", actual " + calculate(resourcebundle, rscEntry, rscKey)
					+ ")", resourcebundle, rscEntry); //$NON-NLS-1$
		}
		return result;
	}

	private boolean check(RscBundleReader resourcebundle, String rscKey,
			Entry rscEntry) {
		boolean ok = true;
		final T act = calculate(resourcebundle, rscEntry, rscKey);
		final T ref = getReferenz(rscKey);
		if (ref == null) {
			// first read
			this.data.put(rscKey, act);
		} else {
			ok = ref.equals(act);
		}
		return ok;
	}

	protected T getReferenz(String rscKey) {
		return this.data.get(rscKey);
	}

	/**
	 * Returns the error text (e.g. "placeholder count").
	 * 
	 * @return error text
	 */
	protected abstract String getErrorText();

	/**
	 * Calculate the value for the passed entry (e.g. placeholder count return 2
	 * for "{0} hash {1}").
	 * 
	 * @return calculated value
	 */
	protected abstract T calculate(final RscBundleReader resourcebundle,
			final Entry rscEntry, final String key);

}
