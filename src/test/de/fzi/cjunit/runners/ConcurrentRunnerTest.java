/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.runners;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import de.fzi.cjunit.ConcurrentTest;
import de.fzi.cjunit.runners.model.ConcurrentFrameworkMethod;
import de.fzi.cjunit.runners.statements.ConcurrentStatement;
import de.fzi.cjunit.testutils.TestException;


public class ConcurrentRunnerTest {

	static public class TestClass {
		@Test public void method1() { }
		@Test public void method2() { }
		public void notTestMethod() { }
	}

	@Test
	public void computeConcurrentTestMethodsForTestClass()
			throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(TestClass.class);
		List<FrameworkMethod> methods
				= runner.computeConcurrentTestMethods();

		assertThat("number of returned methods",
				methods.size(), equalTo(0));
	}

	@Test
	public void computeTestMethodsForTestClass() throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(TestClass.class);
		List<FrameworkMethod> methods = runner.computeTestMethods();

		assertThat("number of returned methods",
				methods.size(), equalTo(2));
		assertThat(methods, hasItems(
				new FrameworkMethod(TestClass.class.getMethod(
						"method1")),
				new FrameworkMethod(TestClass.class.getMethod(
						"method2"))
				));
	}

	@Test
	public void computeTestMethodsReturnsTheSame() throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(TestClass.class);
		List<FrameworkMethod> methods1 = runner.computeTestMethods();
		List<FrameworkMethod> methods2 = runner.computeTestMethods();

		assertThat(methods1, sameInstance(methods2));
	}

	static public class ConcurrentTestClass {
		@ConcurrentTest public void methodInConcurrent1() { }
		@ConcurrentTest public void methodInConcurrent2() { }
		public void notTestMethod() { }
	}

	@Test
	public void computeConcurrentTestMethodsForConcurrentTestClass()
			throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(
				ConcurrentTestClass.class);
		List<FrameworkMethod> methods
				= runner.computeConcurrentTestMethods();

		assertThat("number of returned methods",
				methods.size(), equalTo(2));
		assertThat(methods, hasItems(
				new FrameworkMethod(
					ConcurrentTestClass.class.getMethod(
						"methodInConcurrent1")),
				new FrameworkMethod(
					ConcurrentTestClass.class.getMethod(
						"methodInConcurrent2"))
				));
	}

	@Test
	public void computeTestMethodsForConcurrentTestClass()
			throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(
				ConcurrentTestClass.class);
		List<FrameworkMethod> methods
				= runner.computeConcurrentTestMethods();

		assertThat("number of returned methods",
				methods.size(), equalTo(2));
		assertThat(methods, hasItems(
				new FrameworkMethod(
					ConcurrentTestClass.class.getMethod(
						"methodInConcurrent1")),
				new FrameworkMethod(
					ConcurrentTestClass.class.getMethod(
						"methodInConcurrent2"))
				));
	}

	static public class MixedTestClass {
		@Test public void methodInMixed1() { }
		@Test public void methodInMixed2() { }
		@ConcurrentTest public void concurrentMethodInMixed1() { }
		@ConcurrentTest public void concurrentMethodInMixed2() { }
		public void notTestMethod() { }
	}

	@Test
	public void computeConcurrentTestMethodsForMixedTestClass()
			throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(
				MixedTestClass.class);
		List<FrameworkMethod> methods
				= runner.computeConcurrentTestMethods();

		assertThat("number of returned methods",
				methods.size(), equalTo(2));
		assertThat(methods, hasItems(
				new FrameworkMethod(
					MixedTestClass.class.getMethod(
						"concurrentMethodInMixed1")),
				new FrameworkMethod(
					MixedTestClass.class.getMethod(
						"concurrentMethodInMixed2"))
				));
	}

	@Test
	public void computeTestMethodsForMixedTestClass()
			throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(
				MixedTestClass.class);
		List<FrameworkMethod> methods
				= runner.computeTestMethods();

		assertThat("number of returned methods",
				methods.size(), equalTo(4));
		assertThat(methods, hasItems(
				new FrameworkMethod(
					MixedTestClass.class.getMethod(
						"methodInMixed1")),
				new FrameworkMethod(
					MixedTestClass.class.getMethod(
						"methodInMixed2")),
				new FrameworkMethod(
					MixedTestClass.class.getMethod(
						"concurrentMethodInMixed1")),
				new FrameworkMethod(
					MixedTestClass.class.getMethod(
						"concurrentMethodInMixed2"))
				));
	}

	static public class TestClassWithThreadGroup {
		@ConcurrentTest(threadGroup=5)
		public void testMethod1() { }
		@ConcurrentTest(threadGroup=5)
		public void testMethod2() { }
		@ConcurrentTest(threadGroup=6)
		public void testMethod3() { }
		@ConcurrentTest(threadGroup=6)
		public void testMethod4() { }
		@ConcurrentTest(threadGroup=6)
		public void testMethod5() { }
	}

	@Test
	public void computeTestMethodsForTestClassWithThreadGroup()
			throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithThreadGroup.class);
		List<FrameworkMethod> methods
				= runner.computeTestMethods();

		assertThat("number of returned methods",
				methods.size(), equalTo(2));
		assertThat(methods, hasItems(
				new FrameworkMethod(
					TestClassWithThreadGroup.class
						.getMethod("testMethod1")),
				new FrameworkMethod(
					TestClassWithThreadGroup.class
						.getMethod("testMethod3"))
				));
		ConcurrentFrameworkMethod cfm1
				= (ConcurrentFrameworkMethod) methods.get(0);
		assertThat(cfm1.getThreadGroupMembers().size(), equalTo(1));
		assertThat(cfm1.getThreadGroupMembers(), hasItem(
				new ConcurrentFrameworkMethod(
					TestClassWithThreadGroup.class
						.getMethod("testMethod2"))));
		ConcurrentFrameworkMethod cfm2
				= (ConcurrentFrameworkMethod) methods.get(1);
		assertThat(cfm2.getThreadGroupMembers().size(), equalTo(2));
		assertThat(cfm2.getThreadGroupMembers(), hasItems(
				new ConcurrentFrameworkMethod(
					TestClassWithThreadGroup.class
						.getMethod("testMethod4")),
				new ConcurrentFrameworkMethod(
					TestClassWithThreadGroup.class
						.getMethod("testMethod5"))
				));
	}

	static public class TestAndConcurrentTestClass {
		@Test @ConcurrentTest public void testMethod() { }
	}

	@Test(expected=InitializationError.class)
	public void errorOnTestAndConcurrentTest() throws Throwable {
		@SuppressWarnings("unused")
		ConcurrentRunner runner = new ConcurrentRunner(
				TestAndConcurrentTestClass.class);
	}

	@Test
	public void methodBlockReturnStatementForTestMethod() throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(
				MixedTestClass.class);
		FrameworkMethod method = new FrameworkMethod(
				MixedTestClass.class.getMethod(
						"methodInMixed1"));
		Statement statement = runner.methodBlock(method);

		assertThat(statement, instanceOf(Statement.class));
		assertThat(statement,
				not(instanceOf(ConcurrentStatement.class)));
	}

	@Test
	public void methodBlockReturnConcurrentStatementForConcurrentTestMethod()
			throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(
				MixedTestClass.class);
		FrameworkMethod method = new ConcurrentFrameworkMethod(
				MixedTestClass.class.getMethod(
						"concurrentMethodInMixed1"));
		Statement statement = runner.methodBlock(method);

		assertThat(statement, instanceOf(ConcurrentStatement.class));
	}

	static public class TestClassWithBeforeClass {
		@BeforeClass public static void beforeClassMethod() { }
		@ConcurrentTest public void testMethod() { }
	}

	@Test(expected=InitializationError.class)
	public void errorOnBeforeClassWithConcurrentTest() throws Throwable {
		@SuppressWarnings("unused")
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithBeforeClass.class);
	}

	static public class TestClassWithAfterClass {
		@AfterClass public static void afterClassMethod() { }
		@ConcurrentTest public void testMethod() { }
	}

	@Test(expected=InitializationError.class)
	public void errorOnAfterClassWithConcurrentTest() throws Throwable {
		@SuppressWarnings("unused")
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithAfterClass.class);
	}

	static public class TestClassWithBeforeClassAndAfterClass {
		@BeforeClass public static void beforeClassMethod() { }
		@AfterClass public static void afterClassMethod() { }
		@ConcurrentTest public void testMethod() { }
	}

	@Test(expected=InitializationError.class)
	public void errorOnBeforeClassAndAfterClassWithConcurrentTest()
			throws Throwable {
		@SuppressWarnings("unused")
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithBeforeClassAndAfterClass.class);
	}

	static public class TestClassWithRules {
		@Rule public MethodRule rule;
		@ConcurrentTest public void testMethod() { }
	}

	@Test(expected=InitializationError.class)
	public void errorOnRulesWithConcurrentTest() throws Throwable {
		@SuppressWarnings("unused")
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithRules.class);
	}

	static public class TestClassWithNegativeThreadCount {
		@ConcurrentTest(threadCount=-1) public void testMethod() { }
	}

	@Test(expected=InitializationError.class)
	public void testNegativeThreadCount() throws Throwable {
		@SuppressWarnings("unused")
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithNegativeThreadCount.class);
	}

	static public class TestClassWithZeroThreadCount {
		@ConcurrentTest(threadCount=0) public void testMethod() { }
	}

	@Test(expected=InitializationError.class)
	public void testZeroThreadCount() throws Throwable {
		@SuppressWarnings("unused")
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithZeroThreadCount.class);
	}

	static public class TestClassWithThreadCount {
		@ConcurrentTest(threadCount=3,expected=TestException.class)
		public void testMethod() { }
	}

	@Test
	public void testThreadCount() throws Throwable {
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithThreadCount.class);
		ConcurrentFrameworkMethod method = new ConcurrentFrameworkMethod(
				TestClassWithThreadCount.class.getMethod(
						"testMethod"));

		ConcurrentStatement statement = runner.buildStatements(method,
				null);
		assertThat("number of methods",
				statement.getTestMethods().size(), equalTo(3));
		assertThat("first method",
				statement.getTestMethods().get(0).getMethod(),
				equalTo(method));
		assertThat("first exception (class name)",
				statement.getTestMethods().get(0).getException().getName(),
				equalTo(TestException.class.getName()));
		assertThat("second method",
				statement.getTestMethods().get(1).getMethod(),
				equalTo(method));
		assertThat("second exception (class name)",
				statement.getTestMethods().get(1).getException().getName(),
				equalTo(TestException.class.getName()));
		assertThat("third method",
				statement.getTestMethods().get(2).getMethod(),
				equalTo(method));
		assertThat("third exception (class name)",
				statement.getTestMethods().get(2).getException().getName(),
				equalTo(TestException.class.getName()));
	}

	static public class TestClassWithThreadCountAndThreadGroup {
		@ConcurrentTest(threadCount=3,threadGroup=4)
		public void testMethod() { }
	}

	@Test(expected=InitializationError.class)
	public void testThreadCountAndThreadGroup() throws Throwable {
		@SuppressWarnings("unused")
		ConcurrentRunner runner = new ConcurrentRunner(
				TestClassWithThreadCountAndThreadGroup.class);
	}
}
