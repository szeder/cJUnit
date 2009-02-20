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

import gov.nasa.jpf.jvm.ElementInfo;


public class StackTraceElementWrapper extends ElementInfoWrapper
		implements StackTraceElementInfo {

	public StackTraceElementWrapper(ElementInfo elementInfo) {
		super(elementInfo, StackTraceElementInfoDefaultImpl.class);
	}

	@Override
	public String getClassName() {
		return getStringField("className");
	}

	@Override
	public String getMethodName() {
		return getStringField("methodName");
	}

	@Override
	public String getFileName() {
		return getStringField("fileName");
	}

	@Override
	public int getLineNumber() {
		return getIntField("lineNumber");
	}
}
