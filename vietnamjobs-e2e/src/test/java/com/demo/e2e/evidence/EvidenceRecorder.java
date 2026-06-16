package com.demo.e2e.evidence;

import com.demo.e2e.config.TestConfig;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class EvidenceRecorder {

    private static final DateTimeFormatter RUN_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final DateTimeFormatter STEP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final AtomicInteger TEST_COUNTER = new AtomicInteger(0);
    private static final List<TestSummary> SUMMARIES = new ArrayList<>();
    private static final ThreadLocal<TestArtifact> CURRENT = new ThreadLocal<>();
    private static final Path RUN_DIRECTORY = TestConfig.EVIDENCE_DIRECTORY.resolve("run-" + LocalDateTime.now().format(RUN_FORMAT));

    static {
        ensureDirectory(RUN_DIRECTORY);
    }

    private EvidenceRecorder() {
    }

    public static void startTest(String methodName, String displayName) {
        int order = TEST_COUNTER.incrementAndGet();
        String folderName = String.format("%03d-%s", order, sanitize(methodName));
        Path testDirectory = RUN_DIRECTORY.resolve(folderName);
        ensureDirectory(testDirectory);
        CURRENT.set(new TestArtifact(order, methodName, displayName, testDirectory));
    }

    public static void bindPage(Page page) {
        TestArtifact artifact = CURRENT.get();
        if (artifact != null) {
            artifact.page = page;
        }
    }

    public static Path currentTestDirectory() {
        TestArtifact artifact = CURRENT.get();
        return artifact == null ? RUN_DIRECTORY : artifact.directory;
    }

    public static void captureStep(String action, Page page) {
        if (!TestConfig.EVIDENCE_ENABLED) {
            return;
        }
        TestArtifact artifact = CURRENT.get();
        if (artifact == null) {
            return;
        }
        if (page != null) {
            artifact.page = page;
        }

        int stepNumber = artifact.steps.size() + 1;
        String screenshotName = String.format("step-%03d.png", stepNumber);
        Path screenshotPath = artifact.directory.resolve(screenshotName);
        String url = "";
        String note = "";

        try {
            if (artifact.page != null) {
                url = artifact.page.url();
                artifact.page.screenshot(new Page.ScreenshotOptions()
                        .setPath(screenshotPath)
                        .setFullPage(false)
                        .setTimeout(5000));
            } else {
                note = "No page bound to current test.";
            }
        } catch (PlaywrightException ex) {
            note = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        }

        artifact.steps.add(new StepArtifact(
                stepNumber,
                LocalDateTime.now().format(STEP_FORMAT),
                action,
                url,
                Files.exists(screenshotPath) ? screenshotName : null,
                note));
    }

    public static void recordFailure(Throwable throwable) {
        TestArtifact artifact = CURRENT.get();
        if (artifact == null) {
            return;
        }
        artifact.status = "FAILED";
        artifact.errorMessage = throwable == null ? "Unknown error" : throwable.toString();
        captureStep("Failure captured", artifact.page);
    }

    public static void finishCurrentTest() {
        TestArtifact artifact = CURRENT.get();
        if (artifact == null) {
            return;
        }
        if (artifact.status == null) {
            artifact.status = "PASSED";
        }

        writeTestReport(artifact);
        synchronized (SUMMARIES) {
            SUMMARIES.add(new TestSummary(artifact.order, artifact.methodName, artifact.displayName, artifact.status,
                    RUN_DIRECTORY.relativize(artifact.directory.resolve("index.html")).toString().replace('\\', '/')));
            writeSuiteReport();
        }
        CURRENT.remove();
    }

    private static void writeTestReport(TestArtifact artifact) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">")
                .append("<title>").append(escape(artifact.displayName)).append("</title>")
                .append("<style>")
                .append("body{font-family:Segoe UI,Arial,sans-serif;background:#f5f7fb;color:#1f2937;margin:24px;}")
                .append(".card{background:#fff;border:1px solid #dbe3ef;border-radius:12px;padding:20px;margin-bottom:20px;}")
                .append(".status{display:inline-block;padding:6px 10px;border-radius:999px;font-weight:700;}")
                .append(".PASSED{background:#dcfce7;color:#166534;}.FAILED{background:#fee2e2;color:#991b1b;}")
                .append("table{width:100%;border-collapse:collapse;}th,td{border:1px solid #dbe3ef;padding:10px;vertical-align:top;}")
                .append("th{background:#eef4ff;text-align:left;}img{max-width:100%;border:1px solid #cbd5e1;border-radius:8px;}")
                .append("a{color:#2563eb;text-decoration:none;}a:hover{text-decoration:underline;}")
                .append("</style></head><body>");

        html.append("<div class=\"card\">")
                .append("<h1>").append(escape(artifact.displayName)).append("</h1>")
                .append("<p><strong>Method:</strong> ").append(escape(artifact.methodName)).append("</p>")
                .append("<p><strong>Status:</strong> <span class=\"status ").append(artifact.status).append("\">")
                .append(artifact.status).append("</span></p>");

        Path traceOne = artifact.directory.resolve("trace-01.zip");
        if (Files.exists(traceOne)) {
            html.append("<p><strong>Trace:</strong> <a href=\"trace-01.zip\">trace-01.zip</a>");
            int traceIndex = 2;
            while (Files.exists(artifact.directory.resolve(String.format("trace-%02d.zip", traceIndex)))) {
                html.append(" | <a href=\"")
                        .append(String.format("trace-%02d.zip", traceIndex))
                        .append("\">")
                        .append(String.format("trace-%02d.zip", traceIndex))
                        .append("</a>");
                traceIndex++;
            }
            html.append("</p>");
        }

        if (artifact.errorMessage != null && !artifact.errorMessage.isBlank()) {
            html.append("<p><strong>Error:</strong> ").append(escape(artifact.errorMessage)).append("</p>");
        }
        html.append("</div>");

        html.append("<div class=\"card\"><h2>Step Evidence</h2><table><thead><tr>")
                .append("<th>#</th><th>Time</th><th>Action</th><th>URL</th><th>Screenshot</th><th>Note</th>")
                .append("</tr></thead><tbody>");

        for (StepArtifact step : artifact.steps) {
            html.append("<tr>")
                    .append("<td>").append(step.number).append("</td>")
                    .append("<td>").append(escape(step.timestamp)).append("</td>")
                    .append("<td>").append(escape(step.action)).append("</td>")
                    .append("<td>").append(escape(step.url)).append("</td>")
                    .append("<td>");
            if (step.screenshotName != null) {
                html.append("<a href=\"").append(step.screenshotName).append("\">")
                        .append("<img src=\"").append(step.screenshotName).append("\" alt=\"")
                        .append(escape(step.action)).append("\"></a>");
            }
            html.append("</td>")
                    .append("<td>").append(escape(step.note)).append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table></div></body></html>");
        writeString(artifact.directory.resolve("index.html"), html.toString());
    }

    private static void writeSuiteReport() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">")
                .append("<title>VietnamJobs E2E Evidence</title>")
                .append("<style>")
                .append("body{font-family:Segoe UI,Arial,sans-serif;background:#f5f7fb;color:#1f2937;margin:24px;}")
                .append("table{width:100%;border-collapse:collapse;background:#fff;border:1px solid #dbe3ef;}")
                .append("th,td{border:1px solid #dbe3ef;padding:10px;text-align:left;}th{background:#eef4ff;}")
                .append(".PASSED{color:#166534;font-weight:700;}.FAILED{color:#991b1b;font-weight:700;}")
                .append("a{color:#2563eb;text-decoration:none;}a:hover{text-decoration:underline;}")
                .append("</style></head><body>")
                .append("<h1>VietnamJobs E2E Evidence</h1>")
                .append("<p>Run directory: ").append(escape(RUN_DIRECTORY.toString())).append("</p>")
                .append("<table><thead><tr><th>#</th><th>Test</th><th>Status</th><th>Report</th></tr></thead><tbody>");

        for (TestSummary summary : SUMMARIES) {
            html.append("<tr>")
                    .append("<td>").append(summary.order).append("</td>")
                    .append("<td>").append(escape(summary.displayName)).append("<br><small>")
                    .append(escape(summary.methodName)).append("</small></td>")
                    .append("<td class=\"").append(summary.status).append("\">").append(summary.status).append("</td>")
                    .append("<td><a href=\"").append(summary.relativeReportPath).append("\">Open report</a></td>")
                    .append("</tr>");
        }
        html.append("</tbody></table></body></html>");
        writeString(RUN_DIRECTORY.resolve("index.html"), html.toString());
    }

    private static void ensureDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void writeString(Path path, String value) {
        try {
            Files.writeString(path, value, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]+", "-");
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private record StepArtifact(int number, String timestamp, String action, String url, String screenshotName,
                                String note) {
    }

    private record TestSummary(int order, String methodName, String displayName, String status,
                               String relativeReportPath) {
    }

    private static final class TestArtifact {
        private final int order;
        private final String methodName;
        private final String displayName;
        private final Path directory;
        private final List<StepArtifact> steps = new ArrayList<>();
        private String status;
        private String errorMessage;
        private Page page;

        private TestArtifact(int order, String methodName, String displayName, Path directory) {
            this.order = order;
            this.methodName = methodName;
            this.displayName = displayName;
            this.directory = directory;
        }
    }
}
