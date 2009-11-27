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

/**
 * Helper class to check whether exclusive access to a code region is
 * ensured, or are concurrent accesses from multiple threads possible.  It
 * should be used in assertions with the <code>accessedExclusively()</code>
 * matcher.
 * <p>
 * In the following example <code>AccessManager.waitUntilAllowed()</code>
 * should ensure that only one thread may execute code regions between
 * <code>waitUntilAllowed()</code> and <code>notifyWaiters()</code>.
 *
 * <pre>
 *     import static de.fzi.concurrentmatchers.Matchers.accessedExclusively;
 *     [...]
 *     &#064;ConcurrentTest
 *     public void testExclusiveAccess() throws Throwable {
 *         final AccessManager am = new AccessManager();
 *         final CriticalSection cs = new CriticalSection();
 *
 *         Thread t = new Thread() {
 *             public void run() {
 *                 am.waitUntilAllowed();
 *                 assertThat(cs, accessedExclusively());
 *                 am.notifyWaiters();
 *             }
 *         };
 *         t.start();
 *
 *         am.waitUntilAllowed();
 *         assertThat(cs, accessedExclusively());
 *         am.notifyWaiters();
 *
 *         t.join();
 *    }
 * </pre>
 *
 * If <code>AccessManager.waitUntilAllowed()</code> contains a bug which in
 * some specific thread interleavings allow more than one thread to continue
 * and execute the critical section, then this test will fail with the
 * exception
 *
 * <pre>
 *    java.lang.AssertionError:
 *    Expected: &lt;no other threads in critical section&gt;
 *         got: &lt;thread 'Thread-0' already in critical section&gt;
 * </pre>
 *
 * because the two assertions can be invoked concurrently.
 */
public class CriticalSection {

	boolean violated;
	protected Thread threadInCS;

	public boolean checkExclusiveAccess() {
		boolean result = enter();
		exit();
		return result;
	}

	protected synchronized boolean enter() {
		if (threadInCS != null) {
			violated = true;
			return false;
		}
		threadInCS = Thread.currentThread();
		return true;
	}

	protected synchronized void exit() {
		if (!violated)
			threadInCS = null;
	}

	@Override
	public synchronized String toString() {
		if (threadInCS != null) {
			return "thread '" + threadInCS.getName()
					+ "' already in critical section";
		} else {
			return "no other threads in critical section";
		}
	}
}
