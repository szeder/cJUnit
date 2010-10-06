/*
 * This file is covered by the terms of the Common Public License v1.0.
 *
 * Copyright (c) SZEDER Gábor
 *
 * Parts of this software were developed within the JEOPARD research
 * project, which received funding from the European Union's Seventh
 * Framework Programme under grant agreement No. 216682.
 */

package de.fzi.cjunit.runners.statements;

import static org.hamcrest.MatcherAssert.assertThat;

import static de.fzi.concurrentmatchers.Matchers.accessedExclusively;

import org.junit.Before;
import org.junit.runner.RunWith;

import de.fzi.cjunit.ConcurrentTest;
import de.fzi.cjunit.runners.ConcurrentRunner;
import de.fzi.concurrentmatchers.util.CriticalSection;


@RunWith(ConcurrentRunner.class)
public class ConcurrentStatementTestForParallelTestExecution {

	CriticalSection cs;

	@Before
	public void createCriticalSection() {
		cs = new CriticalSection();
	}

	@ConcurrentTest(threadCount=2)
	public void testInvokeJPFIsNotInvokedConcurrently()
			throws Throwable {
		ConcurrentStatement s = new ConcurrentStatement(null, null) {
			protected void invokeJPF() {
				assertThat(cs, accessedExclusively());
			}
		};
		s.evaluate();
	}
}
