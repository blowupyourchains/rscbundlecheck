package org.dyndns.fichtner.rsccheck.engine.visitors;

import java.util.HashSet;
import java.util.Set;

import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.kohsuke.MetaInfServices;

// TODO Add another PlaceholderVisitor that counts "%s" "%d" etc.
@MetaInfServices(Visitor.class)
public class PlaceholderVisitor extends AbstractRefActCmpVisitor<Set<String>> {

	/** Characters surrounding the placeholders. Must be declared pairwise */
	private String placeholderChars = "{}"; //$NON-NLS-1$

	@Override
	protected Set<String> calculate(final RscBundleReader resourcebundle,
			final Entry rscEntry, final String key) {
		final Set<String> content = new HashSet<String>();

		final StringBuilder[] segments = new StringBuilder[4];
		for (int i = 0; i < segments.length; i++) {
			segments[i] = new StringBuilder();
		}
		int part = 0;
		int formatNumber = 0;
		boolean inQuote = false;
		int braceStack = 0;
		final String value = rscEntry.getValue();
		for (int i = 0; i < value.length(); i++) {
			final char ch = value.charAt(i);
			if (part == 0) {
				if (ch == '\'') {
					if (i + 1 < value.length() && value.charAt(i + 1) == '\'') {
						segments[part].append(ch);
						i++;
					} else {
						inQuote = !inQuote;
					}
				} else if (ch == '{' && !inQuote) {
					part = 1;
				} else {
					segments[part].append(ch);
				}
			} else if (inQuote) {
				segments[part].append(ch);
				if (ch == '\'') {
					inQuote = false;
				}
			} else {
				switch (ch) {
				case ',':
					if (part < 3)
						part++;
					else
						segments[part].append(ch);
					break;
				case '{':
					braceStack++;
					segments[part].append(ch);
					break;
				case '}':
					if (braceStack == 0) {
						part = 0;
						handleFormat(content, segments);
						formatNumber++;
					} else {
						braceStack--;
						segments[part].append(ch);
					}
					break;
				case '\'':
					inQuote = true;
				default:
					segments[part].append(ch);
					break;
				}
			}
		}
		if (braceStack == 0 && part != 0) {
			addError(this, "Unmatched braces", resourcebundle, rscEntry);
		}
		return content;
	}

	private void handleFormat(final Set<String> content,
			final StringBuilder[] segments) {
		final StringBuilder buffer = new StringBuilder();
		for (int i = 1; i < segments.length; i++) {
			buffer.append(segments[i]);
		}
		content.add(buffer.toString());
		segments[1].setLength(0);
		segments[2].setLength(0);
		segments[3].setLength(0);
	}

	public String getPlaceholderChars() {
		return this.placeholderChars;
	}

	public void setPlaceholderChars(final String placeholderChars) {
		if (placeholderChars == null || placeholderChars.length() == 0) {
			throw new IllegalArgumentException(
					"placeholder must not be null or empty"); //$NON-NLS-1$
		}
		if (placeholderChars.length() % 2 != 0) {
			throw new IllegalArgumentException(
					"content of " //$NON-NLS-1$
							+ placeholderChars
							+ "must contain an EVEN count of characters (e.g. \"{}()\")"); //$NON-NLS-1$
		}
		this.placeholderChars = placeholderChars;
	}

	public String getName() {
		return "placeholder check";
	}

	@Override
	protected String getErrorText() {
		return "placeholder";
	}
}
