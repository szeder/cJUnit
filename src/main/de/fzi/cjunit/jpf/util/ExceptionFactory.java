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

import static de.fzi.cjunit.util.LineSeparator.lineSeparator;

public class ExceptionFactory {

	public Throwable createException(String exceptionClassName,
			String exceptionMessage,
			StackTraceElement[] stackTrace)
			throws ClassNotFoundException, IllegalArgumentException,
				InstantiationException, IllegalAccessException,
				InvocationTargetException {
		Class<?> exceptionClass = Class.forName(exceptionClassName);
		Constructor<?> constructor = getExceptionConstructor(
				exceptionClass);
		if (constructor == null) {
			throw new RuntimeException(
					"exception with no suitable " +
					"constructor thrown inside JPF" +
					lineSeparator +
					"type: " + exceptionClassName +
					lineSeparator +
					"message: " + exceptionMessage);
		}

		Throwable t = (Throwable) constructor.newInstance(
				new Object[] { exceptionMessage });
		t.setStackTrace(stackTrace);
		return t;
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
