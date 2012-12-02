package org.dyndns.fichtner.rsccheck.engine.visitors;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dyndns.fichtner.rsccheck.engine.AbstractRscBundleVisitor;
import org.dyndns.fichtner.rsccheck.engine.Context;
import org.dyndns.fichtner.rsccheck.engine.RscBundleCollection;
import org.dyndns.fichtner.rsccheck.engine.RscBundleReader;
import org.dyndns.fichtner.rsccheck.engine.Visitor;
import org.dyndns.fichtner.rsccheck.util.Jdkutil;
import org.dyndns.fichtner.rsccheck.util.NlsReferenceCheck;
import org.dyndns.fichtner.rsccheck.util.NlsReferenceCheck.NlsAccess;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Visitor.class)
public class UnusedKeyVisitor extends AbstractRscBundleVisitor {

	private String fqNlsMethodName;
	private String classpath;
	private URL[] cpURLs;

	private boolean searchNotExistingKeys = true;
	private boolean searchUnusedKeys = true;

	@Override
	public boolean visitCollection(final RscBundleCollection rscBundles,
			final Context context) {

		checkValid();

		final boolean visitCollection = super.visitCollection(rscBundles,
				context);
		final Set<String> rscKeys = collectRscKeys(rscBundles);
		final Set<String> nlsKeys = processClasspathEntry(this.classpath);

		if (this.searchNotExistingKeys) {
			searchNonExistingKeys(nlsKeys, rscKeys);
		}
		if (this.searchUnusedKeys) {
			searchUnusedKeys(nlsKeys, rscKeys);
		}

		return visitCollection;
	}

	private Set<String> collectRscKeys(final RscBundleCollection rscBundles) {
		final Set<String> rscKeys = new HashSet<String>();
		for (final RscBundleReader rscBundleReader : rscBundles.getReaders()) {
			rscKeys.addAll(rscBundles.getRscBundleContent(rscBundleReader)
					.getKeys());
		}
		return rscKeys;
	}

	private void checkValid() {
		if (this.classpath == null || this.classpath.length() == 0) {
			throw new IllegalStateException(
					"Classpath must not be null or empty");
		}
		if (this.fqNlsMethodName == null) {
			throw new IllegalStateException("Method must not be null or empty");
		}
	}

	private void searchNonExistingKeys(final Set<String> nlsKeys,
			final Set<String> rscKeys) {
		final List<String> tmpKeys = new ArrayList<String>(nlsKeys);
		tmpKeys.removeAll(rscKeys);
		if (!tmpKeys.isEmpty()) {
			addError(this,
					"Reference(s) to non-exisiting resourcebundle-key(s) "
							+ tmpKeys, RscBundleReader.DUMMY, null); //$NON-NLS-1$
		}
	}

	private void searchUnusedKeys(final Set<String> nlsKeys,
			final Set<String> rscKeys) {
		final List<String> tmpKeys = new ArrayList<String>(rscKeys);
		tmpKeys.removeAll(nlsKeys);
		if (!tmpKeys.isEmpty()) {
			addError(this, "Unused resourcebundle key(s) " + tmpKeys,
					RscBundleReader.DUMMY, null); //$NON-NLS-1$
		}
	}

	private Set<String> processClasspathEntry(final String classpath) {
		final Set<String> result = new HashSet<String>();
		// do a NlsReferenceCheck for each method configured
		for (Member member : loadMembers()) {
			final NlsReferenceCheck nlsCheck = new NlsReferenceCheck(member);
			nlsCheck.check(classpath);
			result.addAll(extractKeys(nlsCheck.getCollectedKeys()));
		}
		return result;
	}

	private Collection<String> extractKeys(final Set<NlsAccess> collectedKeys) {
		final Collection<String> result = new ArrayList<String>();
		for (final NlsAccess nlsAccess : collectedKeys) {
			result.add(nlsAccess.getFirstArg());
		}
		return result;
	}

	private URLClassLoader getClassloader() {
		return new URLClassLoader(this.cpURLs);
	}

	private static URL[] createURLArray(final String cpArg)
			throws MalformedURLException {
		final String[] classpathEntries = cpArg.split(File.pathSeparator);
		final URL[] result = new URL[classpathEntries.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = createFile(classpathEntries[i]).toURI().toURL();
		}
		return result;
	}

	private Member[] loadMembers() {
		final String[] names = this.fqNlsMethodName.split(":"); //$NON-NLS-1$
		final Member[] result = new Member[names.length];
		for (int i = 0; i < names.length; i++) {
			result[i] = names[i].contains("#") ? loadMethod(names[i])
					: loadConstructor(names[i]);
		}
		return result;
	}

	private Constructor<?> loadConstructor(final String fqMethodName) {
		final String[] result = fqMethodName.split("[\\(\\,\\)]"); //$NON-NLS-1$
		if (result.length < 1) {
			throw new IllegalArgumentException(
					"Illegal constructor string " + this.fqNlsMethodName); //$NON-NLS-1$
		}
		final String className = result[0];
		final Class<?> nlsClass = loadClass(className);
		final String[] paramTypes = trim(createArray(result, 1));
		final Constructor<?> findMethod = findConstructor(nlsClass, paramTypes);
		if (findMethod == null) {
			throw new IllegalArgumentException("Constructor ("
					+ Arrays.toString(paramTypes) + ") not found in "
					+ className);
		}
		return findMethod;
	}

	private Method loadMethod(final String fqMethodName) {
		final String[] result = fqMethodName.split("[\\#\\(\\,\\)]"); //$NON-NLS-1$
		if (result.length < 2) {
			throw new IllegalArgumentException(
					"Illegal method string " + this.fqNlsMethodName); //$NON-NLS-1$
		}
		final String className = result[0];
		final Class<?> nlsClass = loadClass(className);

		final String methodName = result[1];
		final String[] paramTypes = trim(createArray(result, 2));
		final Method findMethod = findMethod(nlsClass, paramTypes, methodName);
		if (findMethod == null) {
			throw new IllegalArgumentException("Method " + methodName + "("
					+ Arrays.toString(paramTypes) + ") not found in "
					+ className);
		}
		return findMethod;
	}

	private String[] trim(final String[] strings) {
		final String[] result = new String[strings.length];
		for (int i = 0; i < strings.length; i++) {
			result[i] = strings[i].trim();
		}
		return result;
	}

	private Method findMethod(final Class<?> nlsClass, final String[] params,
			final String methodName) {
		final Method[] methods = nlsClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			final Method method = methods[i];
			if (method.getName().equals(methodName)) {
				if (checkEquals(method.getParameterTypes(), params)) {
					return method;
				}
			}
		}
		return null;
	}

	private Constructor<?> findConstructor(final Class<?> nlsClass,
			final String[] params) {
		final Constructor<?>[] methods = nlsClass.getConstructors();
		for (int i = 0; i < methods.length; i++) {
			final Constructor<?> method = methods[i];
			if (checkEquals(method.getParameterTypes(), params)) {
				return method;
			}
		}
		return null;
	}

	private boolean checkEquals(final Class<?>[] parameterTypes,
			final String[] params) {
		return Arrays.equals(Jdkutil.getExternalNames(parameterTypes), params);
	}

	private static String[] createArray(final String[] strings, final int offset) {
		final String[] result = new String[strings.length - offset];
		System.arraycopy(strings, offset, result, 0, result.length);
		return result;
	}

	public void setNlsMethodName(final String fqNlsMethodName) {
		this.fqNlsMethodName = fqNlsMethodName;
	}

	private Class<?> loadClass(final String className) {
		try {
			return Class.forName(className, false, getClassloader());
		} catch (final ClassNotFoundException e) {
			throw new IllegalStateException("Error loading class " + className,
					e);
		}
	}

	public void setClasspath(final String classpath) {
		try {
			this.cpURLs = createURLArray(classpath);
		} catch (final MalformedURLException e) {
			throw new IllegalArgumentException(
					"Unable to create classloader for " + this.classpath, e);
		}
		this.classpath = classpath;
	}

	private static File createFile(final String directory) {
		final File file = new File(directory);
		if (!file.exists()) {
			throw new RuntimeException(file + " does not exist");
		}
		return file;
	}

	public void setSearchNotExistingKeys(final boolean searchNotExistingKeys) {
		this.searchNotExistingKeys = searchNotExistingKeys;
	}

	public void setSearchUnusedKeys(final boolean searchUnusedKeys) {
		this.searchUnusedKeys = searchUnusedKeys;
	}

	public String getName() {
		return "unused key check";
	}

}
