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


public class SuccessfulTests {

	public static boolean invoked = false;

	@Test
	public void testMethod() {
		invoked = true;
	}

	@ConcurrentTest
	public void concurrentTestMethod() throws InterruptedException {
		final Counter counter = new Counter();
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
}
