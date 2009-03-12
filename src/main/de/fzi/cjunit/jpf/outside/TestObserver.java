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

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.ExceptionInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;

import de.fzi.cjunit.jpf.inside.NotifierMethods;
import de.fzi.cjunit.jpf.util.ExceptionFactory;
import de.fzi.cjunit.jpf.util.StackFrameConverter;


public class TestObserver extends PropertyListenerAdapter {

	boolean result = true;
	boolean testSucceeded = true;

	String exceptionClassName;
	String exceptionMessage;
	StackFrame[] stackTrace;
	Throwable exception;

	Stack<Boolean> stateStack = new Stack<Boolean>();

	public boolean getTestResult() {
		return testSucceeded;
	}

	public Throwable getException() throws IllegalArgumentException,
			SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		if (exception == null) {
			exception = new ExceptionFactory().createException(
					exceptionClassName, exceptionMessage,
					new StackFrameConverter().toStackTrace(
							stackTrace));
		}
		return exception;
	}

	public void testFailed() {
		result = false;
		testSucceeded = false;
	};

	// from Property
	@Override
	public boolean check(Search search, JVM jvm) {
		return result;
	}

	@Override
	public String getErrorMessage() {
		return "test failed";
	}

	// from VMListener
	@Override
	public void exceptionThrown(JVM vm) {
		ThreadInfo ti = vm.getLastThreadInfo();
		ExceptionInfo ei = vm.getPendingException();

		if (testSucceeded) {
			exceptionClassName = ei.getExceptionClassname();
			String exceptionDetails = ei.getDetails();
			// It seems that the string returned by
			// ExceptionInfo.getDetails() looks like
			// "<exception type> : <message>".  To get the original
			// message we must strip the type prefix.
			if (exceptionDetails.startsWith(
					exceptionClassName + " : ")) {
				exceptionMessage = exceptionDetails.substring(
						exceptionClassName.length()+3);
			} else {
				exceptionMessage = exceptionDetails;
			}
			stackTrace = ti.dumpStack();
		}
	}

	@Override
	public void executeInstruction(JVM vm) {
		Instruction insn = vm.getLastInstruction();

		if (insn instanceof InvokeInstruction) {
			handleInvokeInstruction(vm, (InvokeInstruction) insn);
		}
	}

	public void handleInvokeInstruction(JVM vm, InvokeInstruction insn) {
		ThreadInfo ti = vm.getLastThreadInfo();
		MethodInfo callee = insn.getInvokedMethod(ti);
		if (!callee.getClassName().equals(
				NotifierMethods.class.getName())) {
			return;
		}

		if (callee.getName().equals("testFailed")) {
			testFailed();
		}
	}

	// from SearchListener
	@Override
	public void stateAdvanced(Search search) {
		stateStack.push(result);
	}

	@Override
	public void stateBacktracked(Search search) {
		result = stateStack.pop();
	}
}
