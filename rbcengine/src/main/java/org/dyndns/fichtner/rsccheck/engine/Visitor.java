package org.dyndns.fichtner.rsccheck.engine;

import java.util.List;

import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;

public interface Visitor {

	boolean visitCollection(RscBundleCollection rscBundleCollection,
			Context context);

	boolean visitBundle(RscBundleReader resourcebundle, RscBundleContent content);

	boolean visitBundleKeyValue(RscBundleReader resourcebundle,
			RscBundleContent content, String rscKey, Entry rscEntry);

	String getName();

	List<ErrorEntry> getErrors();

}
