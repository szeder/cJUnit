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

import static de.fzi.cjunit.jpf.inside.NotifierMethods.*;


public class TestWrapper {

	String testClassName;
	String testMethodName;

	Object target;
	Method method;

	public TestWrapper(String... args) {
		parseArgs(args);
	}

	public void parseArgs(String... args) {
		for (String arg : args) {
			if (arg.startsWith("--testclass=")) {
				testClassName = arg.substring(
						arg.indexOf('=')+1);
			} else if (arg.startsWith("--testmethod=")) {
				testMethodName = arg.substring(
						arg.indexOf('=')+1);
			} else {
				throw new RuntimeException("wrong command " +
						"line parameter: " + arg);
			}
		}
	}

	public void run() throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException, Throwable {
		// The testing framework calling this has already checked
		// that both the class and the method exists
		createTestObject();
		createTestMethod();

		runTest();
	}

	public void runTest() throws IllegalArgumentException,
			IllegalAccessException, Throwable {
		try {
			method.invoke(target);
		} catch (InvocationTargetException e) {
			testFailed();
			throw e.getCause();
		} catch (Throwable t) {
			testFailed();
			throw t;
		}
	}

	public void createTestObject() throws
			IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		target = Class.forName(testClassName)
				.getConstructor()
				.newInstance();
	}

	public void createTestMethod() throws SecurityException,
			NoSuchMethodException {
		method = createMethod(testMethodName);
	}

	public Method createMethod(String methodName) throws SecurityException,
			NoSuchMethodException {
		return target.getClass().getMethod(methodName);
	}

	public static void main(String... args) throws Throwable {
		new TestWrapper(args).run();
	}
}
