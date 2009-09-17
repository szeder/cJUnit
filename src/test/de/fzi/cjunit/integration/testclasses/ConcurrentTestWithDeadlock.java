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


import de.fzi.cjunit.ConcurrentTest;


public class ConcurrentTestWithDeadlock {

	@ConcurrentTest
	public void concurrentTestMethod() throws InterruptedException {
		final Object lock = new Object();
		Thread t = new Thread() {
			@Override
			public void run() {
				synchronized (lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {}
				}
			}
		};
		t.start();
		synchronized (lock) {
			lock.wait();
		}
		t.join();
	}
}
