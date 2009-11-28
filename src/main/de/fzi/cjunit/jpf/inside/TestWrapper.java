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
import java.util.ArrayList;
import java.util.List;

import static de.fzi.cjunit.jpf.inside.TestWrapperOptions.*;

import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfoDefaultImpl;
import de.fzi.cjunit.jpf.inside.NotifierMethods;


public class TestWrapper {

	protected String testClassName;
	protected String testMethodName;
	protected List<String> beforeMethodNames;
	protected List<String> afterMethodNames;
	protected String expectedExceptionName;

	protected Object target;
	protected Method method;
	protected List<Method> beforeMethods;
	protected List<Method> afterMethods;

	protected List<Throwable> errors;

	public TestWrapper(String... args) {
		beforeMethodNames = new ArrayList<String>();
		afterMethodNames = new ArrayList<String>();
		beforeMethods = new ArrayList<Method>();
		afterMethods = new ArrayList<Method>();
		errors = new ArrayList<Throwable>();
		parseArgs(args);
	}

	protected void parseArgs(String... args) {
		if (args == null) {
			throw new RuntimeException("no command line arguments");
		}

		for (String arg : args) {
			if (arg.startsWith(TestClassOpt)) {
				testClassName = getArgumentValue(arg);
			} else if (arg.startsWith(TestOpt)) {
				testMethodName = getArgumentValue(arg);
			} else if (arg.startsWith(BeforeMethodOpt)) {
				beforeMethodNames.add(getArgumentValue(arg));
			} else if (arg.startsWith(AfterMethodOpt)) {
				afterMethodNames.add(getArgumentValue(arg));
			} else if (arg.startsWith(ExpectedExceptionOpt)) {
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
			NotifierMethods.testSucceeded();
		} catch (Throwable t) {
			NotifierMethods.testFailed(
					new ExceptionInfoDefaultImpl(t));
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
		createBeforeMethods();
		createAfterMethods();
	}

	protected void runTest() throws IllegalArgumentException,
			IllegalAccessException, AssertionError, Throwable {
		try {
			runBeforeMethods();
			runTestMethod();
		} catch (Throwable t) {
			errors.add(t);
		}
		runAfterMethods();
		handleErrors();
	}

	protected void runBeforeMethods() throws IllegalArgumentException,
			IllegalAccessException, Throwable {
		for (Method beforeMethod : beforeMethods) {
			invokeMethodUnchainingException(beforeMethod);
		}
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

	protected void runAfterMethods() {
		for (Method afterMethod : afterMethods) {
			try {
				invokeMethodUnchainingException(afterMethod);
			} catch (Throwable t) {
				errors.add(t);
			}
		}
	}

	protected void handleErrors() throws Throwable, Exception {
		if (errors.size() == 1) {
			throw errors.get(0);
		} else if (errors.size() > 1) {
			throw new Exception("Multiple failures during test run;"
					+ " only the first one is reported",
					errors.get(0));
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

	protected void createBeforeMethods() throws SecurityException,
			NoSuchMethodException {
		for (String methodName : beforeMethodNames) {
			beforeMethods.add(createMethod(methodName));
		}
	}

	protected void createAfterMethods() throws SecurityException,
			NoSuchMethodException {
		for (String methodName : afterMethodNames) {
			 afterMethods.add(createMethod(methodName));
		}
	}

	protected Method createMethod(String methodName) throws SecurityException,
			NoSuchMethodException {
		return target.getClass().getMethod(methodName);
	}

	public static void main(String... args) {
		new TestWrapper(args).run();
	}
}
