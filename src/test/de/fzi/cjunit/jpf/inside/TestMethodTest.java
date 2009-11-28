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

import de.fzi.cjunit.testutils.*;


public class TestMethodTest {

	@Test(expected=IllegalArgumentException.class)
	public void testInvokeThrows() throws Throwable {
		TestMethod tm = new TestMethod() {
			@Override
			public void invokeMethod() {
				throw new IllegalArgumentException();
			}
		};
		tm.invoke();
	}

	@Test
	public void isExpectedExceptionTrue() throws Throwable {
		TestMethod tm = new TestMethod();
		tm.setExpectedExceptionName(TestException.class.getName());

		assertThat(tm.isExpectedException(new TestException()),
				equalTo(true));
	}

	@Test
	public void isExpectedExceptionFalse() throws Throwable {
		TestMethod tm = new TestMethod();
		tm.setExpectedExceptionName(TestException.class.getName());

		assertThat(tm.isExpectedException(new OtherTestException()),
				equalTo(false));
	}

	@Test
	public void isExpectedExceptionTrueOnSubclass() throws Throwable {
		TestMethod tm = new TestMethod();
		tm.setExpectedExceptionName(
				ParentTestException.class.getName());

		assertThat(tm.isExpectedException(new ChildTestException()),
				equalTo(true));
	}

	@Test(expected=AssertionError.class)
	public void testExpectingExceptionButNoneIsThrown() throws Throwable {
		TestMethod tm = new TestMethod() {
			@Override
			public void invokeMethod() {}
		};
		tm.setExpectedExceptionName(TestException.class.getName());

		tm.invoke();
	}

	@Test
	public void testExpectedExceptionIsCatched() throws Throwable {
		TestMethod tm = new TestMethod() {
			@Override
			public void invokeMethod()
					throws InvocationTargetException {
				throw new InvocationTargetException(
						new TestException());
			}
		};
		tm.setExpectedExceptionName(TestException.class.getName());

		tm.invoke();
	}

	@Test(expected=Exception.class)
	public void testUnexpectedExceptionIsThrown() throws Throwable {
		TestMethod tm = new TestMethod() {
			@Override
			public void invokeMethod()
					throws InvocationTargetException {
				throw new InvocationTargetException(
						new TestException());
			}
		};
		tm.setExpectedExceptionName(OtherTestException.class.getName());

		tm.invoke();
	}
}
