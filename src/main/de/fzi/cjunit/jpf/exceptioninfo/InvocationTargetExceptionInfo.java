/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.exceptioninfo;

import java.lang.reflect.InvocationTargetException;

import de.fzi.cjunit.jpf.util.ExceptionFactory;


public class InvocationTargetExceptionInfo extends ExceptionInfo {

	public InvocationTargetExceptionInfo(String message,
			StackTraceElementInfo[] stackTrace,
			ExceptionInfo cause) {
		super(InvocationTargetException.class.getName(), message,
				stackTrace, cause);
	}

	public InvocationTargetExceptionInfo(InvocationTargetException ite) {
		super(ite);
	}
}
