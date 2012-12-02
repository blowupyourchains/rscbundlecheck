package org.dyndns.fichtner.rsccheck.engine.visitors;

import org.dyndns.fichtner.rsccheck.engine.*;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.kohsuke.MetaInfServices;

@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class LineEndWithVisitor extends AbstractRscBundleVisitor {

	/**
	 * Characters which one of these values must not end (e.g. whitespace and
	 * tab)
	 */
	private String charactersValuesMustNotEndWith = " \t";

	public boolean visitBundleKeyValue(final RscBundleReader resourcebundle,
			final RscBundleContent content, final String rscKey,
			final Entry rscEntry) {
		if (this.charactersValuesMustNotEndWith != null) {
			for (int i = 0; i < this.charactersValuesMustNotEndWith.length(); i++) {
				if (rscEntry.getValue().endsWith(
						String.valueOf(this.charactersValuesMustNotEndWith
								.charAt(i)))) {
					addError(this, "line ending with non allowed character <"
							+ this.charactersValuesMustNotEndWith.charAt(i)
							+ ">", resourcebundle, rscEntry); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return super.visitBundleKeyValue(resourcebundle, content, rscKey,
				rscEntry);
	}

	public String getCharactersValuesMustNotEndWith() {
		return this.charactersValuesMustNotEndWith;
	}

	public void setCharactersValuesMustNotEndWith(
			final String charactersValuesMustNotEndWith) {
		this.charactersValuesMustNotEndWith = charactersValuesMustNotEndWith;
	}

	public String getName() {
		return "line end check";
	}
}
