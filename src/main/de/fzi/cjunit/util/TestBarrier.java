/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


/**
 * Minimalistic helper class to implement a cyclic barrier for concurrent
 * unit tests.
 * <p>
 * The goal is to provide a very simple synchronization aid that allows all
 * threads of a concurrent test case to wait for each other to reach a
 * common barrier point.  No initialization, no resets, no interruptions, no
 * <code>try { await() } catch (InterruptedException) {}</code> blocks or
 * <code>throws InterruptedException</code> declarations.
 * <p>
 * It is automatically initialized to wait for all threads in a concurrent
 * test case.  Can only be used in concurrent test cases with the
 * <code>threadCount</code> or <code>threadGroup</code> annotations.
 */
public class TestBarrier {

	protected static CyclicBarrier barrier;

	/**
	 * Waits until all threads of a concurrent test case have invoked
	 * <code>await()</code>.
	 */
	public static void await() {
		try {
			barrier.await();
		} catch (InterruptedException e) {
		} catch (BrokenBarrierException e) {
		}
	};
}
