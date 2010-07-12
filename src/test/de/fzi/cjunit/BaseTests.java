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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
	de.fzi.cjunit.builders.ConcurrentBuilderTest.class,
	de.fzi.cjunit.runners.ConcurrentRunnerTest.class,
	de.fzi.cjunit.runners.model.ConcurrentFrameworkModelTest.class,
	de.fzi.cjunit.runners.statements.ConcurrentStatementTest.class,
	de.fzi.cjunit.jpf.inside.ReflectiveMethodTest.class,
	de.fzi.cjunit.jpf.inside.TestMethodTest.class,
	de.fzi.cjunit.jpf.inside.TestWrapperTest.class,
	de.fzi.cjunit.jpf.exceptioninfo.ExceptionInfoDefaultImplTest.class,
	de.fzi.cjunit.jpf.exceptioninfo.StackTraceElementInfoDefaultImplTest.class,
	de.fzi.cjunit.jpf.outside.JPFInvokerTest.class,
	de.fzi.cjunit.jpf.outside.ResultCollectorTest.class,
	de.fzi.cjunit.jpf.outside.TestFailedPropertyTest.class,
	de.fzi.cjunit.jpf.util.ExceptionComparatorTest.class,
	de.fzi.cjunit.jpf.util.StackFrameConverterTest.class,
	de.fzi.cjunit.jpf.util.ExceptionFactoryTest.class
})
public class BaseTests {
}
