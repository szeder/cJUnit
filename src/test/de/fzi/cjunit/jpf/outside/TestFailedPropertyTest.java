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
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.ATHROW;
import gov.nasa.jpf.jvm.bytecode.INVOKEVIRTUAL;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;

import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfoDefaultImpl;
import de.fzi.cjunit.jpf.inside.NotifierMethods;
import de.fzi.cjunit.jpf.util.ArgumentCreator;
import de.fzi.cjunit.testutils.Counter;
import de.fzi.cjunit.testutils.TestException;


public class TestFailedPropertyTest {

	@Test
	public void handleInstructionInvokesHandleInvokeInstruction() {
		final Counter invocationCounter = new Counter();
		TestFailedProperty tfp = new TestFailedProperty() {
			@Override
			protected void handleInvokeInstruction(JVM vm,
					InvokeInstruction insn) {
				invocationCounter.increment();
			}
		};
		tfp.handleInstruction(null, new INVOKEVIRTUAL());
		assertThat("handleInvokeInstruction() invoked",
				invocationCounter.getValue(), equalTo(1));
	}

	@Test
	public void handleInstructionDoesNotInvokeHandleInvokeInstruction() {
		final Counter invocationCounter = new Counter();
		TestFailedProperty tfp = new TestFailedProperty() {
			@Override
			protected void handleInvokeInstruction(JVM vm,
					InvokeInstruction insn) {
				invocationCounter.increment();
			}
		};
		tfp.handleInstruction(null, new ATHROW());
		assertThat("handleInvokeInstruction() not invoked",
				invocationCounter.getValue(), equalTo(0));
	}

	@Test
	public void handleMethodInvocationNotFailsWithNullCallee() {
		final Counter invocationCounter = new Counter();
		TestFailedProperty tfp = new TestFailedProperty() {
			@Override
			protected void testFailed(JVM vm) {
				invocationCounter.increment();
			}
		};
		tfp.handleMethodInvocation(null, null);
		assertThat("testFailed() not invoked",
				invocationCounter.getValue(), equalTo(0));
	}

	@Test
	public void handleMethodInvocationWhenNotNotifierMethod() {
		final class NotNotifierMethodInfo extends MethodInfo {
			@Override
			public String getClassName() {
				return "some random invalid class";
			}
		}
		final Counter invocationCounter = new Counter();
		TestFailedProperty tfp = new TestFailedProperty() {
			@Override
			protected void testFailed(JVM vm) {
				invocationCounter.increment();
			}
		};
		tfp.handleMethodInvocation(null, new NotNotifierMethodInfo());
		assertThat("testFailed() not invoked",
				invocationCounter.getValue(), equalTo(0));
	}

	@Test
	public void handleMethodInvocationInvokesTestFailed() {
		final class TestFailedMethodInfo extends MethodInfo {
			@Override
			public String getClassName() {
				return NotifierMethods.class.getName();
			}
			@Override
			public String getName() {
				return "testFailed";
			}
		}
		final Counter invocationCounter = new Counter();
		TestFailedProperty tfp = new TestFailedProperty() {
			@Override
			protected void testFailed(JVM vm) {
				invocationCounter.increment();
			}
		};
		tfp.handleMethodInvocation(null, new TestFailedMethodInfo());
		assertThat("testFailed() invoked", invocationCounter.getValue(),
				equalTo(1));
	}

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
		TestFailedProperty tfp = new TestFailedProperty();
		createAndRunJPF(SucceedingTestClass.class, tfp);

		assertThat(tfp.getTestResult(), equalTo(true));
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
		TestFailedProperty tfp = new TestFailedProperty();
		createAndRunJPF(FailingTestClass.class, tfp);

		assertThat("test result", tfp.getTestResult(), equalTo(false));

		Throwable t = tfp.getException();
		assertThat("exception type", t,
				instanceOf(TestException.class));
		assertThat("exception message", t.getMessage(),
				equalTo("asdf"));
	}

	@Test
	public void reportExceptionDuringCollectingExceptionInfo()
			throws Throwable {
		TestFailedProperty tfp = new TestFailedProperty() {
			@Override
			public ExceptionInfoDefaultImpl collectExceptionInfo(JVM vm)
					throws Exception {
				throw new Exception("exception in TestFailedProperty");
			}
		};
		createAndRunJPF(FailingTestClass.class, tfp);

		assertThat("test result", tfp.getTestResult(), equalTo(false));

		Throwable t = tfp.getException();
		assertThat("exception type", t, instanceOf(Exception.class));
		assertThat("exception message", t.getMessage(),
				equalTo("exception in TestFailedProperty"));
	}

	@Test
	public void searchStartedDoesNotRegisterAtSearch() {
		TestFailedProperty tfp = new TestFailedProperty();
		// Registering at Search means invoking a method on a null
		// reference, which would cause a NPE here.
		tfp.searchStarted(null);
	}
}
