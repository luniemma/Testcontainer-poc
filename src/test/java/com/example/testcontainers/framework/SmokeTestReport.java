package com.example.testcontainers.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Smoke Test Report Generator for ATT Organization
 *
 * Generates detailed reports of smoke test results in multiple formats:
 * - Console output
 * - JSON report
 * - HTML report
 * - Markdown report
 */
public class SmokeTestReport {

    private static final Logger logger = LoggerFactory.getLogger(SmokeTestReport.class);
    private static final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final String applicationName;
    private final String environment;
    private final Instant testStartTime;
    private final Map<String, TestResult> testResults;
    private final List<String> testLogs;

    public SmokeTestReport(String applicationName, String environment) {
        this.applicationName = applicationName;
        this.environment = environment;
        this.testStartTime = Instant.now();
        this.testResults = new LinkedHashMap<>();
        this.testLogs = new ArrayList<>();
    }

    /**
     * Add a test result
     */
    public void addTestResult(String testName, boolean passed, String message, long durationMs) {
        testResults.put(testName, new TestResult(testName, passed, message, durationMs));
    }

    /**
     * Add a log entry
     */
    public void addLog(String log) {
        testLogs.add(log);
    }

    /**
     * Generate console report
     */
    public void printConsoleReport() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SMOKE TEST REPORT");
        System.out.println("=".repeat(80));
        System.out.println("Application: " + applicationName);
        System.out.println("Environment: " + environment);
        System.out.println("Test Time: " + formatter.format(testStartTime));
        System.out.println("-".repeat(80));

        long passedCount = testResults.values().stream().filter(r -> r.passed).count();
        long failedCount = testResults.values().stream().filter(r -> !r.passed).count();
        long totalDuration = testResults.values().stream().mapToLong(r -> r.durationMs).sum();

        System.out.println(String.format("Total Tests: %d | Passed: %d | Failed: %d | Duration: %dms",
            testResults.size(), passedCount, failedCount, totalDuration));
        System.out.println("-".repeat(80));

        testResults.forEach((name, result) -> {
            String status = result.passed ? "✓ PASS" : "✗ FAIL";
            System.out.println(String.format("[%s] %s (%dms)", status, name, result.durationMs));
            if (!result.passed && result.message != null) {
                System.out.println("    Reason: " + result.message);
            }
        });

        System.out.println("=".repeat(80) + "\n");
    }

    /**
     * Generate JSON report
     */
    public void generateJsonReport(String outputPath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            Map<String, Object> report = new LinkedHashMap<>();
            report.put("applicationName", applicationName);
            report.put("environment", environment);
            report.put("testStartTime", formatter.format(testStartTime));
            report.put("summary", getSummary());
            report.put("testResults", testResults);
            report.put("logs", testLogs);

            File file = new File(outputPath);
            mapper.writeValue(file, report);

            logger.info("JSON report generated: {}", outputPath);
        } catch (IOException e) {
            logger.error("Failed to generate JSON report", e);
        }
    }

    /**
     * Generate HTML report
     */
    public void generateHtmlReport(String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n<head>\n");
            writer.write("<title>Smoke Test Report - " + applicationName + "</title>\n");
            writer.write("<style>\n");
            writer.write("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
            writer.write(".container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n");
            writer.write("h1 { color: #333; border-bottom: 3px solid #007bff; padding-bottom: 10px; }\n");
            writer.write(".summary { background: #e9ecef; padding: 15px; border-radius: 5px; margin: 20px 0; }\n");
            writer.write(".test-result { margin: 10px 0; padding: 10px; border-left: 4px solid; }\n");
            writer.write(".pass { border-color: #28a745; background: #d4edda; }\n");
            writer.write(".fail { border-color: #dc3545; background: #f8d7da; }\n");
            writer.write(".metric { display: inline-block; margin-right: 20px; }\n");
            writer.write("</style>\n");
            writer.write("</head>\n<body>\n");
            writer.write("<div class='container'>\n");

            writer.write("<h1>Smoke Test Report</h1>\n");
            writer.write("<div class='summary'>\n");
            writer.write("<p><strong>Application:</strong> " + applicationName + "</p>\n");
            writer.write("<p><strong>Environment:</strong> " + environment + "</p>\n");
            writer.write("<p><strong>Test Time:</strong> " + formatter.format(testStartTime) + "</p>\n");

            Map<String, Object> summary = getSummary();
            writer.write("<div style='margin-top: 15px;'>\n");
            writer.write("<span class='metric'><strong>Total:</strong> " + summary.get("total") + "</span>\n");
            writer.write("<span class='metric' style='color: #28a745;'><strong>Passed:</strong> " + summary.get("passed") + "</span>\n");
            writer.write("<span class='metric' style='color: #dc3545;'><strong>Failed:</strong> " + summary.get("failed") + "</span>\n");
            writer.write("<span class='metric'><strong>Duration:</strong> " + summary.get("totalDurationMs") + "ms</span>\n");
            writer.write("</div>\n");
            writer.write("</div>\n");

            writer.write("<h2>Test Results</h2>\n");
            testResults.forEach((name, result) -> {
                try {
                    String cssClass = result.passed ? "pass" : "fail";
                    String status = result.passed ? "✓ PASS" : "✗ FAIL";
                    writer.write("<div class='test-result " + cssClass + "'>\n");
                    writer.write("<strong>" + status + "</strong> " + name + " <em>(" + result.durationMs + "ms)</em>\n");
                    if (!result.passed && result.message != null) {
                        writer.write("<br><small>Reason: " + result.message + "</small>\n");
                    }
                    writer.write("</div>\n");
                } catch (IOException e) {
                    logger.error("Error writing test result to HTML", e);
                }
            });

            writer.write("</div>\n</body>\n</html>");
            logger.info("HTML report generated: {}", outputPath);
        } catch (IOException e) {
            logger.error("Failed to generate HTML report", e);
        }
    }

    /**
     * Generate Markdown report
     */
    public void generateMarkdownReport(String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("# Smoke Test Report\n\n");
            writer.write("## Summary\n\n");
            writer.write("- **Application:** " + applicationName + "\n");
            writer.write("- **Environment:** " + environment + "\n");
            writer.write("- **Test Time:** " + formatter.format(testStartTime) + "\n\n");

            Map<String, Object> summary = getSummary();
            writer.write("### Results\n\n");
            writer.write("| Metric | Value |\n");
            writer.write("|--------|-------|\n");
            writer.write("| Total Tests | " + summary.get("total") + " |\n");
            writer.write("| Passed | " + summary.get("passed") + " |\n");
            writer.write("| Failed | " + summary.get("failed") + " |\n");
            writer.write("| Duration | " + summary.get("totalDurationMs") + "ms |\n\n");

            writer.write("## Test Results\n\n");
            testResults.forEach((name, result) -> {
                try {
                    String status = result.passed ? "✅ PASS" : "❌ FAIL";
                    writer.write("### " + status + " " + name + "\n\n");
                    writer.write("- **Duration:** " + result.durationMs + "ms\n");
                    if (!result.passed && result.message != null) {
                        writer.write("- **Reason:** " + result.message + "\n");
                    }
                    writer.write("\n");
                } catch (IOException e) {
                    logger.error("Error writing test result to Markdown", e);
                }
            });

            logger.info("Markdown report generated: {}", outputPath);
        } catch (IOException e) {
            logger.error("Failed to generate Markdown report", e);
        }
    }

    /**
     * Get test summary
     */
    private Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", testResults.size());
        summary.put("passed", testResults.values().stream().filter(r -> r.passed).count());
        summary.put("failed", testResults.values().stream().filter(r -> !r.passed).count());
        summary.put("totalDurationMs", testResults.values().stream().mapToLong(r -> r.durationMs).sum());
        return summary;
    }

    /**
     * Test result holder
     */
    private static class TestResult {
        private final String name;
        private final boolean passed;
        private final String message;
        private final long durationMs;

        public TestResult(String name, boolean passed, String message, long durationMs) {
            this.name = name;
            this.passed = passed;
            this.message = message;
            this.durationMs = durationMs;
        }

        public String getName() { return name; }
        public boolean isPassed() { return passed; }
        public String getMessage() { return message; }
        public long getDurationMs() { return durationMs; }
    }
}
