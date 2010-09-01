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

import org.junit.After;

import de.fzi.cjunit.ConcurrentTest;
import de.fzi.cjunit.testutils.Counter;
import de.fzi.cjunit.testutils.TestException;


public class ConcurrentTestWithMultipleFailures {

	@ConcurrentTest
	public void testMethod() throws InterruptedException {
		final Counter counter = new Counter();
		Thread t = new Thread() {
			@Override
			public void run() {
				counter.increment();
			}
		};
		t.start();
		counter.increment();
		t.join();

		// either 1 or 2, never 3 -> always fails, but with different
		// messages
		assertThat(counter.getValue(), equalTo(3));
	}

	@After
	public void afterMethod() {
		throw new TestException("exception thrown from after method");
	}
}
