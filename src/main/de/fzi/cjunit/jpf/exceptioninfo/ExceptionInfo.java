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
public interface ExceptionInfo {

	/**
	 * @return	the fully qualified name of the <tt>Class</tt> of
	 *		the throwable this exception info instance holds info
	 *		about.
	 */
	public String getClassName();

	/**
	 * See {@link Throwable#getMessage()}.
	 *
	 * @return	the detail message string of the <tt>Throwable</tt>
	 *		instance this exception info instance holds info about.
	 */
	public String getMessage();

	/**
	 * See {@link Throwable#getStackTrace()}.
	 *
	 * @return	an array of stack trace element info instances
	 *		representing the stack trace pertaining to this
	 *		throwable.
	 */
	public StackTraceElementInfo[] getStackTrace();

	/**
	 * See {@link #getCause()}.
	 *
	 * @return	<tt>true</tt> if the throwable has a cause,
	 *		<tt>false</tt> otherwise.
	 */
	public boolean hasCause();

	/**
	 * See {@link Throwable#getCause()}.
	 *
	 * @return	the cause of the throwable this exception info
	 *		instance holds info about.
	 */
	public ExceptionInfo getCause();
}
