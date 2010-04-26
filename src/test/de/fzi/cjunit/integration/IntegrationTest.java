/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import de.fzi.cjunit.ConcurrentError;
import de.fzi.cjunit.ConcurrentJUnit;
import de.fzi.cjunit.DeadlockError;
import de.fzi.cjunit.integration.testclasses.ConcurrentTestWithConcurrencyBug;
import de.fzi.cjunit.integration.testclasses.ConcurrentTestWithDeadlock;
import de.fzi.cjunit.integration.testclasses.ConcurrentTestWithExceptionInThread;
import de.fzi.cjunit.integration.testclasses.ConcurrentTestWithSequentialBug;
import de.fzi.cjunit.integration.testclasses.SequentialTestWithFailure;
import de.fzi.cjunit.integration.testclasses.SuccessfulTests;
import de.fzi.cjunit.testutils.OtherTestException;
import de.fzi.cjunit.testutils.TestException;


public class IntegrationTest {

	PrintStream originalSystemOut;

	@Before
	public void disableSystemOut() {
		originalSystemOut = System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream()));
	}

	@After
	public void enableSystemOut() {
		System.setOut(originalSystemOut);
	}

	@Test
	public void successfulTests() {
		Class<?>[] classes = new Class<?>[] { SuccessfulTests.class };
		Result result = ConcurrentJUnit.runClasses(classes);

		assertThat("test method was invoked", SuccessfulTests.invoked,
				is(true));
		assertThat("number of tests", result.getRunCount(), equalTo(7));
		assertThat("number of failed tests", result.getFailureCount(),
				equalTo(0));
	}

	@Test
	public void failingTest() {
		Class<?>[] classes = new Class<?>[] {
				SequentialTestWithFailure.class };
		Result result = ConcurrentJUnit.runClasses(classes);

		assertThat("test method was invoked",
				SequentialTestWithFailure.invoked, is(true));
		assertThat("number of tests", result.getRunCount(), equalTo(1));
		assertThat("number of failed tests", result.getFailureCount(),
				equalTo(1));
		Failure failure = result.getFailures().get(0);
		assertThat("exception's type", failure.getException(),
				instanceOf(TestException.class));
	}

	@Test
	public void concurrentTestWithSequentialBug() {
		Class<?>[] classes = new Class<?>[] {
				ConcurrentTestWithSequentialBug.class };
		Result result = ConcurrentJUnit.runClasses(classes);

		assertThat("number of tests", result.getRunCount(), equalTo(1));
		assertThat("number of failed tests", result.getFailureCount(),
				equalTo(1));
		Failure failure = result.getFailures().get(0);
		assertThat("exception's type", failure.getException(),
				instanceOf(AssertionError.class));
	}

	@Test
	public void concurrentTestWithConcurrencyBug() {
		Class<?>[] classes = new Class<?>[] {
				ConcurrentTestWithConcurrencyBug.class };
		Result result = ConcurrentJUnit.runClasses(classes);

		assertThat("number of tests", result.getRunCount(), equalTo(1));
		assertThat("number of failed tests", result.getFailureCount(),
				equalTo(1));
		Failure failure = result.getFailures().get(0);
		assertThat("exception's type", failure.getException(),
				instanceOf(ConcurrentError.class));
		assertThat("causing exception's type",
				failure.getException().getCause(),
				instanceOf(AssertionError.class));
	}

	@Test
	public void concurrentTestWithDeadlock() {
		Class<?>[] classes = new Class<?>[] {
				ConcurrentTestWithDeadlock.class };
		Result result = ConcurrentJUnit.runClasses(classes);

		assertThat("number of tests", result.getRunCount(), equalTo(1));
		assertThat("number of failed tests", result.getFailureCount(),
				equalTo(1));
		Failure failure = result.getFailures().get(0);
		assertThat("exception's type", failure.getException(),
				instanceOf(DeadlockError.class));
	}

	@Test
	public void concurrentTestWithExceptionInThread() {
		Class<?>[] classes = new Class<?>[] {
				ConcurrentTestWithExceptionInThread.class };
		Result result = ConcurrentJUnit.runClasses(classes);

		assertThat("number of tests", result.getRunCount(), equalTo(1));
		assertThat("number of failures", result.getFailureCount(),
				equalTo(1));
		Throwable exception
				= result.getFailures().get(0).getException();
		assertThat("exception's type", exception,
				instanceOf(TestException.class));
		assertThat("exception's message", exception.getMessage(),
				equalTo(ConcurrentTestWithExceptionInThread.message));

		StackTraceElement[] stackTrace = exception.getStackTrace();
		assertThat("exception's stack trace depth",
				stackTrace.length, equalTo(1));
		assertThat("stack trace[0] classname",
				stackTrace[0].getClassName(),
				equalTo("de.fzi.cjunit.integration.testclasses.ConcurrentTestWithExceptionInThread$1"));
		assertThat("stack trace[0] methodname",
				stackTrace[0].getMethodName(), equalTo("run"));
		assertThat("stack trace[0] filename",
				stackTrace[0].getFileName(),
				equalTo("ConcurrentTestWithExceptionInThread.java"));

		assertThat("has cause", exception.getCause(), notNullValue());
		assertThat("cause's type", exception.getCause(),
				instanceOf(OtherTestException.class));
		assertThat("cause's message", exception.getCause().getMessage(),
				equalTo(ConcurrentTestWithExceptionInThread.causeMessage));

		StackTraceElement[] causeStackTrace
				= exception.getCause().getStackTrace();
		assertThat("cause's stack trace depth",
				causeStackTrace.length, equalTo(2));
		assertThat("cause stack trace[0] classname",
				causeStackTrace[0].getClassName(),
				equalTo("de.fzi.cjunit.integration.testclasses.ConcurrentTestWithExceptionInThread"));
		assertThat("cause stack trace[0] methodname",
				causeStackTrace[0].getMethodName(),
				equalTo("throwCause"));
		assertThat("cause stack trace[0] filename",
				causeStackTrace[0].getFileName(),
				equalTo("ConcurrentTestWithExceptionInThread.java"));
		assertThat("cause stack trace[1] classname",
				causeStackTrace[1].getClassName(),
				equalTo("de.fzi.cjunit.integration.testclasses.ConcurrentTestWithExceptionInThread$1"));
		assertThat("cause stack trace[1] methodname",
				causeStackTrace[1].getMethodName(),
				equalTo("run"));
		assertThat("cause stack trace[1] filename",
				causeStackTrace[1].getFileName(),
				equalTo("ConcurrentTestWithExceptionInThread.java"));
	}
}
