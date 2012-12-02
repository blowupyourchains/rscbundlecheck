package org.dyndns.fichtner.rsccheck.engine.visitors;

import java.text.MessageFormat;

import org.dyndns.fichtner.rsccheck.engine.AbstractRscBundleVisitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;
import org.dyndns.fichtner.rsccheck.engine.visitors.anno.RunnableWithoutConfig;
import org.kohsuke.MetaInfServices;

@RunnableWithoutConfig
@MetaInfServices(Visitor.class)
public class InstantiateMessageFormatVisitor extends AbstractRscBundleVisitor {

	@Override
	public boolean visitBundleKeyValue(RscBundleReader resourcebundle,
			RscBundleContent content, String rscKey, Entry rscEntry) {
		boolean result = super.visitBundleKeyValue(resourcebundle, content,
				rscKey, rscEntry);
		try {
			new MessageFormat(rscEntry.getValue());
		} catch (final Exception e) {
			addError(this, "Unable to create MessageFormat instance "
					+ e.getMessage(), resourcebundle, rscEntry);
		}
		return result;
	}

	public String getName() {
		return "messageformat check";
	}

}
