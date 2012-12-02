package org.dyndns.fichtner.rsccheck.engine.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dyndns.fichtner.rsccheck.engine.AbstractRscBundleVisitor;
import org.dyndns.fichtner.rsccheck.engine.Context;
import org.dyndns.fichtner.rsccheck.engine.RscBundleCollection;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.kohsuke.MetaInfServices;

@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class CrossBundleCheckVisitor extends AbstractRscBundleVisitor {

	public boolean visitCollection(final RscBundleCollection rscBundles,
			final Context context) {
		final boolean result = super.visitCollection(rscBundles, context);
		final Set<String> allKeys = new HashSet<String>();
		for (RscBundleReader rscBundleReader : rscBundles.getReaders()) {
			allKeys.addAll(rscBundles.getRscBundleContent(rscBundleReader)
					.getKeys());
		}

		//		log("Keys found in all resourcebundles: " + allKeys, //$NON-NLS-1$
		// Logadapter.MSG_VERBOSE);

		for (RscBundleReader rscBundleReader : rscBundles.getReaders()) {
			RscBundleContent content = rscBundles
					.getRscBundleContent(rscBundleReader);
			final List<String> tmpAll = new ArrayList<String>(allKeys);
			tmpAll.removeAll(content.getKeys());
			if (!tmpAll.isEmpty()) {
				addError(this,
						"Missing key(s) " + tmpAll, rscBundleReader, null); //$NON-NLS-1$
			}
			//			log("All keys found in " + key, Logadapter.MSG_VERBOSE); //$NON-NLS-1$
		}

		return result;
	}

	public String getName() {
		return "cross bundle check";
	}
}
