/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.runners.statements;

import org.junit.Test;

import de.fzi.cjunit.testutils.TestException;

public class ConcurrentStatementTest {

	// This also covers the case when the same exception is thrown in the
	// test method as expected: invokeJPF() does not throw in that case.
	@Test
	public void testNotThrowsWhenNotExpecting() throws Throwable {
		ConcurrentStatement s = new ConcurrentStatement(null, null) {
			protected void invokeJPF() throws Throwable { }
		};
		s.evaluate();
	}

	// This test also covers other cases:
	//  * the test does not throw an exception while an exception is
	//    expected
	//  * the test throws a different exception type than expected
	// Both of these cases boil down to the test below: we expect the
	// same exception as the one thrown in invokeJPF().
	@Test(expected=TestException.class)
	public void testThrowsWhenNotExpecting() throws Throwable {
		ConcurrentStatement s = new ConcurrentStatement(null, null) {
			protected void invokeJPF() throws Throwable {
				throw new TestException();
			}
		};
		s.evaluate();
	}
}
