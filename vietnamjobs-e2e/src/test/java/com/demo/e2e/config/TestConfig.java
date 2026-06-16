package com.demo.e2e.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TestConfig {

    public record Credentials(String username, String password) {
        public boolean isConfigured() {
            return isMeaningful(username) && isMeaningful(password) && !"change-me".equals(password);
        }

        private static boolean isMeaningful(String value) {
            return value != null && !value.isBlank();
        }
    }

    public static final String BASE_URL = read("VIETNAMJOBS_BASE_URL", "e2e.baseUrl", "http://localhost:8087");
    public static final boolean HEADLESS = Boolean.parseBoolean(read("VIETNAMJOBS_HEADLESS", "e2e.headless", "true"));
    public static final int TIMEOUT_MS = Integer.parseInt(read("VIETNAMJOBS_TIMEOUT_MS", "e2e.timeoutMs", "30000"));
    public static final int SLOW_MO_MS = Integer.parseInt(read("VIETNAMJOBS_SLOWMO_MS", "e2e.slowMoMs", "0"));
    public static final boolean EVIDENCE_ENABLED = Boolean.parseBoolean(read("VIETNAMJOBS_EVIDENCE_ENABLED", "e2e.evidence.enabled", "true"));
    public static final Path EVIDENCE_DIRECTORY = Paths.get(read("VIETNAMJOBS_EVIDENCE_DIR", "e2e.evidence.dir", "target/evidence"))
            .toAbsolutePath()
            .normalize();

    public static final Credentials SEEKER = new Credentials(
            read("VIETNAMJOBS_SEEKER_USERNAME", "e2e.seeker.username", "Minh"),
            read("VIETNAMJOBS_SEEKER_PASSWORD", "e2e.seeker.password", "Nguyen221@"));

    public static final Credentials EMPLOYER = new Credentials(
            read("VIETNAMJOBS_EMPLOYER_USERNAME", "e2e.employer.username", "autotest_employer"),
            read("VIETNAMJOBS_EMPLOYER_PASSWORD", "e2e.employer.password", "Autotest@123"));

    public static final Credentials ADMIN = new Credentials(
            read("VIETNAMJOBS_ADMIN_USERNAME", "e2e.admin.username", "autotest_admin"),
            read("VIETNAMJOBS_ADMIN_PASSWORD", "e2e.admin.password", "Autotest@123"));

    public static final int PUBLIC_POSTING_ID = Integer.parseInt(read("VIETNAMJOBS_PUBLIC_POSTING_ID", "e2e.publicPostingId", "900010"));
    public static final String PUBLIC_POSTING_TITLE = read("VIETNAMJOBS_PUBLIC_POSTING_TITLE", "e2e.publicPostingTitle", "Autotest Java Posting");
    public static final int INTEGRATION_POSTING_ID = Integer.parseInt(read("VIETNAMJOBS_INTEGRATION_POSTING_ID", "e2e.integrationPostingId", "900010"));
    public static final String INTEGRATION_POSTING_TITLE = read("VIETNAMJOBS_INTEGRATION_POSTING_TITLE", "e2e.integrationPostingTitle", "Autotest Java Posting");
    public static final int INTEGRATION_EMPLOYER_ID = Integer.parseInt(read("VIETNAMJOBS_INTEGRATION_EMPLOYER_ID", "e2e.integrationEmployerId", "900002"));
    public static final String INTEGRATION_EMPLOYER_NAME = read("VIETNAMJOBS_INTEGRATION_EMPLOYER_NAME", "e2e.integrationEmployerName", "Autotest Employer Company");
    public static final int CATEGORY_PARENT_ID = Integer.parseInt(read("VIETNAMJOBS_CATEGORY_PARENT_ID", "e2e.categoryParentId", "18"));
    public static final int CATEGORY_CHILD_PARENT_ID = Integer.parseInt(read("VIETNAMJOBS_CATEGORY_CHILD_PARENT_ID", "e2e.categoryChildParentId", "7"));
    public static final String EXISTING_CATEGORY_NAME = read("VIETNAMJOBS_EXISTING_CATEGORY_NAME", "e2e.existingCategoryName", "IT");

    public static final String SEARCH_TITLE = read("VIETNAMJOBS_SEARCH_TITLE", "e2e.searchTitle", "Autotest");
    public static final String SEEKER_EXPECTED_FULLNAME = read("VIETNAMJOBS_SEEKER_FULLNAME", "e2e.seeker.fullname", "Minh Nguyen");
    public static final String SEEKER_EXPECTED_PHONE = read("VIETNAMJOBS_SEEKER_PHONE", "e2e.seeker.phone", "0900002210");
    public static final String SEEKER_EXPECTED_CV = read("VIETNAMJOBS_SEEKER_CV", "e2e.seeker.cv", "cvThanhTu.pdf");
    public static final String JOB_EXPECTED_WAGE = read("VIETNAMJOBS_JOB_WAGE", "e2e.job.wage", "Dưới 10 triệu");
    public static final String JOB_EXPECTED_LOCATION = read("VIETNAMJOBS_JOB_LOCATION", "e2e.job.location", "Hồ Chí Minh");
    public static final String JOB_EXPECTED_EXPERIENCE = read("VIETNAMJOBS_JOB_EXPERIENCE", "e2e.job.experience", "Chưa có kinh nghiệm");
    public static final String JOB_EXPECTED_LEVEL = read("VIETNAMJOBS_JOB_LEVEL", "e2e.job.level", "Nhân viên");
    public static final String JOB_EXPECTED_QUANTITY = read("VIETNAMJOBS_JOB_QUANTITY", "e2e.job.quantity", "2");
    public static final String JOB_EXPECTED_TYPE = read("VIETNAMJOBS_JOB_TYPE", "e2e.job.type", "Toàn thời gian");
    public static final String JOB_EXPECTED_EMPLOYER_ADDRESS = read("VIETNAMJOBS_JOB_EMPLOYER_ADDRESS", "e2e.job.employerAddress", "123 Autotest Street, Ho Chi Minh City");
    public static final String JOB_EXPECTED_EMPLOYER_SCALE = read("VIETNAMJOBS_JOB_EMPLOYER_SCALE", "e2e.job.employerScale", "25-99 nhan vien");
    public static final String JOB_EXPECTED_GENDER = read("VIETNAMJOBS_JOB_GENDER", "e2e.job.gender", "Any");
    public static final String JOB_EXPECTED_CATEGORY = read("VIETNAMJOBS_JOB_CATEGORY", "e2e.job.category", "IT");
    public static final String CATEGORY_NAME_PREFIX = read("VIETNAMJOBS_CATEGORY_PREFIX", "e2e.categoryPrefix", "E2E Category");
    public static final String JOB_TITLE_PREFIX = read("VIETNAMJOBS_JOB_PREFIX", "e2e.jobPrefix", "E2E Job");
    public static final String COMPANY_NAME_PREFIX = read("VIETNAMJOBS_COMPANY_PREFIX", "e2e.companyPrefix", "E2E Company");

    public static final Path VALID_CV_PATH = Paths.get("test-data", "cv_valid.pdf").toAbsolutePath().normalize();
    public static final Path INVALID_AVATAR_PATH = Paths.get("test-data", "avatar.png").toAbsolutePath().normalize();

    private TestConfig() {
    }

    public static String uniqueValue(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + timestamp;
    }

    public static String url(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        if (path.startsWith("/")) {
            return BASE_URL + path;
        }
        return BASE_URL + "/" + path;
    }

    private static String read(String envKey, String propertyKey, String fallback) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }
        return fallback;
    }
}
