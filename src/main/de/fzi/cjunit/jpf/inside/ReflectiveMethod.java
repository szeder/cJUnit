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


public class ReflectiveMethod {

	protected String methodName;

	protected Object target;
	protected Method method;

	protected ReflectiveMethod() {}

	public ReflectiveMethod(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void createMethod(Object target) throws SecurityException,
			NoSuchMethodException {
		method = target.getClass().getMethod(methodName);
		this.target = target;
	}

	public void invoke() throws IllegalArgumentException,
			IllegalAccessException, Throwable {
		try {
			invokeMethod();
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	protected void invokeMethod() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		method.invoke(target);
	}
}
