package org.dyndns.fichtner.rsccheck.engine.visitors;

import org.dyndns.fichtner.rsccheck.engine.*;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.kohsuke.MetaInfServices;

@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class CheckUnicodesVisitor extends AbstractRscBundleVisitor {

	private String allowedChars;

	public boolean visitBundleKeyValue(final RscBundleReader resourcebundle,
			final RscBundleContent content, final String rscKey,
			final Entry rscEntry) {
		final char[] bytes = rscEntry.getValue().toCharArray();
		for (int i = 0; i < bytes.length - 1; i++) {
			final char c = bytes[i];
			if (c == '\\' && bytes[i + 1] == 'u') {
				if (i + 6 > bytes.length) {
					addError(
							this,
							"Malformed unicode encoding <" //$NON-NLS-1$
									+ new String(bytes, i, Math.min(6,
											bytes.length - i)) + ">",
							resourcebundle, rscEntry); //$NON-NLS-1$
				} else {
					int translatedUnicode = translateUnicode(bytes, i + 2);
					if (translatedUnicode == -1) {
						addError(
								this,
								"Malformed unicode encoding <" //$NON-NLS-1$
										+ new String(bytes, i, Math.min(6,
												bytes.length - i)) + ">", resourcebundle, rscEntry); //$NON-NLS-1$
					}
					String unicodeString = String
							.valueOf((char) translatedUnicode);
					if (this.allowedChars != null
							&& this.allowedChars.indexOf(unicodeString) < 0) {
						addError(
								this,
								"Unicode " //$NON-NLS-1$
										+ new String(bytes, i, Math.min(6,
												bytes.length - i))
										+ " ("
										+ unicodeString + ") not allowed", resourcebundle, rscEntry); //$NON-NLS-1$
					}
				}
			}
		}
		return super.visitBundleKeyValue(resourcebundle, content, rscKey,
				rscEntry);
	}

	private int translateUnicode(final char[] in, final int off) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			final char ch = in[off + i];
			if ("0123456789".indexOf(ch) >= 0) {
				value = (value << 4) + ch - '0';
			} else if ("abcdefABCDEF".indexOf(ch) >= 0) {
				value = (value << 4) + 10 + Character.toLowerCase(ch) - 'a';
			} else {
				return -1;
			}
		}
		return value;
	}

	public void setAllowedChars(final String allowedChars) {
		this.allowedChars = allowedChars;
	}

	public String getName() {
		return "unicode check";
	}
}
