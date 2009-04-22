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

import java.io.File;

import org.junit.Test;


public class StackFrameConverterTest {

	StackFrameConverter sfc = new StackFrameConverter();

	@Test
	public void sourceFileBasenameStripDirs() {
		String filename = "Object.java";
		String filenameWithPath = "java" + File.separatorChar
				+ "lang/" + File.separatorChar + filename;
		assertThat(sfc.sourceFileBasename(filenameWithPath),
				equalTo(filename));
	}

	@Test
	public void sourceFileBasenameNothingToStrip() {
		String magicFilename = "<direct call>";
		assertThat(sfc.sourceFileBasename(magicFilename),
				equalTo(magicFilename));
	}
}
