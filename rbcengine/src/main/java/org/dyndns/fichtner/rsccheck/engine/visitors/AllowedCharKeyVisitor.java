package org.dyndns.fichtner.rsccheck.engine.visitors;

import org.dyndns.fichtner.rsccheck.engine.AbstractRscBundleVisitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Visitor.class)
public class AllowedCharKeyVisitor extends AbstractRscBundleVisitor {

	/** String of chars allowed to be used in the key */
	private String allowedKeyChars;

	/** String of characters not allowed to be used in keys */
	private String disallowedKeyChars;

	public boolean visitBundleKeyValue(final RscBundleReader resourcebundle,
			final RscBundleContent content, final String rscKey,
			final Entry rscEntry) {
		final char[] chars = rscKey.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			final char character = chars[i];
			if (this.allowedKeyChars != null
					&& this.allowedKeyChars.indexOf(character) < 0) {
				addError(this, "Key contains character " + character //$NON-NLS-1$
						+ " which was not found in the allowed chars <"
						+ this.allowedKeyChars + ">", resourcebundle, rscEntry); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (this.disallowedKeyChars != null
					&& this.disallowedKeyChars.indexOf(character) >= 0) {
				addError(this, "Key contains character " + character//$NON-NLS-1$
						+ " which was found in the disallowed chars <"
						+ this.disallowedKeyChars + ">", resourcebundle,
						rscEntry);//$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return super.visitBundleKeyValue(resourcebundle, content, rscKey,
				rscEntry);
	}

	public String getAllowedKeyChars() {
		return this.allowedKeyChars;
	}

	public void setAllowedKeyChars(final String allowedKeyChars) {
		this.allowedKeyChars = allowedKeyChars;
	}

	public String getDisallowedKeyChars() {
		return this.disallowedKeyChars;
	}

	public void setDisallowedKeyChars(final String disallowedKeyChars) {
		this.disallowedKeyChars = disallowedKeyChars;
	}

	public String getName() {
		return "allowed char key check";
	}
}
