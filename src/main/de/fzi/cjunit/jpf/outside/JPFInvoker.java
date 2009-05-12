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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.Error;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.report.Publisher;

import de.fzi.cjunit.jpf.inside.TestWrapper;
import de.fzi.cjunit.jpf.outside.TestObserver;
import de.fzi.cjunit.jpf.util.ArgumentCreator;
import de.fzi.cjunit.jpf.util.OnFailurePublisher;


public class JPFInvoker {

	protected Config conf;
	protected JPF jpf;

	protected TestObserver testObserver;

	public JPFInvoker() {
		testObserver = new TestObserver();
	}

	public void run(Object target, Method method,
			Class<? extends Throwable> exceptionClass)
			throws Throwable {
		runJPF(createJPFArgs(target, method, exceptionClass));

		checkResult();
	}

	public void checkResult() throws Throwable {
		if (testObserver.getTestResult() == false) {
			throw testObserver.getException();
		}
	}

	protected void runJPF(String[] args) {
		conf = JPF.createConfig(args);
		jpf = new JPF(conf);
		jpf.addPropertyListener(testObserver);
		registerTestObserverAtPublisher();
		jpf.run();
	}

	protected List<Error> getJPFSearchErrors() {
		return jpf.getSearchErrors();
	}

	protected void registerTestObserverAtPublisher() {
		for (Publisher p : jpf.getReporter().getPublishers()) {
			if (p instanceof OnFailurePublisher) {
				((OnFailurePublisher) p).setTestObserver(
						testObserver);
			}
		}
	}

	protected String[] createJPFArgs(Object target, Method method,
			Class<? extends Throwable> exceptionClass) {
		List<String> testArgs = new ArrayList<String>();
		testArgs.add("--testclass=" + target.getClass().getName());
		testArgs.add("--testmethod=" + method.getName());
		if (exceptionClass != null) {
			testArgs.add("--expectedexception=" +
					exceptionClass.getName());
		}

		return new ArgumentCreator()
			.publisher(OnFailurePublisher.class)
			.jpfArgs(new String[] {
					"+jpf.report.console.start=",
					"+jpf.report.console.finished=result",
					"+jpf.report.console.show_steps=true"
				})
			.app(TestWrapper.class)
			.appArgs(testArgs)
			.getArgs();
	}
}
