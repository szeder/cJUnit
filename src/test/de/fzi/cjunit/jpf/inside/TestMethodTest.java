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

	@Test
	public void testInvokedMethodThrowsNothing() throws Throwable {
		TestMethod tm = new TestMethod() {
			@Override
			public void invokeMethod() {}
		};
		tm.invoke();
		assertThat("no exception stored", tm.exception, nullValue());
	}

	@Test
	public void testInvokedMethodThrows() throws Throwable {
		final InvocationTargetException e
				= new InvocationTargetException(
						new TestException());
		TestMethod tm = new TestMethod() {
			@Override
			public void invokeMethod()
					throws InvocationTargetException {
				throw e;
			}
		};
		tm.invoke();
		assertThat("exception stored", tm.exception, notNullValue());
		assertThat(tm.exception, equalTo((Throwable) e));
	}

	@Test
	public void testInvokeThrows() throws Throwable {
		final IllegalArgumentException e
				= new IllegalArgumentException();
		TestMethod tm = new TestMethod() {
			@Override
			public void invokeMethod()
					throws IllegalArgumentException {
				throw e;
			}
		};
		tm.invoke();
		assertThat("exception stored", tm.exception, notNullValue());
		assertThat(tm.exception, equalTo((Throwable) e));
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
		TestMethod tm = new TestMethod();
		tm.setExpectedExceptionName(TestException.class.getName());
		tm.exception = null;

		tm.checkException();
	}

	@Test
	public void testExpectedExceptionIsCaught() throws Throwable {
		TestMethod tm = new TestMethod();
		tm.setExpectedExceptionName(TestException.class.getName());
		tm.exception = new InvocationTargetException(
				new TestException());

		tm.checkException();
	}

	@Test(expected=Exception.class)
	public void testUnexpectedExceptionIsThrown() throws Throwable {
		TestMethod tm = new TestMethod();
		tm.setExpectedExceptionName(OtherTestException.class.getName());
		tm.exception = new InvocationTargetException(
				new TestException());

		tm.checkException();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testExceptionFromInvoke() throws Throwable {
		TestMethod tm = new TestMethod();
		tm.exception = new IllegalArgumentException();

		tm.checkException();
	}
}
