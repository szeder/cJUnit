/*
 * This file is covered by the terms of the NASA Open Source Agreement v1.3.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.util;

import java.io.PrintWriter;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Reporter;

import de.fzi.cjunit.jpf.outside.TestObserver;


public class OnFailurePublisher extends ConsolePublisher {

	TestObserver testObserver;

	public OnFailurePublisher(Config conf, Reporter reporter) {
		super(conf, reporter);
	}

	public void setTestObserver(TestObserver testObserver) {
		this.testObserver = testObserver;
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	protected void openChannel() {
		out = new PrintWriter(System.out, false);
	}

	@Override
	public void publishFinished() {
		super.publishFinished();
		if (testObserver.getTestResult() == false) {
			out.flush();
		}
	}
}
