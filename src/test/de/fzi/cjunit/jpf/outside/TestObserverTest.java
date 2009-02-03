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

import static de.fzi.cjunit.jpf.inside.NotifierMethods.*;
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
			testFailed();
		}
	}

	@Test
	public void failingTest() {
		TestObserver to = new TestObserver();
		createAndRunJPF(FailingTestClass.class, to);

		assertThat(to.getTestResult(), equalTo(false));
	}

	public static class ExceptionThrowingClass {
		public static void main(String... args) throws Throwable {
			throw new TestException("asdf");
		}
	}

	@Test
	public void exceptionInfo() {
		TestObserver to = new TestObserver();
		createAndRunJPF(ExceptionThrowingClass.class, to);

		assertThat(to.exceptionClassName, equalTo(
				TestException.class.getName()));
		assertThat(to.exceptionMessage, equalTo("asdf"));
	}

	@Test
	public void getException() throws Throwable {
		TestObserver to = new TestObserver();
		createAndRunJPF(ExceptionThrowingClass.class, to);

		Throwable t = to.getException();
		assertThat(t, is(TestException.class));
		assertThat(t.getMessage(), equalTo("asdf"));
	}
}
