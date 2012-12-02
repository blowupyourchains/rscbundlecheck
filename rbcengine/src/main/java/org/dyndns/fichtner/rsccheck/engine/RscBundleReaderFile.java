// http://eclipse-rbv.cvs.sourceforge.net/viewvc/*checkout*/eclipse-rbv/ml.eclipse.resourcebundlevalidator/src/ml/eclipse/resourcebundlevalidator/utils/ResourceBundleReader.java?revision=1.1

/*
 * $Id: RscBundleReaderFile.java,v 1.1 2012/08/16 09:06:25 fichtner Exp $
 * 
 * Copyright (c) 2005 Marco Lehmann. All rights reserved.
 * 
 */
package org.dyndns.fichtner.rsccheck.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.dyndns.fichtner.rsccheck.engine.RscBundleContent.Entry;

/**
 *
 * Reads a resource bundle. This code is borrowed from the
 * <code>java.util.Properties</code>.
 *
 * @author <a href="mailto:marcolehmann@users.sourceforge.net">Marco Lehmann</a>
 *
 * @version $Revision: 1.1 $
 *
 * @since 0.1.0
 *
 * @see java.util.Properties
 *
 */
public class RscBundleReaderFile implements RscBundleReader {

	private static final String KEY_VALUE_SEPARATORS = "=: \t\r\n\f"; //$NON-NLS-1$
	private static final String STRICT_KEY_VALUE_SEPARATORS = "=:"; //$NON-NLS-1$
	private static final String WHITE_SPACE_CHARS = " \t\r\n\f"; //$NON-NLS-1$

	private final String identifier;
	private BufferedReader reader;

	public RscBundleReaderFile(File rscBundle) throws FileNotFoundException {
		this(rscBundle, rscBundle.toString());
	}

	public RscBundleReaderFile(File rscBundle, String identifier) throws FileNotFoundException {
		this(new FileReader(rscBundle), identifier);
	}

	public RscBundleReaderFile(Reader reader, String identifier) {
		this(new BufferedReader(reader), identifier);
	}

	public RscBundleReaderFile(BufferedReader reader, String identifier) {
		this.reader = reader;
		this.identifier = identifier;
	}

	private void readPropertiesFile(RscBundleContent content)
			throws IOException {

		int lineCount = -1;
		for (; ; ) {

			// Get next line
			String line = this.reader.readLine();
			if (line == null)
				return;

			lineCount++;

			if (line.length() > 0) {

				// Find start of key
				int keyStart = findFirstNonWhiteSpaceChar(line);

				// Blank lines are ignored
				if (keyStart == line.length())
					continue;

				// Continue lines that end in slashes if they are not comments
				char firstChar = line.charAt(keyStart);
				if ((firstChar != '#') && (firstChar != '!')) {
					line = readFullyLineIfNeeded(line);

					// Find separation between key and value
					int separatorIndex;
					for (separatorIndex = keyStart; separatorIndex < line.length(); separatorIndex++) {
						char currentChar = line.charAt(separatorIndex);
						if (currentChar == '\\')
							separatorIndex++;
						else if (KEY_VALUE_SEPARATORS.indexOf(currentChar) != -1)
							break;
					}

					// Skip over whitespace after key if any
					int valueIndex = findFirstNonWhiteSpaceChar(line, separatorIndex);

					// Skip over one non whitespace key value separators if any
					if (valueIndex < line.length())
						if (STRICT_KEY_VALUE_SEPARATORS.indexOf(line.charAt(valueIndex)) != -1)
							valueIndex++;

					// Skip over white space after other separators if any
					valueIndex = findFirstNonWhiteSpaceChar(line, valueIndex);

					String key = line.substring(keyStart, separatorIndex);
					String value = (separatorIndex < line.length()) ? line.substring(valueIndex, line.length()) : "";
					content.add(key, new Entry(value, lineCount + 1));

				}

			}

		}

	}

	private String readFullyLineIfNeeded(String line) throws IOException {

		while (continueLine(line)) {
			String loppedLine = line.substring(0, line.length() - 1);

			String nextLine = reader.readLine();
			if (nextLine == null)
				nextLine = ""; //$NON-NLS-1$

			// Advance beyond whitespace on new line
			int startIndex = findFirstNonWhiteSpaceChar(nextLine);
			nextLine = nextLine.substring(startIndex, nextLine.length());

			line = loppedLine + nextLine;
		}
		return line;

	}

	private int findFirstNonWhiteSpaceChar(String line) {
		return findFirstNonWhiteSpaceChar(line, 0);
	}

	private int findFirstNonWhiteSpaceChar(String line, int beginIndex) {

		int keyStart;
		for (keyStart = beginIndex; keyStart < line.length(); keyStart++)
			if (WHITE_SPACE_CHARS.indexOf(line.charAt(keyStart)) == -1)
				break;
		return keyStart;

	}

	/*
	 * Returns true if the given line is a line that must be appended to the
	 * next line
	 */
	private boolean continueLine(String line) {

		int slashCount = 0;
		int index = line.length() - 1;
		while ((index >= 0) && (line.charAt(index--) == '\\')) {
			slashCount++;
		}
		return (slashCount % 2 == 1);

	}

	public void fill(RscBundleContent content) throws Exception {
		readPropertiesFile(content);
	}

	public String getIdentifier() {
		return this.identifier;
	}

}
