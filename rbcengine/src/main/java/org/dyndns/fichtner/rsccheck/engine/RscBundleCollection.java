package org.dyndns.fichtner.rsccheck.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RscBundleCollection {

	private static final long serialVersionUID = 7365640744103408422L;

	private Map<RscBundleReader, RscBundleContent> data = new HashMap<RscBundleReader, RscBundleContent>();

	public void accept(Visitor rscBundleVisitor, Context context) {
		rscBundleVisitor.visitCollection(this, context);
	}

	public void add(RscBundleReader bundleReader,
			RscBundleContent rscBundleContent) {
		this.data.put(bundleReader, rscBundleContent);
	}

	public RscBundleContent getRscBundleContent(RscBundleReader rscBundleReader) {
		return this.data.get(rscBundleReader);
	}

	public Set<RscBundleReader> getReaders() {
		return this.data.keySet();
	}

}
