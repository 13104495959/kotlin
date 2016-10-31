/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.completion.test;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/idea-completion/testData/incrementalResolve")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class CompletionIncrementalResolveTestGenerated extends AbstractCompletionIncrementalResolveTest {
    public void testAllFilesPresentInIncrementalResolve() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/idea-completion/testData/incrementalResolve"), Pattern.compile("^(.+)\\.kt$"), TargetBackend.ANY, true);
    }

    @TestMetadata("codeAboveChanged.kt")
    public void testCodeAboveChanged() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/codeAboveChanged.kt");
        doTest(fileName);
    }

    @TestMetadata("codeAboveChanged2.kt")
    public void testCodeAboveChanged2() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/codeAboveChanged2.kt");
        doTest(fileName);
    }

    @TestMetadata("dataFlowInfoFromPrevStatement.kt")
    public void testDataFlowInfoFromPrevStatement() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/dataFlowInfoFromPrevStatement.kt");
        doTest(fileName);
    }

    @TestMetadata("dataFlowInfoFromSameStatement.kt")
    public void testDataFlowInfoFromSameStatement() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/dataFlowInfoFromSameStatement.kt");
        doTest(fileName);
    }

    @TestMetadata("doNotAnalyzeComplexStatement.kt")
    public void testDoNotAnalyzeComplexStatement() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/doNotAnalyzeComplexStatement.kt");
        doTest(fileName);
    }

    @TestMetadata("noDataFlowFromOldStatement.kt")
    public void testNoDataFlowFromOldStatement() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/noDataFlowFromOldStatement.kt");
        doTest(fileName);
    }

    @TestMetadata("noPrevStatement.kt")
    public void testNoPrevStatement() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/noPrevStatement.kt");
        doTest(fileName);
    }

    @TestMetadata("outOfBlockModification.kt")
    public void testOutOfBlockModification() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/outOfBlockModification.kt");
        doTest(fileName);
    }

    @TestMetadata("prevStatementNotResolved.kt")
    public void testPrevStatementNotResolved() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/prevStatementNotResolved.kt");
        doTest(fileName);
    }

    @TestMetadata("sameStatement.kt")
    public void testSameStatement() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("idea/idea-completion/testData/incrementalResolve/sameStatement.kt");
        doTest(fileName);
    }
}
