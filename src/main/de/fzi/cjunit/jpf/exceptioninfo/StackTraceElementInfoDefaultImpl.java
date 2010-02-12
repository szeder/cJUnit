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


public class StackTraceElementInfoDefaultImpl
		implements StackTraceElementInfo {

	String className;
	String methodName;
	String fileName;
	int lineNumber;

	public StackTraceElementInfoDefaultImpl(String className,
			String methodName, String fileName, int lineNumber) {
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public StackTraceElementInfoDefaultImpl(StackTraceElement ste) {
		this(ste.getClassName(), ste.getMethodName(),
				ste.getFileName(), ste.getLineNumber());
	}

	public StackTraceElementInfoDefaultImpl(StackTraceElementInfo other) {
		this(other.getClassName(), other.getMethodName(),
				other.getFileName(), other.getLineNumber());
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
