/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.outside;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;

import de.fzi.cjunit.jpf.util.ArgumentCreator;


public class AssumptionsAboutJPF {

	protected Config config;
	protected JPF jpf;

	public static class DoNothing {
		public static void main(String... args) {
		}
	}

	protected void createJPF(Class<?> appClass) {
		String[] jpfArgs = new ArgumentCreator()
				.app(appClass)
				.defaultJPFTestArgs()
				.getArgs();
		config = JPF.createConfig(jpfArgs);
		jpf = new JPF(config);
	}

	class FailingProperty extends GenericProperty {
		@Override
		public boolean check(Search search, JVM vm) {
			return false;
		}
	}

	@Test
	public void multiplePropertiesViolatedAtTheSameTime() {
		createJPF(DoNothing.class);
		jpf.addSearchProperty(new FailingProperty());
		jpf.addSearchProperty(new FailingProperty());
		jpf.run();

		assertThat("only one property violation is reported",
				jpf.getSearchErrors().size(), equalTo(1));
	}
}
