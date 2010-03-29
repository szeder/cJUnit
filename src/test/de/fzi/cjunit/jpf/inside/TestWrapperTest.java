/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.inside;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static de.fzi.cjunit.jpf.inside.TestWrapperOptions.*;

import de.fzi.cjunit.testutils.*;


public class TestWrapperTest {

	String className = "java.lang.String";
	String methodName = "toString";
	String methodName2 = "hashCode";
	String exceptionName = "java.lang.AssertionError";
	String exceptionName2 = "java.lang.RuntimeException";

	public void throwNothing() { };
	public void throwTestException() {
		throw new TestException();
	}
	public void throwOtherTestException() {
		throw new OtherTestException();
	}


	@Test
	public void testGetRequiredArgumentValueWithArgument() {
		TestWrapper tw = new TestWrapper();
		assertThat(tw.getRequiredArgumentValue("key=value"),
				equalTo("value"));
	}

	@Test(expected=RuntimeException.class)
	public void testGetRequiredArgumentValueWithoutArgument() {
		TestWrapper tw = new TestWrapper();
		tw.getRequiredArgumentValue("key=");
	}

	@Test
	public void testGetArgumentValueWithArgument() {
		TestWrapper tw = new TestWrapper();
		assertThat(tw.getArgumentValue("key=value"), equalTo("value"));
	}

	@Test
	public void testGetArgumentValueWithoutArgument() {
		TestWrapper tw = new TestWrapper();
		assertThat(tw.getArgumentValue("key="), nullValue());
	}

	@Test
	public void parseArgsTestClass() {
		TestWrapper tw = new TestWrapper(new String[] {
				TestClassOpt + className });
		assertThat(tw.testClassName, equalTo(className));
	}

	@Test
	public void parseArgsTest() {
		TestWrapper tw = new TestWrapper(new String[] {
				TestOpt + MethodSubOpt + methodName + ","
					+ ExceptionSubOpt + exceptionName,
				TestOpt + MethodSubOpt + methodName2 + ","
					+ ExceptionSubOpt + exceptionName2
				});
		assertThat("test method list size", tw.testMethods.size(),
				equalTo(2));
		assertThat("method name", tw.testMethods.get(0).getMethodName(),
				equalTo(methodName));
		assertThat("exception name",
				tw.testMethods.get(0).getExpectedExceptionName(),
				equalTo(exceptionName));
		assertThat("method name2", tw.testMethods.get(1).getMethodName(),
				equalTo(methodName2));
		assertThat("exception name2",
				tw.testMethods.get(1).getExpectedExceptionName(),
				equalTo(exceptionName2));
	}

	@Test
	public void parseArgsBeforeMethods() {
		TestWrapper tw = new TestWrapper(new String[] {
				BeforeMethodOpt + "toString",
				BeforeMethodOpt + "hashCode" });
		assertThat("number of before methods",
				tw.beforeMethods.size(), equalTo(2));
		assertThat("first name", tw.beforeMethods.get(0).methodName,
				equalTo("toString"));
		assertThat("second name", tw.beforeMethods.get(1).methodName,
				equalTo("hashCode"));
	}

	@Test
	public void parseArgsAfterMethods() {
		TestWrapper tw = new TestWrapper(new String[] {
				AfterMethodOpt + "wait",
				AfterMethodOpt + "notifyAll" });
		assertThat("number of methods",
				tw.afterMethods.size(), equalTo(2));
		assertThat("first name", tw.afterMethods.get(0).methodName,
				equalTo("wait"));
		assertThat("second name", tw.afterMethods.get(1).methodName,
				equalTo("notifyAll"));
	}

	@Test(expected=RuntimeException.class)
	public void parseArgsWrongArgument() {
		new TestWrapper(new String[] { "asdf" });
	}

	@Test(expected=RuntimeException.class)
	public void parseArgsNoValue() {
		new TestWrapper(new String[] { TestClassOpt });
	}

	@Test(expected=RuntimeException.class)
	public void parseArgsNoArguments() {
		new TestWrapper((String[]) null);
	}

	@Test
	public void createTestObject() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.testClassName = String.class.getName();
		tw.createTestObject();
		assertThat(tw.target, instanceOf(String.class));
	}

	@Test
	public void createTestMethod() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.testClassName = String.class.getName();
		TestMethod tm = new TestMethod();
		tm.setMethodName(methodName);
		tw.testMethods.add(tm);
		TestMethod tm2 = new TestMethod();
		tm2.setMethodName(methodName2);
		tw.testMethods.add(tm2);
		tw.createTestObject();
		tw.createTestMethods();
		assertThat(tw.testMethods.get(0).method, notNullValue());
		assertThat(tw.testMethods.get(1).method, notNullValue());
	}

	@Test
	public void createBeforeMethods() throws Throwable {
		final List<String> createdMethodNames = new ArrayList<String>();
		TestWrapper tw = new TestWrapper();
		for (String name : new String[] { "toString", "hashCode" }) {
			tw.beforeMethods.add(new ReflectiveMethod(name) {
				@Override
				public void createMethod(Object target) {
					createdMethodNames.add(methodName);
				}
			});
		}
		tw.createBeforeMethods();
		assertThat("number of created methods",
				createdMethodNames.size(), equalTo(2));
		assertThat("created method names", createdMethodNames,
				hasItems("toString", "hashCode"));
	}

	@Test
	public void createAfterMethods() throws Throwable {
		final List<String> createdMethodNames = new ArrayList<String>();
		TestWrapper tw = new TestWrapper();
		for (String name : new String[] { "toString", "hashCode" }) {
			tw.afterMethods.add(new ReflectiveMethod(name) {
				@Override
				public void createMethod(Object target) {
					createdMethodNames.add(methodName);
				}
			});
		}
		tw.createAfterMethods();
		assertThat("number of created methods",
				createdMethodNames.size(), equalTo(2));
		assertThat("created method names", createdMethodNames,
				hasItems("toString", "hashCode"));
	}

	@Test
	public void testCreateThreads() {
		TestWrapper tw = new TestWrapper();
		TestMethod tm = new TestMethod();
		tw.testMethods.add(tm);
		TestMethod tm2 = new TestMethod();
		tw.testMethods.add(tm2);
		TestMethod tm3 = new TestMethod();
		tw.testMethods.add(tm3);

		tw.createThreads();

		assertThat("number of created threads", tw.threads.size(),
				equalTo(2));
	}

	@Test
	public void testCheckExceptions() {
		TestWrapper tw = new TestWrapper();
		TestMethod tm = new TestMethod();
		tw.testMethods.add(tm);
		TestMethod tm2 = new TestMethod();
		tm2.exception = new TestException();
		tw.testMethods.add(tm2);
		TestMethod tm3 = new TestMethod();
		tm3.exception = new TestException();
		tw.testMethods.add(tm3);

		tw.checkExceptions();

		assertThat("number of exceptions", tw.errors.size(),
				equalTo(2));
		assertThat("first exception", tw.errors.get(0),
				equalTo(tm2.getException()));
		assertThat("second exception", tw.errors.get(1),
				equalTo(tm3.getException()));
	}

	@Test
	public void testRunTestMethod() throws Throwable {
		final Thread[] threads = new Thread[3];
		TestWrapper tw = new TestWrapper();
		TestMethod tm = new TestMethod() {
			@Override
			public void invoke() {
				threads[0] = Thread.currentThread();
			}
		};
		tw.testMethods.add(tm);
		TestMethod tm2 = new TestMethod() {
			@Override
			public void invoke() {
				threads[1] = Thread.currentThread();
			}
		};
		tw.testMethods.add(tm2);
		TestMethod tm3 = new TestMethod() {
			@Override
			public void invoke() {
				threads[2] = Thread.currentThread();
			}
		};
		tw.testMethods.add(tm3);
		tw.createThreads();

		tw.runTestMethod();

		assertThat(threads[0], equalTo(Thread.currentThread()));
		assertThat(threads[1], equalTo(tw.threads.get(0)));
		assertThat(threads[2], equalTo(tw.threads.get(1)));
	}

	@Test
	public void testRunBeforeMethods() throws Throwable {
		final List<String> invokedMethodNames = new ArrayList<String>();
		TestWrapper tw = new TestWrapper();
		for (String name : new String[] { "toString", "hashCode" }) {
			tw.beforeMethods.add(new ReflectiveMethod(name) {
				@Override
				public void invoke() {
					invokedMethodNames.add(methodName);
				}
			});
		}
		tw.runBeforeMethods();
		assertThat("number of invoked methods",
				invokedMethodNames.size(), equalTo(2));
		assertThat("invoked method names", invokedMethodNames,
				hasItems("toString", "hashCode"));
	}

	@Test(expected=TestException.class)
	public void testExceptionInBeforeMethods() throws Throwable {
		TestWrapper tw = new TestWrapper();
		ReflectiveMethod rm = new ReflectiveMethod() {
			@Override
			public void invoke() throws Throwable {
				throw new TestException();
			}
		};
		tw.beforeMethods.add(rm);

		tw.runBeforeMethods();
	}

	@Test
	public void testMethodIsNotInvokedIfABeforeMethodFails()
			throws Throwable {
		final Counter invocationCounter = new Counter();
		TestWrapper tw = new TestWrapper() {
			protected void runTestMethod() {
				invocationCounter.increment();
			}
		};
		tw.target = this;
		ReflectiveMethod rm = new ReflectiveMethod() {
			@Override
			public void invoke() throws Throwable {
				throw new TestException();
			}
		};
		tw.beforeMethods.add(rm);

		try {
			tw.runTest();
		} catch (TestException te) {}

		assertThat("not invoked", invocationCounter.getValue(),
				equalTo(0));
	}

	@Test
	public void testRunAllAfterMethodsEvenIfOneThrows() throws Throwable {
		final List<String> invokedMethodNames = new ArrayList<String>();
		TestWrapper tw = new TestWrapper();
		tw.target = this;
		for (String name : new String[] { "throwTestException",
				"throwNothing" }) {
			ReflectiveMethod rm = new ReflectiveMethod(name) {
				@Override
				public void invoke() throws
						IllegalArgumentException,
						IllegalAccessException,
						Throwable {
					invokedMethodNames.add(methodName);
					super.invoke();
				}
			};
			rm.createMethod(this);
			tw.afterMethods.add(rm);
		}

		tw.runAfterMethods();

		assertThat("number of invoked methods",
				invokedMethodNames.size(), equalTo(2));
		assertThat("exception-thrower method is invoked first",
				invokedMethodNames.get(0),
				equalTo("throwTestException"));
		assertThat("other method is invoked after", invokedMethodNames,
				hasItem("throwNothing"));
	}

	@Test(expected=TestException.class)
	public void testSingleError() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.errors.add(new TestException());

		tw.handleErrors();
	}

	@Test(expected=Exception.class)
	public void testExceptionOnMultipleError() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.errors.add(new TestException());
		tw.errors.add(new OtherTestException());

		tw.handleErrors();
	}

	@Test(expected=TestException.class)
	public void testFirstErrorOnMultipleError() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.errors.add(new TestException());
		tw.errors.add(new OtherTestException());

		try {
			tw.handleErrors();
		} catch (Exception e) {
			throw e.getCause();
		}
	}

	// this also implicitly tests that @After methods are invoked even if
	// the test method throws an exception
	@Test
	public void testErrorsAreCollectedInOrder() throws Throwable {
		TestWrapper tw = new TestWrapper();
		TestMethod tm = new TestMethod() {
			@Override
			public void invoke() {}
			@Override
			public void checkException() throws Throwable {
				throw new TestException();
			}
		};
		tw.testMethods.add(tm);
		ReflectiveMethod rm = new ReflectiveMethod() {
			@Override
			public void invoke() throws Throwable {
				throw new OtherTestException();
			}
		};
		tw.afterMethods.add(rm);

		try {
			tw.runTest();
		} catch (Exception e) {}

		assertThat("number of exceptions", tw.errors.size(),
				equalTo(2));
		assertThat("test method's exception is the first",
				tw.errors.get(0),
				instanceOf(TestException.class));
		assertThat("@After method's exception is second",
				tw.errors.get(1),
				instanceOf(OtherTestException.class));
	}
}
