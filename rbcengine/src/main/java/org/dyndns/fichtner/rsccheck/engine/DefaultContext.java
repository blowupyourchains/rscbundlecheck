package org.dyndns.fichtner.rsccheck.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultContext implements Context {

	private List<RscBundleReader> readers = new ArrayList<RscBundleReader>();
	private List<RscBundleReader> readerView = Collections
			.unmodifiableList(this.readers);

	private List<Visitor> visitors = new ArrayList<Visitor>();
	private List<Visitor> visitorView = Collections
			.unmodifiableList(this.visitors);

	public DefaultContext() {
		super();
	}

	public DefaultContext(Collection<RscBundleReader> readers,
			Collection<Visitor> visitors) {
		this.readers.addAll(readers);
		this.visitors.addAll(visitors);
	}

	public Collection<RscBundleReader> getRscBundleReaders() {
		return this.readerView;
	}

	public Collection<Visitor> getVisitors() {
		return this.visitorView;
	}

	public <T extends Visitor> T getVisitor(final Class<T> clazz) {
		for (final Visitor visitor : this.visitorView) {
			if (clazz.isInstance(visitor)) {
				return clazz.cast(visitor);
			}
		}
		return null;
	}

	// ----------------------------------------------------------

	public void addRscBundleReader(RscBundleReader reader) {
		this.readers.add(reader);
	}

	public void addVisitor(Visitor visitor) {
		this.visitors.add(visitor);
	}

}
