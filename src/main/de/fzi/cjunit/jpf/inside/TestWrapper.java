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
import java.lang.reflect.Method;

import de.fzi.cjunit.jpf.inside.NotifierMethods;


public class TestWrapper {

	protected String testClassName;
	protected String testMethodName;
	protected String expectedExceptionName;

	protected Object target;
	protected Method method;

	public TestWrapper(String... args) {
		parseArgs(args);
	}

	protected void parseArgs(String... args) {
		if (args == null) {
			throw new RuntimeException("no command line arguments");
		}

		for (String arg : args) {
			if (arg.startsWith("--testclass=")) {
				testClassName = getArgumentValue(arg);
			} else if (arg.startsWith("--testmethod=")) {
				testMethodName = getArgumentValue(arg);
			} else if (arg.startsWith("--expectedexception=")) {
				expectedExceptionName = getArgumentValue(arg);
			} else {
				throw new RuntimeException("wrong command " +
						"line parameter: " + arg);
			}
		}
	}

	protected String getArgumentValue(String arg) {
		String value = arg.substring(arg.indexOf('=')+1);
		if (value.length() == 0) {
			throw new RuntimeException(
					"wrong command line parameter: " +
					"option without value: " + arg);
		}
		return value;
	}

	protected void run() {
		try {
			createTest();
			runTest();
		} catch (Throwable t) {
			NotifierMethods.testFailed();
		}
	}

	protected void createTest() throws IllegalArgumentException,
			SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		// The testing framework calling this has already checked
		// that both the class and the method exists, so they won't
		// throw any exceptions
		createTestObject();
		createTestMethod();
	}

	protected void runTest() throws IllegalArgumentException,
			IllegalAccessException, AssertionError, Throwable {
		runTestMethod();
	}

	protected void runTestMethod() throws IllegalArgumentException,
			IllegalAccessException, AssertionError, Throwable {
		try {
			invokeTestMethod();
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

	protected void invokeTestMethod() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		method.invoke(target);
	}

	protected void invokeMethodUnchainingException(Method m) throws
			IllegalArgumentException, IllegalAccessException,
			Throwable {
		try {
			m.invoke(target);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	protected boolean isExpectingException() {
		return expectedExceptionName != null;
	}

	protected boolean isExpectedException(Throwable t)
			throws ClassNotFoundException {
		return Class.forName(expectedExceptionName)
				.isAssignableFrom(t.getClass());
	}

	protected void createTestObject() throws
			IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		target = Class.forName(testClassName)
				.getConstructor()
				.newInstance();
	}

	protected void createTestMethod() throws SecurityException,
			NoSuchMethodException {
		method = createMethod(testMethodName);
	}

	protected Method createMethod(String methodName) throws SecurityException,
			NoSuchMethodException {
		return target.getClass().getMethod(methodName);
	}

	public static void main(String... args) {
		new TestWrapper(args).run();
	}
}
