/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.assumptions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Test;

import de.fzi.cjunit.testutils.JPFForTesting;

import gov.nasa.jpf.Error;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.jvm.NoUncaughtExceptionsProperty;


public class ExceptionInThreadCausesPropertyViolation
		extends JPFForTesting {

	public static class ThrowInThread {
		public static void main(String... args) throws Throwable {
			Thread t = new Thread() {
				@Override
				public void run() {
					throw new AssertionError();
				}
			};
			t.start();
			t.join();
		}
	}
	@Test
	public void exceptionInThreadCausesPropertyViolation() {
		createJPF(ThrowInThread.class);
		Property property = new NoUncaughtExceptionsProperty(config);
		jpf.addSearchProperty(property);
		jpf.run();

		List<Error> errors = jpf.getSearchErrors();
		assertThat("there is an error", errors.size(), equalTo(1));
		assertThat(errors.get(0).getProperty(), equalTo(property));
	}
}
