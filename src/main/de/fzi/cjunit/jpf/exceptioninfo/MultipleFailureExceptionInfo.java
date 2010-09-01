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

import org.junit.internal.runners.model.MultipleFailureException;

import de.fzi.cjunit.jpf.util.ExceptionFactory;


public class MultipleFailureExceptionInfo extends ExceptionInfo {

	protected ExceptionInfo[] failures;

	public MultipleFailureExceptionInfo(String message,
			StackTraceElementInfo[] stackTrace,
			ExceptionInfo cause, ExceptionInfo[] failures) {
		super(MultipleFailureException.class.getName(), message,
				stackTrace, cause);
		this.failures = failures;
	}

	public MultipleFailureExceptionInfo(MultipleFailureException mfe) {
		super(mfe);

		if (mfe.getFailures() == null) {
			failures = new ExceptionInfo[0];
		} else {
			failures = new ExceptionInfo[mfe.getFailures().size()];
			int i = 0;
			for (Throwable t : mfe.getFailures()) {
				failures[i] = new ExceptionInfo(t);
				i++;
			}
		}
	}

	public ExceptionInfo[] getFailures() {
		return failures;
	}

	@Override
	public Throwable reconstruct() throws IllegalArgumentException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return new ExceptionFactory().createMultipleFailureException(
				this);
	}
}
