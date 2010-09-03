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

import gov.nasa.jpf.jvm.ElementInfo;

import de.fzi.cjunit.jpf.exceptioninfo.StackTraceElementInfo;


public class StackFrameConverter {

	public StackTraceElementInfo[] toStackTraceElementInfoArray(
			ElementInfo[] elementInfoArray) {
		if (elementInfoArray == null) {
			return new StackTraceElementInfo[0];
		}

		StackTraceElementInfo[] stackTrace = new StackTraceElementInfo[
				elementInfoArray.length];
		int i = 0;
		for (ElementInfo stei : elementInfoArray) {
			stackTrace[i] = toStackTraceElementInfo(stei);
			i++;
		}
		return stackTrace;
	}

	protected StackTraceElementInfo toStackTraceElementInfo(
			ElementInfo ei) {
		ElementInfoWrapper eiw = new ElementInfoWrapper(ei,
				StackTraceElementInfo.class);

		return new StackTraceElementInfo(
				eiw.getStringField("className"),
				eiw.getStringField("methodName"),
				eiw.getStringField("fileName"),
				eiw.getIntField("lineNumber"));
	}

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
