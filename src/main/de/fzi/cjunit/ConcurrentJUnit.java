/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit;

import org.junit.runner.Result;
import org.junit.internal.JUnitSystem;
import org.junit.internal.RealSystem;

import de.fzi.cjunit.runner.Request;

public class ConcurrentJUnit extends org.junit.runner.JUnitCore {

	public static void main(String... args) {
		runMainAndExit(new RealSystem(), args);
	}

	public static void runMainAndExit(JUnitSystem system, String... args) {
		Result result= new ConcurrentJUnit().runMain(system, args);
		system.exit(result.wasSuccessful() ? 0 : 1);
	}

	public static Result runClasses(Class<?>... classes) {
		return new ConcurrentJUnit().run(classes);
	}

	@Override
	public Result runMain(JUnitSystem system, String... args) {
		system.out().println(
			"cJUnit - a JUnit extension for concurrent unit tests");
		system.out().print("based on ");
		return super.runMain(system, args);
	}

	@Override
	public Result run(Class<?>... classes) {
		return run(Request.classes(classes));
	}

}
