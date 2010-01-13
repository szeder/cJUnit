/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.runners.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import de.fzi.cjunit.ConcurrentTest;
import de.fzi.cjunit.testutils.TestException;


public class ConcurrentFrameworkModelTest {

	static public class TestClass {
		@ConcurrentTest
		public void testMethod() { }
		@ConcurrentTest(expected=TestException.class)
		public void testMethodWithException() { }
		@ConcurrentTest(threadCount=5)
		public void testMethodWithThreadCount() { }
		@ConcurrentTest(threadGroup=6)
		public void testMethodWithThreadGroup() { }
	}

	@Test
	public void testBasicConcurrentTestMethod() throws Throwable {
		ConcurrentFrameworkMethod cfm = new ConcurrentFrameworkMethod(
				TestClass.class.getMethod("testMethod"));

		assertThat(cfm.getExpected(), nullValue());
	}

	@Test
	public void testConcurrentTestMethodWithException() throws Throwable {
		ConcurrentFrameworkMethod cfm = new ConcurrentFrameworkMethod(
				TestClass.class.getMethod(
						"testMethodWithException"));

		assertThat("exception (class name)",
				cfm.getExpected().getName(),
				equalTo(TestException.class.getName()));
	}

	@Test
	public void testConcurrentTestMethodWithThreadCount() throws Throwable {
		ConcurrentFrameworkMethod cfm = new ConcurrentFrameworkMethod(
				TestClass.class.getMethod(
						"testMethodWithThreadCount"));

		assertThat(cfm.getThreadCount(), equalTo(5));
	}

	@Test
	public void testConcurrentTestMethodWithThreadGroup() throws Throwable {
		ConcurrentFrameworkMethod cfm = new ConcurrentFrameworkMethod(
				TestClass.class.getMethod(
						"testMethodWithThreadGroup"));

		assertThat(cfm.getThreadGroup(), equalTo(6));
	}
}
