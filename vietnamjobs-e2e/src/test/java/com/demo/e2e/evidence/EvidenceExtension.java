package com.demo.e2e.evidence;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class EvidenceExtension implements BeforeEachCallback, AfterEachCallback, TestExecutionExceptionHandler {

    @Override
    public void beforeEach(ExtensionContext context) {
        EvidenceRecorder.startTest(
                context.getRequiredTestMethod().getName(),
                context.getDisplayName());
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        EvidenceRecorder.recordFailure(throwable);
        throw throwable;
    }

    @Override
    public void afterEach(ExtensionContext context) {
        EvidenceRecorder.finishCurrentTest();
    }
}
