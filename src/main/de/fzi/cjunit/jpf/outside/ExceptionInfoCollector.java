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

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;

import de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfo;
import de.fzi.cjunit.jpf.exceptioninfo.MultipleFailureExceptionInfo;
import de.fzi.cjunit.jpf.exceptioninfo.StackTraceElementInfo;
import de.fzi.cjunit.jpf.util.ElementInfoWrapper;
import de.fzi.cjunit.jpf.util.StackFrameConverter;


public class ExceptionInfoCollector {

	public ExceptionInfoCollector() { }

	public ExceptionInfo collectFromJPFExceptionInfo(
			gov.nasa.jpf.jvm.ExceptionInfo exceptionInfo) {
		ElementInfo ei = elementInfoFromReference(
				exceptionInfo.getExceptionReference());

		return exceptionInfoFromThrowable(ei);
	}

	protected ExceptionInfo exceptionInfoFromThrowable(ElementInfo ei) {
		ElementInfoWrapper eiw = new ElementInfoWrapper(ei,
				Throwable.class);

		StackTraceElementInfo[] stackTrace
				= stackTraceFromThrowable(eiw);

		ExceptionInfo cause = causeFromThrowable(eiw);

		return new ExceptionInfo(eiw.getClassName(),
				eiw.getStringField("detailMessage"),
				stackTrace, cause);
	}

	protected ExceptionInfo causeFromThrowable(ElementInfoWrapper eiw) {
		ElementInfo cause = eiw.getElementInfoForField("cause");
		// cause == this means no cause; see Throwable.getCause()
		if (cause == null || cause == eiw.getWrappedElementInfo()) {
			return null;
		} else {
			return exceptionInfoFromThrowable(cause);
		}
	}

	protected StackTraceElementInfo[] stackTraceFromThrowable(
			ElementInfoWrapper eiw) {
		ElementInfo[] array;
		try {
			array = eiw.getReferenceArray("stackTrace");
		} catch (Throwable t) {
			return stackTraceFromSnapshot(eiw);
		}

		StackTraceElementInfo[] stackTrace
				= new StackTraceElementInfo[array.length];
		int i = 0;
		for (ElementInfo stei : array) {
			stackTrace[i] = stackTraceElementInfoFromJPFStackTraceElement(
					stei);
			i++;
		}
		return stackTrace;
	}

	protected StackTraceElementInfo stackTraceElementInfoFromJPFStackTraceElement(
			ElementInfo ei) {
		ElementInfoWrapper eiw = new ElementInfoWrapper(ei,
				StackTraceElement.class);

		return new StackTraceElementInfo(
				eiw.getStringField("clsName"),
				eiw.getStringField("mthName"),
				eiw.getStringField("fileName"),
				eiw.getIntField("line"));
	}

	protected StackTraceElementInfo[] stackTraceFromSnapshot(
			ElementInfoWrapper eiw) {
		int[] snapshot = eiw.getIntArrayForField("snapshot");

		List<StackTraceElementInfo> list
				= new ArrayList<StackTraceElementInfo>();
		for (int i = 0; i < snapshot.length; i += 2) {
			StackTraceElementInfo stei
				= stackTraceElementInfoFromSnapshotElement(
						snapshot, i);
			if (stei != null) {
				list.add(stei);
			}
		}

		StackTraceElementInfo[] stackTrace
				= new StackTraceElementInfo[list.size()];
		stackTrace = list.toArray(stackTrace);

		return stackTrace;
	}

	protected StackTraceElementInfo stackTraceElementInfoFromSnapshotElement(
			int[] snapshot, int idx) {
		String className, methodName, fileName;
		int lineNumber;

		MethodInfo mi = MethodInfo.getMethodInfo(snapshot[idx]);
		if (mi.isDirectCallStub()) {
			if (mi.getName().startsWith("[reflection]")) {
				className = "java.lang.reflect.Method";
				methodName = "invoke";
				fileName = "Native Method";
				lineNumber = -1;
			} else {
				return null;
			}
		} else {
			className = mi.getClassName();
			methodName = mi.getName();

			if (mi.isMJI()) {
				fileName = "Native Method";
				lineNumber = -1;
			} else {
				fileName = mi.getSourceFileName();
				lineNumber = mi.getLineNumber(
					mi.getInstruction(snapshot[idx+1]));
			}
		}

		return new StackTraceElementInfo(className, methodName, fileName,
				lineNumber);
	}

	public ExceptionInfo collectFromExceptionInfoOnStack(JVM vm)
			throws Exception {
		ElementInfo ei = elementInfoFromStack(vm);

		if (ei.getClassInfo().getName().equals(
				MultipleFailureExceptionInfo.class.getName())) {
			return multipleFailureExceptionInfoFromInfo(ei);
		} else {
			return exceptionInfoFromInfo(ei);
		}
	}

	protected ExceptionInfo exceptionInfoFromInfo(ElementInfo ei) {
		ElementInfoWrapper eiw = new ElementInfoWrapper(ei,
				ExceptionInfo.class);

		StackTraceElementInfo[] stackTrace = stackTraceFromInfo(eiw);

		ExceptionInfo cause = causeFromInfo(eiw);

		return new ExceptionInfo(
				eiw.getStringField("className"),
				eiw.getStringField("message"),
				stackTrace, cause);
	}

	protected MultipleFailureExceptionInfo multipleFailureExceptionInfoFromInfo(
			ElementInfo ei) {
		ElementInfoWrapper eiw = new ElementInfoWrapper(ei,
				MultipleFailureExceptionInfo.class);

		StackTraceElementInfo[] stackTrace = stackTraceFromInfo(eiw);

		ExceptionInfo cause = causeFromInfo(eiw);

		ElementInfo[] array = eiw.getReferenceArray("failures");
		ExceptionInfo[] failures = new ExceptionInfo[array.length];
		for (int i = 0; i < array.length; i++) {
			failures[i] = exceptionInfoFromInfo(array[i]);
		}

		return new MultipleFailureExceptionInfo(
				eiw.getStringField("message"), stackTrace,
				cause, failures);
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
		StackTraceElementInfo[] stackTrace = new StackFrameConverter()
				.toStackTraceElementInfoArray(array);
		return stackTrace;
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
