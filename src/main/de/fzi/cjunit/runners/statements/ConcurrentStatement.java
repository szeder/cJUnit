/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.runners.statements;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.fzi.cjunit.runners.model.ConcurrentFrameworkMethod;

import static de.fzi.cjunit.jpf.inside.TestWrapperOptions.*;

import de.fzi.cjunit.jpf.inside.TestWrapper;
import de.fzi.cjunit.jpf.outside.JPFInvoker;
import de.fzi.cjunit.jpf.util.ArgumentCreator;
import de.fzi.cjunit.jpf.util.OnFailurePublisher;
import de.fzi.cjunit.jpf.util.TestReporter;

public class ConcurrentStatement extends Statement {

	protected ConcurrentFrameworkMethod testMethod;
	protected Object target;
	protected List<FrameworkMethod> befores;
	protected List<FrameworkMethod> afters;
	protected Class<? extends Throwable> expectedExceptionClass;

	// for testing
	ConcurrentStatement() {}

	public ConcurrentStatement(ConcurrentFrameworkMethod testMethod,
			Object target) {
		this.testMethod = testMethod;
		this.target = target;
		befores = new ArrayList<FrameworkMethod>();
		afters = new ArrayList<FrameworkMethod>();
	}

	@Override
	public void evaluate() throws Throwable {
		invokeJPF();
	}

	protected void invokeJPF() throws Throwable {
		new JPFInvoker().run(createJPFArgs());
	}

	public void expectException(Class<? extends Throwable> expected) {
		expectedExceptionClass = expected;
	}

	public void addBefores(List<FrameworkMethod> befores) {
		this.befores = befores;
	}

	public void addAfters(List<FrameworkMethod> afters) {
		this.afters = afters;
	}

	protected String[] createJPFArgs() {
		List<String> testArgs = new ArrayList<String>();
		testArgs.add(TestClassOpt + target.getClass().getName());
		testArgs.add(TestOpt + MethodSubOpt + testMethod.getName()
				+ "," + ExceptionSubOpt
				+ getExceptionClassName(expectedExceptionClass));
		for (FrameworkMethod beforeMethod : befores) {
			testArgs.add(BeforeMethodOpt +
					beforeMethod.getName());
		}
		for (FrameworkMethod afterMethod : afters) {
			testArgs.add(AfterMethodOpt +
					afterMethod.getName());
		}

		return new ArgumentCreator()
			.publisher(OnFailurePublisher.class)
			.reporter(TestReporter.class)
			.jpfArgs(new String[] {
					"+vm.por.field_boundaries.never=",
					"+search.multiple_errors=true",
					"+jpf.report.console.start=",
					"+jpf.report.console.finished=result",
					"+jpf.report.console.show_steps=true"
				})
			.app(TestWrapper.class)
			.appArgs(testArgs)
			.getArgs();
	}

	protected String getExceptionClassName(
			Class<? extends Throwable> exceptionClass) {
		if (exceptionClass == null) {
			return "";
		} else {
			return exceptionClass.getName();
		}
	}
}
