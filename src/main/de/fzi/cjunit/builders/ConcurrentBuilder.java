/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.builders;

import de.fzi.cjunit.ConcurrentTest;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.TestClass;

public class ConcurrentBuilder extends RunnerBuilder {

	@Override
	public Runner runnerForClass(Class<?> klass) throws Throwable {
		TestClass testClass = new TestClass(klass);
		if (testClass.getAnnotatedMethods(
					ConcurrentTest.class).size() != 0) {
			return new BlockJUnit4ClassRunner(klass);
		}
		return null;
	}
}
