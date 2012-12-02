package org.dyndns.fichtner.rsccheck.engine.dev.visitors;

import static org.dyndns.fichtner.rsccheck.engine.Visitors.isRunnableWithoutConfig;

import java.lang.reflect.Modifier;

import org.dyndns.fichtner.rsccheck.engine.Visitor;

import de.reflectk.Inspect4J;

public class DumpVisitorNames {

	public static void main(String[] args) throws Exception {
		for (String classname : Inspect4J.findClassnames(Visitor.class
				.getName())) {
			doWork(classname);
		}
	}

	private static void doWork(String classname) throws ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<? extends Visitor> clazz = (Class<? extends Visitor>) Class
				.forName(classname);
		try {
			if (!Modifier.isAbstract(clazz.getModifiers())) {
				Visitor visitor = clazz.newInstance();
				System.out.println(visitor.getName() + ", argless: "
						+ isRunnableWithoutConfig(visitor));
			}
		} catch (InstantiationException e) {
			System.err.println("InstantiationException " + clazz);
		} catch (IllegalAccessException e) {
			System.err.println("IllegalAccessException " + clazz);
		}
	}

}
