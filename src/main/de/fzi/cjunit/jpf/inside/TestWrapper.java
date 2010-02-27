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
import java.util.Iterator;
import java.util.List;

import org.junit.internal.runners.model.MultipleFailureException;

import static de.fzi.cjunit.jpf.inside.TestWrapperOptions.*;

import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfo;
import de.fzi.cjunit.jpf.exceptioninfo.MultipleFailureExceptionInfo;
import de.fzi.cjunit.jpf.inside.NotifierMethods;


public class TestWrapper {

	protected String testClassName;
	protected List<TestMethod> testMethods;
	protected List<ReflectiveMethod> beforeMethods;
	protected List<ReflectiveMethod> afterMethods;

	protected Object target;
	protected Method method;
	protected List<Thread> threads;

	protected List<Throwable> errors;

	public TestWrapper(String... args) {
		testMethods = new ArrayList<TestMethod>();
		beforeMethods = new ArrayList<ReflectiveMethod>();
		afterMethods = new ArrayList<ReflectiveMethod>();
		threads = new ArrayList<Thread>();
		errors = new ArrayList<Throwable>();
		parseArgs(args);
	}

	protected void parseArgs(String... args) {
		if (args == null) {
			throw new RuntimeException("no command line arguments");
		}

		for (String arg : args) {
			if (arg.startsWith(TestClassOpt)) {
				testClassName = getRequiredArgumentValue(arg);
			} else if (arg.startsWith(TestOpt)) {
				parseTestOpt(getRequiredArgumentValue(arg));
			} else if (arg.startsWith(BeforeMethodOpt)) {
				beforeMethods.add(new ReflectiveMethod(
						getRequiredArgumentValue(arg)));
			} else if (arg.startsWith(AfterMethodOpt)) {
				afterMethods.add(new ReflectiveMethod(
						getRequiredArgumentValue(arg)));
			} else {
				throw new RuntimeException("wrong command " +
						"line parameter: " + arg);
			}
		}
	}

	protected void parseTestOpt(String arg) {
		TestMethod tm = new TestMethod();
		for (String subopt : arg.split(",")) {
			if (subopt.startsWith(MethodSubOpt)) {
				tm.setMethodName(
						getRequiredArgumentValue(
								subopt));
			} else if (subopt.startsWith(ExceptionSubOpt)) {
				tm.setExpectedExceptionName(
						getArgumentValue(subopt));
			} else {
				throw new RuntimeException(
						"wrong command line parameter");
			}
		}
		testMethods.add(tm);
	}

	protected String getRequiredArgumentValue(String arg) {
		String value = arg.substring(arg.indexOf('=')+1);
		if (value.length() == 0) {
			throw new RuntimeException(
					"wrong command line parameter: " +
					"option without value: " + arg);
		}
		return value;
	}

	protected String getArgumentValue(String arg) {
		String value = arg.substring(arg.indexOf('=')+1);
		if (value.length() == 0) {
			return null;
		}
		return value;
	}

	protected void run() {
		try {
			createTest();
			runTest();
			notifyTestSucceeded();
		} catch (MultipleFailureException mfe) {
			notifyTestFailed(new MultipleFailureExceptionInfo(mfe));
		} catch (Throwable t) {
			notifyTestFailed(new ExceptionInfo(t));
		}
	}

	protected void notifyTestSucceeded() {
		NotifierMethods.testSucceeded();
	}

	protected void notifyTestFailed(ExceptionInfo ei) {
		NotifierMethods.testFailed(ei);
	}

	protected void createTest() throws IllegalArgumentException,
			SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		// The testing framework calling this has already checked
		// that both the class and the method exists, so they won't
		// throw any exceptions
		createTestObject();
		createTestMethods();
		createBeforeMethods();
		createAfterMethods();
		createThreads();
		createTestBarrier();
	}

	protected void createTestBarrier() {
		new TestBarrierInitializator(testMethods.size());
	}

	protected void runTest() throws IllegalArgumentException,
			IllegalAccessException, AssertionError, Throwable {
		try {
			runBeforeMethods();
			runTestMethod();
			checkExceptions();
		} catch (Throwable t) {
			errors.add(t);
		}
		runAfterMethods();
		handleErrors();
	}

	protected void runBeforeMethods() throws IllegalArgumentException,
			IllegalAccessException, Throwable {
		for (ReflectiveMethod beforeMethod : beforeMethods) {
			beforeMethod.invoke();
		}
	}

	protected void runTestMethod() throws IllegalArgumentException,
			IllegalAccessException, InterruptedException {
		for (Thread t : threads) {
			t.start();
		}
		testMethods.get(0).invoke();
		for (Thread t : threads) {
			t.join();
		}
	}

	protected void checkExceptions() {
		for (TestMethod tm : testMethods) {
			try {
				tm.checkException();
			} catch (Throwable t) {
				errors.add(t);
			}
		}
	}

	protected void runAfterMethods() {
		for (ReflectiveMethod afterMethod : afterMethods) {
			try {
				afterMethod.invoke();
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

	protected void createTestObject() throws
			IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		target = Class.forName(testClassName)
				.getConstructor()
				.newInstance();
	}

	protected void createTestMethods() throws SecurityException,
			NoSuchMethodException {
		for (TestMethod tm : testMethods) {
			tm.createMethod(target);
		}
	}

	protected void createBeforeMethods() throws SecurityException,
			NoSuchMethodException {
		for (ReflectiveMethod beforeMethod : beforeMethods) {
			beforeMethod.createMethod(target);
		}
	}

	protected void createAfterMethods() throws SecurityException,
			NoSuchMethodException {
		for (ReflectiveMethod afterMethod : afterMethods) {
			afterMethod.createMethod(target);
		}
	}

	protected void createThreads() {
		Iterator<TestMethod> i = testMethods.iterator();

		if (i.hasNext())
			i.next();

		while (i.hasNext()) {
			TestMethod tm = i.next();
			Thread t = new Thread(tm);
			threads.add(t);
		}
	}

	public static void main(String... args) {
		new TestWrapper(args).run();
	}
}
