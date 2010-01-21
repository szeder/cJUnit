/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.inside;

import java.lang.reflect.InvocationTargetException;

public class TestMethod extends ReflectiveMethod implements Runnable {

	protected String expectedExceptionName;
	protected Throwable exception;

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setExpectedExceptionName(String exceptionName) {
		this.expectedExceptionName = exceptionName;
	}

	public String getExpectedExceptionName() {
		return expectedExceptionName;
	}

	public Throwable getException() {
		return exception;
	}

	@Override
	public void invoke() {
		try {
			invokeMethod();
			exception = null;
		} catch (Throwable t) {
			exception = t;
		}
	}

	public void checkException() throws ClassNotFoundException,
			AssertionError, Exception, Throwable {
		if (exception == null) {
			if (isExpectingException()) {
				throw new AssertionError(
						"Expected exception: " +
						expectedExceptionName);
			}
		} else if (exception instanceof InvocationTargetException) {
			Throwable cause = exception.getCause();
			if (!isExpectingException()) {
				throw cause;
			}
			if (!isExpectedException(cause)) {
				String message
					= "Unexpected exception, expected<"
					+ expectedExceptionName + "> but was<"
					+ cause.getClass().getName() + ">";
				throw new Exception(message, cause);
			}
		} else {
			throw exception;
		}
	}

	public boolean isExpectingException() {
		return expectedExceptionName != null;
	}

	protected boolean isExpectedException(Throwable t)
			throws ClassNotFoundException {
		return Class.forName(expectedExceptionName)
				.isAssignableFrom(t.getClass());
	}

	@Override
	public void run() {
		invoke();
	}
}
