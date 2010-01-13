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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import de.fzi.cjunit.jpf.util.ArgumentCreator;

public class JPFForTesting {

	protected Config config;
	protected JPF jpf;

	protected void createJPF(Class<?> appClass) {
		createJPF(new String[] {}, appClass);
	}

	protected void createJPF(String[] args, Class<?> appClass) {
		String[] jpfArgs = new ArgumentCreator()
				.app(appClass)
				.defaultJPFTestArgs()
				.jpfArgs(args)
				.getArgs();
		config = JPF.createConfig(jpfArgs);
		jpf = new JPF(config);
	}
}
