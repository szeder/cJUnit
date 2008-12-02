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

import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Constructor;

public class ExceptionFactoryTest {

	@Test
	public void testGetCheckedConstructorReturnConstructor() {
		Constructor<?> constructor
				= ExceptionFactory.getCheckedConstructor(
						Throwable.class, String.class);
		assertThat(constructor, not(nullValue()));
		Class<?> declaringClass = constructor.getDeclaringClass();
		assertThat(declaringClass.equals(Throwable.class),
				equalTo(true));
		assertThat(constructor.getParameterTypes().length,
				equalTo(1));
		Class<?> parameterType = constructor.getParameterTypes()[0];
		assertThat(parameterType.equals(String.class), equalTo(true));
	}

	@Test
	public void testGetCheckedConstructorReturnNull() {
		Constructor<?> constructor
				= ExceptionFactory.getCheckedConstructor(
						Object.class, String.class);
		assertThat(constructor, nullValue());
	}

	public class InnerExceptionClass extends Throwable {
		private static final long serialVersionUID = 1L;
		public InnerExceptionClass(String msg) { super(msg); }
	}

	@Ignore("known breakage")
	@Test
	public void testGetCheckedConstructorWorksOnInnerClass() {
		Constructor<?> constructor
				= ExceptionFactory.getCheckedConstructor(
						InnerExceptionClass.class, String.class);
		assertThat(constructor, not(nullValue()));
	}

	@Test
	public void testGetExceptionConstructorFindStringConstructor() {
		Constructor<?> constructor
				= ExceptionFactory.getExceptionConstructor(
						Exception.class);
		assertThat(constructor, not(nullValue()));
		assertThat(constructor.getParameterTypes().length,
				equalTo(1));
		Class<?> parameterType = constructor.getParameterTypes()[0];
		assertThat(parameterType.equals(String.class), equalTo(true));
	}

	@Test
	public void testGetExceptionConstructorFindObjectConstructor() {
		Constructor<?> constructor
				= ExceptionFactory.getExceptionConstructor(
						AssertionError.class);
		assertThat(constructor, not(nullValue()));
		assertThat(constructor.getParameterTypes().length,
				equalTo(1));
		Class<?> parameterType = constructor.getParameterTypes()[0];
		assertThat(parameterType.equals(Object.class), equalTo(true));
	}
}
