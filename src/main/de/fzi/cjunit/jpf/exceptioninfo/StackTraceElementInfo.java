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
public class StackTraceElementInfo {

	String className;
	String methodName;
	String fileName;
	int lineNumber;

	public StackTraceElementInfo(String className,
			String methodName, String fileName, int lineNumber) {
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public StackTraceElementInfo(StackTraceElement ste) {
		this(ste.getClassName(), ste.getMethodName(),
				ste.getFileName(), ste.getLineNumber());
	}

	public StackTraceElementInfo(StackTraceElementInfo other) {
		this(other.getClassName(), other.getMethodName(),
				other.getFileName(), other.getLineNumber());
	}

	/**
	 * See {@link StackTraceElement#getClassName()}.
	 *
	 * @return	the fully qualified name of the <tt>Class</tt>
	 *		containing the execution point represented by the
	 *		stack trace element this instance holds info about.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * See {@link StackTraceElement#getMethodName()}.
	 *
	 * @return	the name of the method containing the execution point
	 *		represented by the stack trace element this instance
	 *		holds info about.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * See {@link StackTraceElement#getFileName()}.
	 *
	 * @return	the name of the file containing the execution point
	 *		represented by the stack trace element this instance
	 *		holds info about.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * See {@link StackTraceElement#getLineNumber()}.
	 *
	 * @return	the line number of the source line containing the
	 *		execution point point represented by the stack trace
	 *		element this instance holds info about.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

}
