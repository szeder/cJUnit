/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.testutils;

import java.util.Properties;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;

import de.fzi.cjunit.jpf.util.DumbPublisher;


public class JPFForTesting {

	protected Config config;
	protected JPF jpf;

	protected void createJPF(Class<?> appClass) {
		createJPF(appClass, new Properties());
	}

	protected void createJPF(Class<?> appClass, Properties jpfArgs) {
		config = JPF.createConfig(new String[] { appClass.getName() } );

		config.setProperty("search.properties",
				PropertyListenerAdapter.class.getName());
		config.setProperty("jpf.report.publisher",
				DumbPublisher.class.getSimpleName());
		config.setProperty("jpf.report.DumbPublisher.class",
				DumbPublisher.class.getName());
		config.putAll(jpfArgs);

		jpf = new JPF(config);
	}
}
