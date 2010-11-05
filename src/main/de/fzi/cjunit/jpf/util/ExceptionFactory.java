/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.model.MultipleFailureException;

import static de.fzi.cjunit.internal.util.LineSeparator.lineSeparator;

import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfo;
import de.fzi.cjunit.jpf.exceptioninfo.InvocationTargetExceptionInfo;
import de.fzi.cjunit.jpf.exceptioninfo.MultipleFailureExceptionInfo;


public class ExceptionFactory {

	public Throwable createException(ExceptionInfo exceptionInfo)
			throws ClassNotFoundException, IllegalArgumentException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException,
				NoSuchMethodException {
		if (exceptionInfo == null) {
			return null;
		}

		Throwable cause = null;
		if (exceptionInfo.hasCause()) {
			cause = createException(exceptionInfo.getCause());
		}

		Class<?> exceptionClass = Class.forName(
				exceptionInfo.getClassName());
		Constructor<?> constructor = getExceptionConstructor(
				exceptionClass);
		if (constructor == null) {
			throw new NoSuchMethodException(
					"exception with no suitable " +
					"constructor thrown inside JPF" +
					lineSeparator +
					"type: " +
					exceptionInfo.getClassName() +
					lineSeparator +
					"message: " +
					exceptionInfo.getMessage());
		}

		Throwable t = (Throwable) constructor.newInstance(
				new Object[] { exceptionInfo.getMessage() });
		t.initCause(cause);
		t.setStackTrace(new StackFrameConverter().toStackTraceElementArray(
					exceptionInfo.getStackTrace()));
		return t;
	}

	public MultipleFailureException createMultipleFailureException(
			MultipleFailureExceptionInfo exceptionInfo)
			throws IllegalArgumentException, ClassNotFoundException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException,
				NoSuchMethodException {
		List<Throwable> exceptions = new ArrayList<Throwable>();

		ExceptionInfo[] failures = exceptionInfo.getFailures();

		for (int i = 0; i < failures.length; i++) {
			exceptions.add(createException(failures[i]));
		}

		return new MultipleFailureException(exceptions);
	}

	public InvocationTargetException createInvocationTargetException(
			InvocationTargetExceptionInfo exceptionInfo)
			throws IllegalArgumentException, ClassNotFoundException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException,
				NoSuchMethodException {
		Throwable cause = createException(exceptionInfo.getCause());

		InvocationTargetException ite = new InvocationTargetException(
				cause, exceptionInfo.getMessage());
		ite.setStackTrace(new StackFrameConverter().toStackTraceElementArray(
				exceptionInfo.getStackTrace()));
		return ite;
	}

	protected Constructor<?> getExceptionConstructor(
			Class<?> exceptionClass) {
		Constructor<?> constructor = getCheckedConstructor(
				exceptionClass, String.class);
		if (constructor != null)
			return constructor;
		return getCheckedConstructor(
				exceptionClass, Object.class);
	}

	protected Constructor<?> getCheckedConstructor(
			Class<?> exceptionClass, Class<?> constructorArg) {
		Class<?>[] args = new Class[] { constructorArg };
		try {
			return exceptionClass.getConstructor(args);
		} catch (NoSuchMethodException e) { }
		return null;
	}
}
