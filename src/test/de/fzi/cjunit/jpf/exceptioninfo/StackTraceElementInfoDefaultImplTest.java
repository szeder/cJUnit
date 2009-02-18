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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import de.fzi.cjunit.jpf.exceptioninfo.StackTraceElementInfoDefaultImpl;


public class StackTraceElementInfoDefaultImplTest {

	String className = "de.fzi.cjunit.MightyClass";
	String methodName = "mightyMethod";
	String fileName = "/de/fzi/cjunit/MightyClass.java";
	int lineNumber = 42;

	@Test
	public void attributesFromStackTraceElement() {
		StackTraceElement ste = new StackTraceElement(className,
				methodName, fileName, lineNumber);

		StackTraceElementInfo steInfo
				= new StackTraceElementInfoDefaultImpl(ste);

		assertThat("classname", steInfo.getClassName(),
				equalTo(className));
		assertThat("methodname", steInfo.getMethodName(),
				equalTo(methodName));
		assertThat("filename", steInfo.getFileName(),
				equalTo(fileName));
		assertThat("linenumber", steInfo.getLineNumber(),
				equalTo(lineNumber));
	}

	@Test
	public void attributesFromStackTraceElementInfo() {
		StackTraceElement ste = new StackTraceElement(className,
				methodName, fileName, lineNumber);
		StackTraceElementInfo otherSTEInfo
				= new StackTraceElementInfoDefaultImpl(ste);

		StackTraceElementInfoDefaultImpl steInfo
				= new StackTraceElementInfoDefaultImpl(
						otherSTEInfo);

		assertThat("classname", steInfo.getClassName(),
				equalTo(className));
		assertThat("methodname", steInfo.getMethodName(),
				equalTo(methodName));
		assertThat("filename", steInfo.getFileName(),
				equalTo(fileName));
		assertThat("linenumber", steInfo.getLineNumber(),
				equalTo(lineNumber));
	}
}
