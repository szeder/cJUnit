/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.jpf.inside;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import java.lang.reflect.Method;

public class TestWrapperTest {

	String className = "java.lang.String";
	String methodName = "toString";

	@Test
	public void parseArgsTestClass() {
		TestWrapper tw = new TestWrapper(new String[] {
				"--testclass=" + className });
		assertThat(tw.testClassName, equalTo(className));
	}

	@Test
	public void parseArgsTesMethod() {
		TestWrapper tw = new TestWrapper(new String[] {
				"--testmethod=" + methodName });
		assertThat(tw.testMethodName, equalTo(methodName));
	}

	@Test(expected=RuntimeException.class)
	public void parseArgsWrongArgument() {
		new TestWrapper(new String[] { "asdf" });
	}

	@Test
	public void createTestObject() throws Throwable {
		TestWrapper tw = new TestWrapper(new String[] {
				"--testclass=" + className });
		tw.createTestObject();
		assertThat(tw.target, instanceOf(String.class));
	}

	@Test
	public void createMethod() throws Throwable {
		TestWrapper tw = new TestWrapper(new String[] {
				"--testclass=" + className });
		tw.createTestObject();
		Method m = tw.createMethod(methodName);
		assertThat(m.getName(), equalTo(methodName));
		assertThat(m.getDeclaringClass().getName(), equalTo(className));
	}

	@Test
	public void createTestMethod() throws Throwable {
		TestWrapper tw = new TestWrapper(new String[] {
				"--testclass=" + className,
				"--testmethod=" + methodName
				});
		tw.createTestObject();
		tw.createTestMethod();
		assertThat(tw.method.getName(), equalTo(methodName));
		assertThat(tw.method.getDeclaringClass().getName(),
				equalTo(className));
	}
}
