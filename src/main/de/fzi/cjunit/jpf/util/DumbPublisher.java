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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.Reporter;

public class DumbPublisher extends Publisher {

	public DumbPublisher(Config conf, Reporter reporter) {
		super(conf, reporter);
	}

	@Override
	public String getName() {
		return "DumbPublisher";
	}
}
