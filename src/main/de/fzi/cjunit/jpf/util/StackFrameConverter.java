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


import de.fzi.cjunit.jpf.exceptioninfo.StackTraceElementInfo;


public class StackFrameConverter {

	public StackTraceElement[] toStackTraceElementArray(
			StackTraceElementInfo[] infoArray) {
		if (infoArray == null) {
			return new StackTraceElement[0];
		}

		StackTraceElement[] stackTrace
				= new StackTraceElement[infoArray.length];
		for (int i = 0; i < stackTrace.length; i++) {
			stackTrace[i] = toStackTraceElement(infoArray[i]);
		}
		return stackTrace;
	}

	protected StackTraceElement toStackTraceElement(
			StackTraceElementInfo info) {
		return new StackTraceElement(info.getClassName(),
				info.getMethodName(),
				sourceFileBasename(info.getFileName()),
				info.getLineNumber());
	}

	protected String sourceFileBasename(String filename) {
		if (filename == null) {
			return "(Unknown source)";
		}
		int idx = filename.lastIndexOf(File.separatorChar);
		if (0 <= idx) {
			return filename.substring(idx+1);
		}
		return filename;
	}
}
