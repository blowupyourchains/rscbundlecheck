package org.dyndns.fichtner.rsccheck.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RscBundleContent {

	private static final long serialVersionUID = 2288731121064475289L;

	public static class Entry {

		private final String value;

		private final int lineOfKey;

		private Entry chainedEntry;

		public Entry(final String value, final int lineOfKey) {
			this.value = value;
			this.lineOfKey = lineOfKey;
		}

		public String getValue() {
			return this.value;
		}

		public int getLineOfKey() {
			return this.lineOfKey;
		}

		public void chainAppend(final Entry newChainedEntry) {
			if (this.chainedEntry == null) {
				this.chainedEntry = newChainedEntry;
			} else {
				this.chainedEntry.chainAppend(newChainedEntry);
			}
		}

		public Entry getChainedEntry() {
			return this.chainedEntry;
		}

		public String toString() {
			final String string = this.lineOfKey + ":" + this.value; //$NON-NLS-1$
			return this.chainedEntry == null ? string : string
					+ " + " + this.chainedEntry; //$NON-NLS-1$
		}

	}

	private Map<String, RscBundleContent.Entry> data = new HashMap<String, RscBundleContent.Entry>();

	public RscBundleContent() {
		super();
	}

	public Set<String> getKeys() {
		return this.data.keySet();
	}

	public Entry getEntry(String key) {
		return this.data.get(key);
	}

	public RscBundleContent(final RscBundleReader rscBundleReader)
			throws Exception {
		this();
		rscBundleReader.fill(this);
	}

	public Entry add(final String key, final Entry entry) {
		Entry oldValue = this.data.get(key);
		if (oldValue == null) {
			oldValue = this.data.put(key, entry);
		} else {
			oldValue.chainAppend(entry);
		}
		return oldValue;
	}

	public Entry getProperty(final String key) {
		return this.data.get(key);
	}

	public void accept(final RscBundleReader bundleReader, final Visitor visitor) {
		visitor.visitBundle(bundleReader, this);
	}

}
