package org.dyndns.fichtner.rsccheck.engine.visitors;

import java.util.Set;

import org.dyndns.fichtner.rsccheck.engine.AbstractRscBundleVisitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.kohsuke.MetaInfServices;

@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class DuplicateKeyVisitor extends AbstractRscBundleVisitor {

	/**
	 * Java differs between keys like MyKey and MYKEY but it's very confusing to
	 * have keys only differing in their upper-/lowercase notation
	 */
	private boolean checkForBloodySimilarKeys;

	public boolean visitBundle(final RscBundleReader resourcebundle,
			final RscBundleContent content) {
		if (this.checkForBloodySimilarKeys) {
			final Set<String> keys = content.getKeys();
			for (final String cmpKey : keys) {
				if (definedBloddySimilar(keys, cmpKey)) {
					addError(
							this,
							"key "
									+ cmpKey
									+ " defined more than once (already declared as "
									+ cmpKey + ")", resourcebundle, content.getEntry(cmpKey)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

		} else {
			for (final String cmpKey : content.getKeys()) {
				final Entry rscEntry = content.getEntry(cmpKey);
				if (rscEntry.getChainedEntry() != null) {
					addError(this, "key " + cmpKey
							+ " defined more than once ("
							+ rscEntry.getChainedEntry() + ")", resourcebundle,
							rscEntry); //$NON-NLS-1$
				}
			}
		}
		return super.visitBundle(resourcebundle, content);
	}

	private boolean definedBloddySimilar(final Set<String> keySet,
			final String cmpKey) {
		for (final String rscKey : keySet) {
			if (!cmpKey.equals(rscKey) && cmpKey.equalsIgnoreCase(rscKey)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCheckForBloodySimilarKeys() {
		return this.checkForBloodySimilarKeys;
	}

	public void setCheckForBloodySimilarKeys(
			final boolean checkForBloodySimilarKeys) {
		this.checkForBloodySimilarKeys = checkForBloodySimilarKeys;
	}

	public String getName() {
		return "duplicate key check";
	}
}
