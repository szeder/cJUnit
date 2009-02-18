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
 * Holds basic information about a {@link StackTraceElement}.
 */
public interface StackTraceElementInfo {

	/**
	 * See {@link StackTraceElement#getClassName()}.
	 *
	 * @return	the fully qualified name of the <tt>Class</tt>
	 *		containing the execution point represented by the
	 *		stack trace element this instance holds info about.
	 */
	public String getClassName();

	/**
	 * See {@link StackTraceElement#getMethodName()}.
	 *
	 * @return	the name of the method containing the execution point
	 *		represented by the stack trace element this instance
	 *		holds info about.
	 */
	public String getMethodName();

	/**
	 * See {@link StackTraceElement#getFileName()}.
	 *
	 * @return	the name of the file containing the execution point
	 *		represented by the stack trace element this instance
	 *		holds info about.
	 */
	public String getFileName();

	/**
	 * See {@link StackTraceElement#getLineNumber()}.
	 *
	 * @return	the line number of the source line containing the
	 *		execution point point represented by the stack trace
	 *		element this instance holds info about.
	 */
	public int getLineNumber();

}
