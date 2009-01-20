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

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.fzi.cjunit.runners.model.ConcurrentFrameworkMethod;

import de.fzi.cjunit.jpf.outside.JPFInvoker;

public class ConcurrentStatement extends Statement {

	private final ConcurrentFrameworkMethod testMethod;
	private Object target;
	private Class<? extends Throwable> expectedExceptionClass;

	public ConcurrentStatement(FrameworkMethod testMethod, Object target) {
		this.testMethod = (ConcurrentFrameworkMethod) testMethod;
		this.target = target;
	}

	@Override
	public void evaluate() throws Throwable {
		invokeJPF();
	}

	void invokeJPF() throws Throwable {
		new JPFInvoker().run(target, testMethod.getMethod(),
				expectedExceptionClass);
	}

	public void expectException(Class<? extends Throwable> expected) {
		expectedExceptionClass = expected;
	}
}
