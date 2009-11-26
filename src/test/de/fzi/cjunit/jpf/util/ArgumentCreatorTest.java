/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import gov.nasa.jpf.PropertyListenerAdapter;

public class ArgumentCreatorTest {

	@Test
	public void testApp() {
		String[] args = new ArgumentCreator()
			.app(Thread.class)
			.getArgs();
		assertThat(args.length, equalTo(1));
		assertThat(args, hasItemInArray("java.lang.Thread"));
	}

	@Test
	public void testAppArgsStringArray() {
		String[] args = new ArgumentCreator()
			.app(Thread.class)
			.appArgs(new String[] { "asdf", "ghjk" })
			.getArgs();
		assertThat(args.length, equalTo(3));
		assertThat(args, hasItemInArray("java.lang.Thread"));
		assertThat(args, hasItemInArray("asdf"));
		assertThat(args, hasItemInArray("ghjk"));
	}

	@Test
	public void testProperty() {
		String[] args = new ArgumentCreator()
			.property(PropertyListenerAdapter.class)
			.getArgs();
		assertThat(args.length, equalTo(1));
		assertThat(args, hasItemInArray("+search.properties=" +
				"gov.nasa.jpf.PropertyListenerAdapter"));
	}

	@Test
	public void testPublisher() {
		String[] args = new ArgumentCreator()
			.publisher(DumbPublisher.class)
			.getArgs();
		assertThat(args.length, equalTo(2));
		assertThat(args, hasItemInArray(
				"+jpf.report.publisher=DumbPublisher"));
		assertThat(args, hasItemInArray(
				"+jpf.report.DumbPublisher.class=" +
				"de.fzi.cjunit.jpf.util.DumbPublisher"));
	}

	@Test
	public void testReporter() {
		String[] args = new ArgumentCreator()
				.reporter(TestReporter.class)
				.getArgs();
		assertThat(args.length, equalTo(1));
		assertThat(args, hasItemInArray(
				"+jpf.report.class=" +
				"de.fzi.cjunit.jpf.util.TestReporter"));
	}

	@Test
	public void testJpfArgsStringArray() {
		String[] args = new ArgumentCreator()
			.jpfArgs(new String[] { "asdf", "ghjk" })
			.getArgs();
		assertThat(args.length, equalTo(2));
		assertThat(args, hasItemInArray("asdf"));
		assertThat(args, hasItemInArray("ghjk"));
	}
}
