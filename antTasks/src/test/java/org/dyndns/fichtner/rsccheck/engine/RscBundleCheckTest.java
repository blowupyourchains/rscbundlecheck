package org.dyndns.fichtner.rsccheck.engine;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

public class RscBundleCheckTest extends TestCase {

	public void testNoError() throws Exception {
		assertNoError("\n\n\n\n\n\n\n\n\n\na=b"); //$NON-NLS-1$
		assertNoError("na=b"); //$NON-NLS-1$
		assertNoError("a=b\n\n\n\n\n\n\n\n\n\n"); //$NON-NLS-1$
	}

	private void assertNoError(String string) throws Exception {
		List<ErrorEntry> errors = exec(string);
		assertEquals(errors.toString(), 0, errors.size()); //$NON-NLS-1$
	}

	public void testUmlaut() throws Exception {
		checkMulti("a=ä"); //$NON-NLS-1$
	}

	public void testUnicode() throws Exception {
		checkMulti("a=\\uXYZx"); //$NON-NLS-1$
	}

	public void testEmptyString() throws Exception {
		checkMulti("a="); //$NON-NLS-1$
	}

	private void checkMulti(final String string) throws Exception {
		assertErrorInLine(1, string);

		assertErrorInLine(1, string + "\n\n\n\n\n\n\n\n\n"); //$NON-NLS-1$
		assertErrorInLine(10, "\n\n\n\n\n\n\n\n\n" + string + "\n"); //$NON-NLS-1$ //$NON-NLS-2$

		final String comment = "# comment\n"; //$NON-NLS-1$
		assertErrorInLine(1, string + "\n" + comment + comment + comment //$NON-NLS-1$
				+ comment + comment + comment + comment + comment + comment);
		assertErrorInLine(10, comment + comment + comment + comment + comment
				+ comment + comment + comment + comment + string);

		String commentLF = comment + "\n"; //$NON-NLS-1$
		assertErrorInLine(1, string + "\n" + commentLF + commentLF + commentLF //$NON-NLS-1$
				+ commentLF + commentLF + commentLF + commentLF + commentLF
				+ commentLF);
		assertErrorInLine(19, commentLF + commentLF + commentLF + commentLF
				+ commentLF + commentLF + commentLF + commentLF + commentLF
				+ string);

	}

	private void assertErrorInLine(int lineNum, String rscContent)
			throws Exception {
		final List<ErrorEntry> errors = exec(rscContent);
		assertEquals(1, errors.size());
		assertEquals(lineNum, errors.get(0).getEntry().getLineOfKey());
	}

	private static List<ErrorEntry> exec(final String string) throws Exception {
		return createRscBundleCheck(string).execute();
	}

	private static RscBundleCheck createRscBundleCheck(String string)
			throws Exception {
		DefaultContext context = new DefaultContext();
		context.addRscBundleReader(new RscBundleReaderFile(new StringReader(
				string), "<identifier>"));
		for (Visitor visitor : new StaticVisitorFactory().getDefaultVisitors()) {
			context.addVisitor(visitor);
		}
		return new RscBundleCheck(context);
	}

}
