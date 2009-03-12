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

import java.io.File;

import gov.nasa.jpf.jvm.StackFrame;

public class StackFrameConverter {

	public String sourceFileBasename(String filename) {
		int idx = filename.lastIndexOf(File.separatorChar);
		if (0 <= idx) {
			return filename.substring(idx+1);
		}
		return filename;
	}

	public StackTraceElement toStackTraceElement(StackFrame stackFrame) {
		return new StackTraceElement(stackFrame.getClassName(),
				stackFrame.getMethodName(),
				sourceFileBasename(stackFrame.getSourceFile()),
				stackFrame.getLine());
	}

	public StackTraceElement[] toStackTrace(
			StackFrame[] stackFrameArray) {
		StackTraceElement[] stackTrace
				= new StackTraceElement[stackFrameArray.length];
		for (int i = 0, j = stackFrameArray.length-1;
				i < stackTrace.length;
				i++, j--) {
			stackTrace[i] = toStackTraceElement(stackFrameArray[j]);
		}
		return stackTrace;
	}
}
