package org.dyndns.fichtner.rsccheck.engine.visitors;

import org.dyndns.fichtner.rsccheck.engine.*;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.kohsuke.MetaInfServices;

@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class InvalidCharInValueVistor extends AbstractRscBundleVisitor {

	public boolean visitBundleKeyValue(final RscBundleReader resourcebundle,
			final RscBundleContent content, final String rscKey,
			final Entry rscEntry) {
		final String value = rscEntry.getValue();
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) > 127) {
				addError(this, "Invalid character <" + value.charAt(i) + ">",
						resourcebundle, rscEntry); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return super.visitBundleKeyValue(resourcebundle, content, rscKey,
				rscEntry);
	}

	public String getName() {
		return "invalid char check";
	}
}
