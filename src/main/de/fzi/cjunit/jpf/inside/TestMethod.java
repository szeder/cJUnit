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

public class TestMethod extends ReflectiveMethod {

	protected String expectedExceptionName;

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setExpectedExceptionName(String exceptionName) {
		this.expectedExceptionName = exceptionName;
	}

	public String getExpectedExceptionName() {
		return expectedExceptionName;
	}

	@Override
	public void invoke() throws IllegalArgumentException,
			IllegalAccessException, Exception, AssertionError,
			Throwable {
		try {
			invokeMethod();
			if (isExpectingException()) {
				throw new AssertionError(
						"Expected exception: " +
						expectedExceptionName);
			}
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
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
}
