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

	private static final String keyValueSeparators = "=: \t\r\n\f"; //$NON-NLS-1$

	private static final String strictKeyValueSeparators = "=:"; //$NON-NLS-1$

	private static final String whiteSpaceChars = " \t\r\n\f"; //$NON-NLS-1$

	private void readPropertiesFile(RscBundleContent content)
			throws IOException {

		int lineCount = -1;
		for (;;) {

			// Get next line
			String line = this.reader.readLine();
			if (line == null)
				return;

			lineCount++;

			if (line.length() > 0) {

				// Find start of key
				int len = line.length();
				int keyStart;
				for (keyStart = 0; keyStart < len; keyStart++)
					if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1)
						break;

				// Blank lines are ignored
				if (keyStart == len)
					continue;

				// Continue lines that end in slashes if they are not comments
				char firstChar = line.charAt(keyStart);
				if ((firstChar != '#') && (firstChar != '!')) {
					while (continueLine(line)) {
						String nextLine = this.reader.readLine();
						if (nextLine == null)
							nextLine = ""; //$NON-NLS-1$
						String loppedLine = line.substring(0, len - 1);
						// Advance beyond whitespace on new line
						int startIndex;
						for (startIndex = 0; startIndex < nextLine.length(); startIndex++)
							if (whiteSpaceChars.indexOf(nextLine
									.charAt(startIndex)) == -1)
								break;
						nextLine = nextLine.substring(startIndex, nextLine
								.length());
						line = new String(loppedLine + nextLine);
						len = line.length();
					}

					// Find separation between key and value
					int separatorIndex;
					for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
						char currentChar = line.charAt(separatorIndex);
						if (currentChar == '\\')
							separatorIndex++;
						else if (keyValueSeparators.indexOf(currentChar) != -1)
							break;
					}

					// Skip over whitespace after key if any
					int valueIndex;
					for (valueIndex = separatorIndex; valueIndex < len; valueIndex++)
						if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
							break;

					// Skip over one non whitespace key value separators if any
					if (valueIndex < len)
						if (strictKeyValueSeparators.indexOf(line
								.charAt(valueIndex)) != -1)
							valueIndex++;

					// Skip over white space after other separators if any
					while (valueIndex < len) {
						if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
							break;
						valueIndex++;
					}
					content.add(line.substring(keyStart, separatorIndex),
							new Entry(((separatorIndex < len) ? line.substring(
									valueIndex, len) : ""), lineCount + 1));

				}

			}

		}

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
