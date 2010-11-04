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
import de.fzi.cjunit.testutils.TestException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ConcurrentTestWithInvocationTargetException {

	public void method() {
		throw new TestException();
	}

	@ConcurrentTest
	public void concurrentTestMethod() throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Method m = this.getClass().getMethod("method");
		m.invoke(this, (Object[]) null);
	}
}
