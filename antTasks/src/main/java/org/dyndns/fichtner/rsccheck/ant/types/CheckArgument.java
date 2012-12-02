package org.dyndns.fichtner.rsccheck.ant.types;

public class CheckArgument {

	private String key;
	private String value;

	public void setName(String name) {
		this.key = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}

	public String toString() {
		return this.key + "=" + this.value;
	}

}
