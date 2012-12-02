package org.dyndns.fichtner.rsccheck.ant.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Checks {

	private final List<CheckHolder> data = new ArrayList<CheckHolder>();
	private final List<CheckHolder> dataView = Collections
			.unmodifiableList(this.data);

	public Checks() {
		super();
	}

	public void addInclude(final Check check) {
		this.data.add(new CheckHolder(CheckHolder.Mode.INCLUDE, check));
	}

	public void addExclude(final Check check) {
		this.data.add(new CheckHolder(CheckHolder.Mode.EXCLUDE, check));
	}

	public List<CheckHolder> getData() {
		return this.dataView;
	}

	public String toString() {
		return getClass().getName() + " data: " + this.data;
	}

}
