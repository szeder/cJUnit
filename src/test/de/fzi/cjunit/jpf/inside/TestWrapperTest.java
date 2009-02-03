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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.fzi.cjunit.testexceptions.*;


public class TestWrapperTest {

	String className = "java.lang.String";
	String methodName = "toString";
	String exceptionName = "java.lang.AssertionError";

	@Test
	public void parseArgsTestClass() {
		TestWrapper tw = new TestWrapper(new String[] {
				"--testclass=" + className });
		assertThat(tw.testClassName, equalTo(className));
	}

	@Test
	public void parseArgsTesMethod() {
		TestWrapper tw = new TestWrapper(new String[] {
				"--testmethod=" + methodName });
		assertThat(tw.testMethodName, equalTo(methodName));
	}

	@Test
	public void parseArgsExpectedException() {
		TestWrapper tw = new TestWrapper(new String[] {
				"--expectedexception=" + exceptionName });
		assertThat(tw.expectedExceptionName, equalTo(exceptionName));
	}

	@Test(expected=RuntimeException.class)
	public void parseArgsWrongArgument() {
		new TestWrapper(new String[] { "asdf" });
	}

	@Test(expected=RuntimeException.class)
	public void parseArgsNoValue() {
		new TestWrapper(new String[] { "--testclass=" });
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
	public void createMethod() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.testClassName = String.class.getName();
		tw.createTestObject();
		Method m = tw.createMethod(methodName);
		assertThat(m.getName(), equalTo(methodName));
		assertThat(m.getDeclaringClass().getName(), equalTo(className));
	}

	@Test
	public void createTestMethod() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.testClassName = String.class.getName();
		tw.testMethodName = methodName;
		tw.createTestObject();
		tw.createTestMethod();
		assertThat(tw.method.getName(), equalTo(methodName));
		assertThat(tw.method.getDeclaringClass().getName(),
				equalTo(className));
	}

	@Test
	public void isExpectedExceptionTrue() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.expectedExceptionName = TestException.class.getName();
		assertThat(tw.isExpectedException(new TestException()),
				equalTo(true));
	}

	@Test
	public void isExpectedExceptionFalse() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.expectedExceptionName = TestException.class.getName();
		assertThat(tw.isExpectedException(new OtherTestException()),
				equalTo(false));
	}

	@Test
	public void isExpectedExceptionTrueOnSubclass() throws Throwable {
		TestWrapper tw = new TestWrapper();
		tw.expectedExceptionName = ParentTestException.class.getName();
		assertThat(tw.isExpectedException(new ChildTestException()),
				equalTo(true));
	}

	@Test(expected=AssertionError.class)
	public void testExpectingExceptionButNoneIsThrown() throws Throwable {
		TestWrapper tw = new TestWrapper() {
			public void invokeTestMethod() throws
					IllegalArgumentException,
					IllegalAccessException,
					InvocationTargetException {
			}
		};
		tw.expectedExceptionName = TestException.class.getName();

		tw.runTest();
	}

	@Test
	public void testExpectedExceptionIsCatched() throws Throwable {
		TestWrapper tw = new TestWrapper() {
			public void invokeTestMethod() throws
					IllegalArgumentException,
					IllegalAccessException,
					InvocationTargetException {
				throw new InvocationTargetException(
						new TestException());
			}
		};
		tw.expectedExceptionName = TestException.class.getName();

		tw.runTest();
	}

	@Test(expected=Exception.class)
	public void testUnexpectedExceptionIsThrown() throws Throwable {
		TestWrapper tw = new TestWrapper() {
			public void invokeTestMethod() throws
					IllegalArgumentException,
					IllegalAccessException,
					InvocationTargetException {
				throw new InvocationTargetException(
						new OtherTestException());
			}
		};
		tw.expectedExceptionName = TestException.class.getName();

		tw.runTest();
	}
}
