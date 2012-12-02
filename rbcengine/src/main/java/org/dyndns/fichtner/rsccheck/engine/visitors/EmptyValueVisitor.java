package org.dyndns.fichtner.rsccheck.engine.visitors;

import org.dyndns.fichtner.rsccheck.engine.*;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.kohsuke.MetaInfServices;

@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class EmptyValueVisitor extends AbstractRscBundleVisitor {

	public boolean visitBundleKeyValue(RscBundleReader resourcebundle,
			RscBundleContent content, String rscKey, Entry rscEntry) {
		if (rscEntry.getValue().trim().length() == 0) {
			addError(this, "empty value for key " + rscKey, resourcebundle,
					rscEntry); //$NON-NLS-1$
		}
		return super.visitBundleKeyValue(resourcebundle, content, rscKey,
				rscEntry);
	}

	public String getName() {
		return "empty value check";
	}

}
