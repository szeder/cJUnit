/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.runner;

import de.fzi.cjunit.builders.ConcurrentDefaultPossibilitiesBuilder;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;


public abstract class Request extends org.junit.runner.Request {

	public static org.junit.runner.Request classes(Class<?>... classes) {
		try {
			ConcurrentDefaultPossibilitiesBuilder builder
				= new ConcurrentDefaultPossibilitiesBuilder(
						true);
			Suite suite = new Suite(builder, classes);
			return runner(suite);
		} catch (InitializationError e) {
			throw new RuntimeException(
					"Bug in saff's brain: Suite " +
					"constructor, called as above, should" +
					" always complete");
		}
	}

}
