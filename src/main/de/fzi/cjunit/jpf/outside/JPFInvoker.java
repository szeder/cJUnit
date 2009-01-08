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
import gov.nasa.jpf.JPF;

import de.fzi.cjunit.jpf.inside.TestWrapper;
import de.fzi.cjunit.jpf.outside.TestObserver;
import de.fzi.cjunit.jpf.util.ArgumentCreator;


public class JPFInvoker {

	TestObserver testObserver;

	public JPFInvoker() {
		testObserver = new TestObserver();
	}

	public void run(Object target, Method method) throws Throwable {
		runJPF(createJPFArgs(target, method));

		checkResult();
	}

	public void checkResult() throws Throwable {
		if (testObserver.getTestResult() == false) {
			throw testObserver.getException();
		}
	}

	public void runJPF(String[] args) {
		Config conf = JPF.createConfig(args);
		JPF jpf = new JPF(conf);
		jpf.addPropertyListener(testObserver);
		jpf.run();
	}

	public String[] createJPFArgs(Object target, Method method) {
		List<String> testArgs = new ArrayList<String>();
		testArgs.add("--testclass=" + target.getClass().getName());
		testArgs.add("--testmethod=" + method.getName());

		return new ArgumentCreator()
			.app(TestWrapper.class)
			.appArgs(testArgs)
			.getArgs();
	}
}
