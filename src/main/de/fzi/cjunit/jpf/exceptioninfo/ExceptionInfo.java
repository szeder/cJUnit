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


/**
 * Holds basic information about a {@link Throwable}.
 */
public class ExceptionInfo {

	protected String className;
	protected String message;
	protected ExceptionInfo cause;
	protected StackTraceElementInfo[] stackTrace;

	public ExceptionInfo(String className, String message,
			StackTraceElementInfo[] stackTrace,
			ExceptionInfo cause) {
		this.className = className;
		this.message = message;
		this.stackTrace = stackTrace;
		this.cause = cause;
	}

	public ExceptionInfo(Throwable t) {
		className = t.getClass().getName();
		message = t.getMessage();
		if (t.getCause() != null) {
			cause = new ExceptionInfo(t.getCause());
		}

		StackTraceElement[] origStackTrace = t.getStackTrace();
		stackTrace = new StackTraceElementInfo[origStackTrace.length];
		for (int i = 0; i < origStackTrace.length; i++) {
			stackTrace[i] = new StackTraceElementInfo(
					origStackTrace[i]);
		}
	}

	public ExceptionInfo(ExceptionInfo other) {
		className = other.getClassName();
		message = other.getMessage();
		if (other.hasCause()) {
			cause = new ExceptionInfo(other.getCause());
		}

		StackTraceElementInfo[] origStackTrace = other.getStackTrace();
		stackTrace = new StackTraceElementInfo[origStackTrace.length];
		for (int i = 0; i < origStackTrace.length; i++) {
			stackTrace[i] = new StackTraceElementInfo(
					origStackTrace[i]);
		}
	}

	/**
	 * @return	the fully qualified name of the <tt>Class</tt> of
	 *		the throwable this exception info instance holds info
	 *		about.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * See {@link Throwable#getMessage()}.
	 *
	 * @return	the detail message string of the <tt>Throwable</tt>
	 *		instance this exception info instance holds info about.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * See {@link Throwable#getStackTrace()}.
	 *
	 * @return	an array of stack trace element info instances
	 *		representing the stack trace pertaining to this
	 *		throwable.
	 */
	public StackTraceElementInfo[] getStackTrace() {
		return stackTrace;
	}

	/**
	 * See {@link #getCause()}.
	 *
	 * @return	<tt>true</tt> if the throwable has a cause,
	 *		<tt>false</tt> otherwise.
	 */
	public boolean hasCause() {
		return cause != null;
	}

	/**
	 * See {@link Throwable#getCause()}.
	 *
	 * @return	the cause of the throwable this exception info
	 *		instance holds info about.
	 */
	public ExceptionInfo getCause() {
		return cause;
	}
}
