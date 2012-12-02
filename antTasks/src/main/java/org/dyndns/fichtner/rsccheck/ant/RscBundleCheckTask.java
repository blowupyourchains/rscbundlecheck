package org.dyndns.fichtner.rsccheck.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.dyndns.fichtner.rsccheck.ant.types.Check;
import org.dyndns.fichtner.rsccheck.ant.types.Check.CheckName;
import org.dyndns.fichtner.rsccheck.ant.types.CheckHolder;
import org.dyndns.fichtner.rsccheck.ant.types.Checks;
import org.dyndns.fichtner.rsccheck.engine.Context;
import org.dyndns.fichtner.rsccheck.engine.DefaultContext;
import org.dyndns.fichtner.rsccheck.engine.ErrorEntry;
import org.dyndns.fichtner.rsccheck.engine.RscBundleCheck;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReaderFile;
import org.dyndns.fichtner.rsccheck.engine.StaticVisitorFactory;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.engine.VisitorFactory;

/**
 * Ant Task for checking resourcebundles. All resourcebundles passed by a
 * fileset argument are loaded and checked if <b>every</b> key is found in each
 * resourcebundle. If one ore more keys are <b>not</b> found or if a key occurs
 * multiple times in a resourcebundle a BuildException is thrown. <br>
 * Also the content will be checked if there are no ascii chars above (int) 127.<br>
 * If <code>failOnError</code> is set a {@link BuildException} is thrown on
 * error else the error is logged.
 * 
 * @author Peter Fichtner (fichtner@c2tn.de)
 */
public class RscBundleCheckTask extends Task {

	/** Flag if this task stop the build process if an error was found */
	private boolean failOnError = true;

	/** The resourcebundles to check */
	private final List<FileSet> filesets = new ArrayList<FileSet>();

	/** The visitors */
	private final List<Checks> checks = new ArrayList<Checks>();

	/** sort result (linenumbers) */
	private boolean sortResult = true;

	/** Flag if this task should be verbose or not */
	private boolean verbose;

	private final VisitorFactory visitorFactory = new StaticVisitorFactory();

	private void error(final String message) {
		if (this.failOnError) {
			throw new BuildException(message);
		}
		log(message, Project.MSG_WARN);
	}

	public void execute() throws BuildException {
		if (this.filesets.isEmpty()) {
			throw new BuildException("Specify at least one resourcebundle"); //$NON-NLS-1$
		}

		final List<RscBundleReader> readers = createReaders();
		if (readers.isEmpty()) {
			throw new BuildException("No resourcebundle does match the pattern"); //$NON-NLS-1$
		}
		final Context context = new DefaultContext(readers,
				createVisitors(this.visitorFactory));

		log("Enabled checks: " + context.getVisitors(), Project.MSG_INFO); //$NON-NLS-1$
		final RscBundleCheck rscBundleCheck = new RscBundleCheck(context);
		try {
			final List<ErrorEntry> result = sort(rscBundleCheck.execute());
			for (final ErrorEntry errorEntry : result) {
				log(errorEntry.toString(), Project.MSG_ERR);
			}
			for (final ErrorEntry errorEntry : result) {
				error(errorEntry.toString());
			}
		} catch (final Exception e) {
			error(e.getMessage());
		}
	}

	private List<ErrorEntry> sort(final List<ErrorEntry> list) {
		if (!this.sortResult) {
			return list;
		}
		final List<ErrorEntry> result = new ArrayList<ErrorEntry>(list);
		Collections.sort(result, new Comparator<ErrorEntry>() {
			public int compare(final ErrorEntry arg0, final ErrorEntry arg1) {
				return arg0.getEntry() == null ? (arg1.getEntry() == null ? 0
						: -1) : arg0.getEntry().getLineOfKey()
						- (arg1.getEntry() == null ? 0 : arg1.getEntry()
								.getLineOfKey());
			}

		});
		return result;
	}

	private List<RscBundleReader> createReaders() {
		final List<RscBundleReader> rscBundleReaders = new ArrayList<RscBundleReader>();
		for (int i = 0; i < this.filesets.size(); i++) {
			final FileSet fileSet = this.filesets.get(i);
			final DirectoryScanner ds = fileSet
					.getDirectoryScanner(getProject());
			final File dir = fileSet.getDir(getProject());
			for (final String file : ds.getIncludedFiles()) {
				final File rscBundle = new File(dir, file);
				log("Including " + rscBundle, Project.MSG_INFO); //$NON-NLS-1$
				rscBundleReaders.add(createBundleReader(rscBundle));
			}
		}
		return rscBundleReaders;
	}

	private Collection<Visitor> createVisitors(VisitorFactory visitorFactory) {
		if (this.checks.isEmpty()) {
			final Checks defVisitors = new Checks();
			for (final Visitor visitor : visitorFactory.getDefaultVisitors()) {
				final Check check = new Check();
				check.setName(new CheckName(visitor.getName(), visitorFactory));
				defVisitors.addInclude(check);
			}
			this.checks.add(defVisitors);
		}

		final List<Visitor> visitors = new ArrayList<Visitor>();
		for (final Checks checks : this.checks) {
			visitors.addAll(extractVisitorsFromSet(checks, visitorFactory));
		}
		return visitors;
	}

	private Collection<Visitor> extractVisitorsFromSet(final Checks visitorSet,
			final VisitorFactory visitorFactory) {
		final List<Visitor> result = new ArrayList<Visitor>();
		for (final CheckHolder checkHolder : visitorSet.getData()) {
			result.addAll(checkHolder.getVisitors(visitorFactory));
		}
		return result;
	}

	private RscBundleReaderFile createBundleReader(final File rscBundle) {
		try {
			return new RscBundleReaderFile(rscBundle);
		} catch (final FileNotFoundException e) {
			throw new BuildException(e);
		}
	}

	public void setFailOnError(final boolean failOnError) {
		this.failOnError = failOnError;
	}

	public void addFileset(final FileSet set) {
		this.filesets.add(set);
	}

	public void addChecks(final Checks visitors) {
		this.checks.add(visitors);
	}

	public void setSortResult(final boolean sortResult) {
		this.sortResult = sortResult;
	}

	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

}
