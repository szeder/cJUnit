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



public class ExceptionInfoDefaultImpl implements ExceptionInfo {

	String className;
	String message;
	ExceptionInfo cause;
	StackTraceElementInfo[] stackTrace;

	public ExceptionInfoDefaultImpl(String className, String message,
			StackTraceElementInfo[] stackTrace,
			ExceptionInfo cause) {
		this.className = className;
		this.message = message;
		this.stackTrace = stackTrace;
		this.cause = cause;
	}

	public ExceptionInfoDefaultImpl(Throwable t) {
		className = t.getClass().getName();
		message = t.getMessage();
		if (t.getCause() != null) {
			cause = new ExceptionInfoDefaultImpl(t.getCause());
		}

		StackTraceElement[] origStackTrace = t.getStackTrace();
		stackTrace = new StackTraceElementInfo[origStackTrace.length];
		for (int i = 0; i < origStackTrace.length; i++) {
			stackTrace[i] = new StackTraceElementInfo(
					origStackTrace[i]);
		}
	}

	public ExceptionInfoDefaultImpl(ExceptionInfo other) {
		className = other.getClassName();
		message = other.getMessage();
		if (other.hasCause()) {
			cause = new ExceptionInfoDefaultImpl(other.getCause());
		}

		StackTraceElementInfo[] origStackTrace = other.getStackTrace();
		stackTrace = new StackTraceElementInfo[origStackTrace.length];
		for (int i = 0; i < origStackTrace.length; i++) {
			stackTrace[i] = new StackTraceElementInfo(
					origStackTrace[i]);
		}
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public StackTraceElementInfo[] getStackTrace() {
		return stackTrace;
	}

	@Override
	public boolean hasCause() {
		return cause != null;
	}

	@Override
	public ExceptionInfo getCause() {
		return cause;
	}
}
