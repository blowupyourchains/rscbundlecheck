package org.dyndns.fichtner.rsccheck.engine;

import static org.dyndns.fichtner.rsccheck.engine.Visitors.purgeExcludedFromDefaults;
import static org.dyndns.fichtner.rsccheck.engine.Visitors.purgeNotRunnableWithoutConfig;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dyndns.fichtner.rsccheck.engine.visitors.AllowedCharKeyVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.CheckUnicodesVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.CrossBundleCheckVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.DuplicateKeyVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.EmptyKeyVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.EmptyValueVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.InstantiateMessageFormatVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.InvalidCharInValueVistor;
import org.dyndns.fichtner.rsccheck.engine.visitors.KeyRegexpVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.LineEndWithVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.PlaceholderVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.UnusedKeyVisitor;
import org.dyndns.fichtner.rsccheck.engine.visitors.UpperLowerVisitor;

/**
 * A VisitorFactory that has hard coded references to the used Visitors. Can be
 * used when using Java5 otherwise {@link Visitor}s should be loaded using
 * {@link java.util.ServiceLoader}:<br>
 * 
 * <pre>
 * for (Iterator&lt;Visitor&gt; iterator = ServiceLoader.load(Visitor.class).iterator(); iterator
 * 		.hasNext();) {
 * 	Visitor visitor = iterator.next();
 * }
 * </pre>
 * 
 * To filter Visitors on their annotations the static methods/fields of
 * {@link Visitors} should be used.
 * 
 * @see Visitors#isNotExcludedFromDefaults
 * @see Visitors#isRunnableWithoutConfig
 * @see Visitors#isNotExcludedFromDefaults(Visitor)
 * @see Visitors#isRunnableWithoutConfig(Visitor)
 * 
 * @author Peter Fichtner
 */
public class StaticVisitorFactory implements VisitorFactory {

	private static final Map<String, Visitor> ALL = Collections
			.unmodifiableMap(createMap());

	public Visitor getByName(final String name) {
		return ALL.get(name);
	}

	private static Map<String, Visitor> createMap() {
		final Map<String, Visitor> result = new HashMap<String, Visitor>();
		for (Visitor visitor : getAllVisitors()) {
			result.put(visitor.getName(), visitor);
		}
		return result;

	}

	private static List<AbstractRscBundleVisitor> getAllVisitors() {
		return Arrays.asList(new AllowedCharKeyVisitor(),
				new CheckUnicodesVisitor(), new CrossBundleCheckVisitor(),
				new DuplicateKeyVisitor(), new EmptyValueVisitor(),
				new EmptyKeyVisitor(), new InvalidCharInValueVistor(),
				new KeyRegexpVisitor(), new LineEndWithVisitor(),
				new PlaceholderVisitor(),
				new InstantiateMessageFormatVisitor(), new UpperLowerVisitor(),
				new UnusedKeyVisitor());
	}

	public Collection<Visitor> getVisitors() {
		return ALL.values();
	}

	public Collection<Visitor> getDefaultVisitors() {
		return purgeExcludedFromDefaults(purgeNotRunnableWithoutConfig(getVisitors()));
	}

}
