package org.dyndns.fichtner.rsccheck.engine;

import java.io.StringReader;

import junit.framework.TestCase;

import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;

public class RsBundleContentTest extends TestCase {

	public void test1() throws Exception {
		final RscBundleContent bundleContent = new RscBundleContent(
				new RscBundleReaderFile(new StringReader("foo=bar\n" //$NON-NLS-1$
						+ "bar=foo"), "<identifier>")); //$NON-NLS-1$
		assertEquals(2, bundleContent.getKeys().size());
		check(bundleContent, "foo", 1, "bar"); //$NON-NLS-1$ //$NON-NLS-2$
		check(bundleContent, "bar", 2, "foo"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test2() throws Exception {
		final RscBundleContent bundleContent = new RscBundleContent(
				new RscBundleReaderFile(new StringReader(
						"foo=bar\n\n\n\n\n\n\n\n\n" //$NON-NLS-1$
								+ "bar=foo"), "<identifier>")); //$NON-NLS-1$
		assertEquals(2, bundleContent.getKeys().size());
		check(bundleContent, "foo", 1, "bar"); //$NON-NLS-1$ //$NON-NLS-2$
		check(bundleContent, "bar", 10, "foo"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test3() throws Exception {
		final RscBundleContent bundleContent = new RscBundleContent(
				new RscBundleReaderFile(
						new StringReader(
								"#comment\n" //$NON-NLS-1$
										+ "foo=bar\n" + "# comment\n" + "bar\\\n" + "=foo\n"), "<identifier>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertEquals(2, bundleContent.getKeys().size());
		check(bundleContent, "foo", 2, "bar"); //$NON-NLS-1$ //$NON-NLS-2$
		check(bundleContent, "bar", 4, "foo"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test4() throws Exception {
		final RscBundleContent bundleContent = new RscBundleContent(
				new RscBundleReaderFile(
						new StringReader(
								"     a=         \n      b =          y "), "<identifier>")); //$NON-NLS-1$
		assertEquals(2, bundleContent.getKeys().size());
		check(bundleContent, "a", 1, ""); //$NON-NLS-1$ //$NON-NLS-2$
		check(bundleContent, "b", 2, "y "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test5() throws Exception {
		final RscBundleContent bundleContent = new RscBundleContent(
				new RscBundleReaderFile(
						new StringReader(" a ="), "<identifier>")); //$NON-NLS-1$
		assertEquals(1, bundleContent.getKeys().size());
		check(bundleContent, "a", 1, ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testX() throws Exception {
		final RscBundleContent bundleContent = new RscBundleContent(
				new RscBundleReaderFile(
						new StringReader("foo=\\u00C4"), "<identifier>")); //$NON-NLS-1$
		assertEquals(1, bundleContent.getKeys().size());
		check(bundleContent, "foo", 1, "\\u00C4"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void check(final RscBundleContent bundleContent, final String key,
			final int expectedLine, final String expectedValue) {
		final Entry object = bundleContent.getProperty(key);
		assertEquals(expectedLine, object.getLineOfKey());
		assertEquals(expectedValue, object.getValue());
	}

}
