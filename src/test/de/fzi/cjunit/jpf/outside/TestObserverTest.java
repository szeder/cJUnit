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
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;

import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfoDefaultImpl;
import de.fzi.cjunit.jpf.inside.NotifierMethods;
import de.fzi.cjunit.jpf.util.ArgumentCreator;
import de.fzi.cjunit.testexceptions.TestException;


public class TestObserverTest {

	void createAndRunJPF(Class<?> appClass,
			PropertyListenerAdapter propertyListener) {
		String[] jpfArgs = new ArgumentCreator()
				.app(appClass)
				.defaultJPFTestArgs()
				.getArgs();

		Config conf = JPF.createConfig(jpfArgs);
		JPF jpf = new JPF(conf);
		jpf.addPropertyListener(propertyListener);
		jpf.run();
	}

	public static class SucceedingTestClass {
		public static void main(String... args) {
		}
	}

	@Test
	public void succeedingTest() {
		TestObserver to = new TestObserver();
		createAndRunJPF(SucceedingTestClass.class, to);

		assertThat(to.getTestResult(), equalTo(true));
	}

	public static class FailingTestClass {
		public static void main(String... args) {
			try {
				throw new TestException("asdf");
			} catch (Throwable t) {
				NotifierMethods.testFailed(
						new ExceptionInfoDefaultImpl(t));
			}
		}
	}

	@Test
	public void failingTest() {
		TestObserver to = new TestObserver();
		createAndRunJPF(FailingTestClass.class, to);

		assertThat(to.getTestResult(), equalTo(false));
	}

	@Test
	public void getException() throws Throwable {
		TestObserver to = new TestObserver();
		createAndRunJPF(FailingTestClass.class, to);

		Throwable t = to.getException();
		assertThat(t, is(TestException.class));
		assertThat(t.getMessage(), equalTo("asdf"));
	}
}
