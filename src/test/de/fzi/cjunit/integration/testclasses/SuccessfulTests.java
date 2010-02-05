/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.integration.testclasses;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import de.fzi.cjunit.ConcurrentTest;
import de.fzi.cjunit.testutils.Counter;
import de.fzi.cjunit.testutils.TestException;
import de.fzi.cjunit.util.TestBarrier;


public class SuccessfulTests {

	final int TGID_Basic = 1;
	final int TGID_WithException = 2;

	public static boolean invoked = false;
	Counter counter = new Counter();

	@Test
	public void testMethod() {
		invoked = true;
	}

	@ConcurrentTest
	public void concurrentTestMethod() throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				synchronized (counter) {
					counter.increment();
				}
			}
		};
		t.start();
		synchronized (counter) {
			counter.increment();
		}
		t.join();
		assertThat(counter.getValue(), equalTo(2));
	}

	@ConcurrentTest(expected=TestException.class)
	public void concurrentTestMethodWithExpectedException()
			throws InterruptedException, TestException {
		concurrentTestMethod();
		throw new TestException();
	}

	@ConcurrentTest(threadCount=2)
	public void concurrentTestWithThreadCount() {
		synchronized (this) {
			counter.increment();
		}
		TestBarrier.await();
		assertThat(counter.getValue(), equalTo(2));
	}

	@ConcurrentTest(threadCount=2,expected=TestException.class)
	public void concurrentTestWithThreadCountAndException()
			throws Throwable {
		throw new TestException();
	}

	@ConcurrentTest(threadGroup=TGID_Basic)
	public void concurrentTestWithThreadGroup1() {
		TestBarrier.await();
		assertThat(counter.getValue(), equalTo(1));
	}

	@ConcurrentTest(threadGroup=TGID_Basic)
	public void concurrentTestWithThreadGroup2() {
		synchronized (this) {
			counter.increment();
		}
		TestBarrier.await();
	}

	@ConcurrentTest(threadGroup=TGID_WithException)
	public void concurrentTestWithThreadGroupAndException1() {
		assertThat(true, equalTo(true));
	}

	@ConcurrentTest(threadGroup=TGID_WithException,
			expected=TestException.class)
	public void concurrentTestWithThreadGroupAndException2()
			throws Throwable {
		throw new TestException();
	}
}
