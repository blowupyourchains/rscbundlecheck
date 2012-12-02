package org.dyndns.fichtner.rsccheck.engine.visitors;

import java.util.HashMap;
import java.util.Map;

import org.dyndns.fichtner.rsccheck.engine.AbstractRscBundleVisitor;
import org.dyndns.fichtner.rsccheck.engine.Context;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.ExcludeFromDefaults;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.kohsuke.MetaInfServices;

/**
 * Visitor that checks if all messages of a given key start in common with an
 * upper- or a lowercase character. This test isn't very useful since this there
 * are different rules in the languages about that, only useful if the bundles
 * only contain whole sentences. The implementation accesses the
 * {@link PlaceholderVisitor} (if available) and ignores entries that will start
 * with a placeholder character.
 * 
 * @author Peter Fichtner
 */
@ExcludeFromDefaults
@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class UpperLowerVisitor extends AbstractRscBundleVisitor {

	private final Map<String, Character> upperLowerMap = new HashMap<String, Character>();

	public boolean visitBundleKeyValue(final RscBundleReader resourcebundle,
			final RscBundleContent content, final String rscKey,
			final Entry rscEntry) {
		if (rscEntry.getValue().length() != 0) {
			if (!checkUpperLowerCase(this.context, rscKey, rscEntry.getValue())) {
				addError(this, "inconsistent lower/upper case", resourcebundle,
						rscEntry); //$NON-NLS-1$
			}
		}
		return super.visitBundleKeyValue(resourcebundle, content, rscKey,
				rscEntry);
	}

	private boolean checkUpperLowerCase(final Context context,
			final String key, final String value) {
		/** Key key from RSC (String), value first char (Character) */
		final Character character = this.upperLowerMap.get(key);
		final char firstChar = value.charAt(0);
		final PlaceholderVisitor phv = context
				.getVisitor(PlaceholderVisitor.class);
		if (phv == null || phv.getPlaceholderChars() == null
				|| phv.getPlaceholderChars().indexOf(firstChar) < 0) {
			if (character == null) {
				// first read
				this.upperLowerMap.put(key, Character.valueOf(firstChar));
			} else {
				return Character.isLowerCase(firstChar) == Character
						.isLowerCase(character.charValue());
			}
		}
		return true;
	}

	public String getName() {
		return "upper lower check";
	}
}
