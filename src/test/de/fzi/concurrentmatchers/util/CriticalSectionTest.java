/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.concurrentmatchers.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.fzi.cjunit.ConcurrentTest;
import de.fzi.cjunit.runners.ConcurrentRunner;

import de.fzi.concurrentmatchers.util.CriticalSection;


@RunWith(ConcurrentRunner.class)
public class CriticalSectionTest {

	@Test
	public void testEnterIfNotViolated() {
		CriticalSection cs = new CriticalSection();
		assertThat("return value", cs.enter(), equalTo(true));
		assertThat("stored thread", cs.threadInCS,
				equalTo(Thread.currentThread()));
		assertThat("violated", cs.violated, equalTo(false));
	}

	@Test
	public void testEnterIfViolated() {
		CriticalSection cs = new CriticalSection();
		Thread otherThread = new Thread("other");
		cs.threadInCS = otherThread;
		assertThat("return value", cs.enter(), equalTo(false));
		assertThat("stored thread", cs.threadInCS,
				equalTo(otherThread));
		assertThat("violated", cs.violated, equalTo(true));
	}

	@Test
	public void testExitResetsThreadIfNotViolated() {
		CriticalSection cs = new CriticalSection();
		cs.threadInCS = Thread.currentThread();
		cs.exit();
		assertThat(cs.threadInCS, nullValue());
	}

	@Test
	public void testExitDoesNotResetThreadIfViolated() {
		CriticalSection cs = new CriticalSection();
		cs.threadInCS = Thread.currentThread();
		cs.violated = true;
		cs.exit();
		assertThat(cs.threadInCS, equalTo(Thread.currentThread()));
	}

	@Test
	public void testCheckIfViolated() {
		CriticalSection cs = new CriticalSection() {
			@Override
			protected synchronized boolean enter() {
				return true;
			}
		};
		assertThat(cs.checkExclusiveAccess(), equalTo(true));
	}

	@Test
	public void testCheckIfNotViolated() {
		CriticalSection cs = new CriticalSection() {
			@Override
			protected synchronized boolean enter() {
				return false;
			}
		};
		assertThat(cs.checkExclusiveAccess(), equalTo(false));
	}

	@Test
	public void testToString() {
		CriticalSection cs = new CriticalSection();
		assertThat(cs.toString(),
			equalTo("no other threads in critical section"));
	}

	@Test
	public void testToStringWithThreadIn() {
		CriticalSection cs = new CriticalSection();
		cs.threadInCS = new Thread("other");
		assertThat(cs.toString(),
			equalTo("thread 'other' already in critical section"));
	}

	@ConcurrentTest
	public void testThreadIsStillStoredAfterCheckFails() throws Throwable {
		final CriticalSection cs = new CriticalSection();
		final Thread mainThread = Thread.currentThread();
		Thread t = new Thread() {
			@Override
			public void run() {
				if (!cs.checkExclusiveAccess()) {
					assertThat(cs.threadInCS,
							equalTo(mainThread));
				}
			}
		};
		t.start();
		if (!cs.checkExclusiveAccess()) {
			assertThat(cs.threadInCS, equalTo(t));
		}
		t.join();
	}
}
