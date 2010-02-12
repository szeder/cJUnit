/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.outside;

import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.JVM;

import de.fzi.cjunit.jpf.exceptioninfo.ElementInfoWrapper;
import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfo;
import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfoDefaultImpl;
import de.fzi.cjunit.jpf.exceptioninfo.StackTraceElementInfo;
import de.fzi.cjunit.jpf.exceptioninfo.StackTraceElementInfoDefaultImpl;


public class ExceptionInfoCollector {

	public ExceptionInfoCollector() { }

	public ExceptionInfo collectFromStack(JVM vm) throws Exception {
		ElementInfo ei = elementInfoFromStack(vm);

		return exceptionInfoFromInfo(ei);
	}

	protected ExceptionInfo exceptionInfoFromInfo(ElementInfo ei) {
		ElementInfoWrapper eiw = new ElementInfoWrapper(ei,
				ExceptionInfoDefaultImpl.class);

		StackTraceElementInfo[] stackTrace = stackTraceFromInfo(eiw);

		ExceptionInfo cause = causeFromInfo(eiw);

		return new ExceptionInfoDefaultImpl(
				eiw.getStringField("className"),
				eiw.getStringField("message"),
				stackTrace, cause);
	}

	protected ExceptionInfo causeFromInfo(ElementInfoWrapper eiw) {
		ElementInfo cause = eiw.getElementInfoForField("cause");
		if (cause == null) {
			return null;
		} else {
			return exceptionInfoFromInfo(cause);
		}
	}

	protected StackTraceElementInfo[] stackTraceFromInfo(
			ElementInfoWrapper eiw) {
		ElementInfo[] array = eiw.getReferenceArray("stackTrace");
		StackTraceElementInfo[] stackTrace
				= new StackTraceElementInfoDefaultImpl[
					array.length];
		int i = 0;
		for (ElementInfo stei : array) {
			stackTrace[i] = stackTraceElementInfoFromElementInfo(
					stei);
			i++;
		}
		return stackTrace;
	}

	protected StackTraceElementInfo stackTraceElementInfoFromElementInfo(
			ElementInfo ei) {
		ElementInfoWrapper eiw = new ElementInfoWrapper(ei,
				StackTraceElementInfoDefaultImpl.class);

		return new StackTraceElementInfoDefaultImpl(
				eiw.getStringField("className"),
				eiw.getStringField("methodName"),
				eiw.getStringField("fileName"),
				eiw.getIntField("lineNumber"));
	}

	protected ElementInfo elementInfoFromStack(JVM vm) throws Exception {
		int argRef = vm.getLastThreadInfo().peek();
		if (argRef == -1) {
			throw new Exception("Cannot examine stack: " +
					"cannot collect ExceptionInfo");
		}

		return elementInfoFromReference(argRef);
	}

	protected ElementInfo elementInfoFromReference(int argRef) {
		return DynamicArea.getHeap().get(argRef);
	}
}
