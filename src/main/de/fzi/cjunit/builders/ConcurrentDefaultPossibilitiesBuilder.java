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

import java.util.Arrays;
import java.util.List;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

public class ConcurrentDefaultPossibilitiesBuilder
		extends AllDefaultPossibilitiesBuilder {

	public ConcurrentDefaultPossibilitiesBuilder(
			boolean canUseSuiteMethod) {
		super(canUseSuiteMethod);
	}

	@Override
	public Runner runnerForClass(Class<?> testClass) throws Throwable {
		for (RunnerBuilder each : builderList()) {
			Runner runner = each.safeRunnerForClass(testClass);
			if (runner != null) {
				return runner;
			}
		}
		return null;
	}

	protected List<RunnerBuilder> builderList() {
		return Arrays.asList(
				ignoredBuilder(),
				annotatedBuilder(),
				suiteMethodBuilder(),
				junit3Builder(),
				concurrentBuilder(),
				junit4Builder()
				);
	}

	protected ConcurrentBuilder concurrentBuilder() {
		return new ConcurrentBuilder();
	}
}
