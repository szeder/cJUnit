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
import gov.nasa.jpf.jvm.JVM;

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
	public void failingTest() throws Throwable {
		TestObserver to = new TestObserver();
		createAndRunJPF(FailingTestClass.class, to);

		assertThat("test result", to.getTestResult(), equalTo(false));

		Throwable t = to.getException();
		assertThat("exception type", t, is(TestException.class));
		assertThat("exception message", t.getMessage(),
				equalTo("asdf"));
	}

	@Test
	public void reportExceptionDuringCollectingExceptionInfo()
			throws Throwable {
		TestObserver to = new TestObserver() {
			@Override
			public ExceptionInfoDefaultImpl collectExceptionInfo(JVM vm)
					throws Exception {
				throw new Exception("exception in TestObserver");
			}
		};
		createAndRunJPF(FailingTestClass.class, to);

		assertThat("test result", to.getTestResult(), equalTo(false));

		Throwable t = to.getException();
		assertThat("exception type", t, is(Exception.class));
		assertThat("exception message", t.getMessage(),
				equalTo("exception in TestObserver"));
	}

	public static class TriggerNullCallee {
		public static void main(String... args) {
			Integer integer = null;
			// Yes, the  variable integer can only be null at this
			// point, but it's intentional, because it will cause
			// a null reference returned from
			// InvokeInstruction.getInvokedmethod() in
			// TestObserver.handleInvokeInstruction().
			@SuppressWarnings({"null", "unused"})
			int i = integer+1;
		}
	}

	@Test
	public void handlesNullCallee() {
		TestObserver to = new TestObserver();
		createAndRunJPF(TriggerNullCallee.class, to);
	}
}
