package org.dyndns.fichtner.rsccheck.engine.visitors;

import java.util.regex.Pattern;

import org.dyndns.fichtner.rsccheck.engine.AbstractRscBundleVisitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.kohsuke.MetaInfServices;

@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class KeyRegexpVisitor extends AbstractRscBundleVisitor {

	/** Pattern compiled with allowedKeyRegexp */
	private Pattern pattern;

	public boolean visitBundleKeyValue(final RscBundleReader resourcebundle,
			final RscBundleContent content, final String rscKey,
			final Entry rscEntry) {
		if (this.pattern != null && !this.pattern.matcher(rscKey).find()) {
			addError(this, "Key " + rscKey + " does not match regexp "
					+ this.pattern.pattern(), resourcebundle, rscEntry);//$NON-NLS-1$
		}
		return super.visitBundleKeyValue(resourcebundle, content, rscKey,
				rscEntry);
	}

	public void setAllowedKeyRegexp(final String allowedKeyRegexp) {
		if (allowedKeyRegexp == null || allowedKeyRegexp.length() == 0) {
			throw new IllegalArgumentException("allowedKeyRegexp must not be empty");
		}
		this.pattern = Pattern.compile(allowedKeyRegexp);
	}

	public String getName() {
		return "key regexp check";
	}
}
