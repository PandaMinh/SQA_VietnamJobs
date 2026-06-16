package com.demo.e2e.tests;

import com.demo.e2e.config.TestConfig;
import com.demo.e2e.evidence.EvidenceExtension;
import com.demo.e2e.evidence.EvidenceRecorder;
import com.demo.e2e.pages.AdminPage;
import com.demo.e2e.pages.EmployerPage;
import com.demo.e2e.pages.JobPage;
import com.demo.e2e.pages.LoginPage;
import com.demo.e2e.pages.RegisterPage;
import com.demo.e2e.pages.SeekerPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Tag("regression")
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(EvidenceExtension.class)
class VietnamJobsE2ETest {

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;
    private int traceSegment;

    private LoginPage loginPage;
    private RegisterPage registerPage;
    private JobPage jobPage;
    private SeekerPage seekerPage;
    private EmployerPage employerPage;
    private AdminPage adminPage;

    @BeforeAll
    static void beforeAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(TestConfig.HEADLESS)
                .setSlowMo((double) TestConfig.SLOW_MO_MS));
    }

    @AfterAll
    static void afterAll() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setUp() {
        traceSegment = 0;
        initializeSession();
    }

    private void initializeSession() {
        context = browser.newContext();
        traceSegment++;
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
        page.setDefaultTimeout(TestConfig.TIMEOUT_MS);
        EvidenceRecorder.bindPage(page);

        loginPage = new LoginPage(page);
        registerPage = new RegisterPage(page);
        jobPage = new JobPage(page);
        seekerPage = new SeekerPage(page);
        employerPage = new EmployerPage(page);
        adminPage = new AdminPage(page);
        EvidenceRecorder.captureStep("Initialize browser context " + traceSegment, page);
    }

    @AfterEach
    void tearDown() {
        closeCurrentSession();
    }

    private void closeCurrentSession() {
        if (context == null) {
            return;
        }
        try {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(EvidenceRecorder.currentTestDirectory().resolve(String.format("trace-%02d.zip", traceSegment))));
        } catch (Exception ignored) {
            // Keep teardown resilient so the report still gets written.
        } finally {
            context.close();
            context = null;
            page = null;
        }
    }

    private void assumeSeekerConfigured() {
        assumeTrue(TestConfig.SEEKER.isConfigured(),
                "Configure seeker credentials in TestConfig or env vars before running authenticated seeker tests.");
    }

    private void assumeEmployerConfigured() {
        assumeTrue(TestConfig.EMPLOYER.isConfigured(),
                "Configure employer credentials in TestConfig or env vars before running authenticated employer tests.");
    }

    private void assumeAdminConfigured() {
        assumeTrue(TestConfig.ADMIN.isConfigured(),
                "Configure admin credentials in TestConfig or env vars before running authenticated admin tests.");
    }

    private void loginAsSeeker() {
        assumeSeekerConfigured();
        loginPage.open();
        loginPage.login(TestConfig.SEEKER.username(), TestConfig.SEEKER.password());
    }

    private void loginAsEmployer() {
        assumeEmployerConfigured();
        loginPage.open();
        loginPage.login(TestConfig.EMPLOYER.username(), TestConfig.EMPLOYER.password());
    }

    private void loginAsAdmin() {
        assumeAdminConfigured();
        loginPage.open();
        loginPage.login(TestConfig.ADMIN.username(), TestConfig.ADMIN.password());
    }

    private String uniqueCategoryName(String suffix) {
        return TestConfig.uniqueValue(TestConfig.CATEGORY_NAME_PREFIX + "-" + suffix);
    }

    private String uniqueJobTitle(String suffix) {
        return TestConfig.uniqueValue(TestConfig.JOB_TITLE_PREFIX + "-" + suffix);
    }

    @Test
    @Order(1)
    @Tag("smoke")
    @DisplayName("TC-E2E-001 Login page loads")
    void tc001_loginPageLoads() {
        loginPage.open();
        loginPage.assertLoaded();
    }

    @Test
    @Order(2)
    @Tag("smoke")
    @DisplayName("TC-E2E-002 Register page loads")
    void tc002_registerPageLoads() {
        registerPage.open();
        registerPage.assertLoaded();
    }

    @Test
    @Order(3)
    @DisplayName("TC-E2E-003 Register page shows seeker option")
    void tc003_registerPageShowsSeekerOption() {
        registerPage.open();
        assertTrue(page.locator("input[value='ROLE_SEEKER']").count() > 0);
    }

    @Test
    @Order(4)
    @DisplayName("TC-E2E-004 Register page shows employer option")
    void tc004_registerPageShowsEmployerOption() {
        registerPage.open();
        assertTrue(page.locator("input[value='ROLE_EMPLOYER']").count() > 0);
    }

    @Test
    @Order(5)
    @Tag("smoke")
    @DisplayName("TC-E2E-005 Public job list loads")
    void tc005_publicJobListLoads() {
        jobPage.openList();
        jobPage.assertListLoaded();
    }

    @Test
    @Order(6)
    @DisplayName("TC-E2E-006 Public search by title works")
    void tc006_publicCanSearchJobsByTitle() {
        jobPage.openList();
        jobPage.searchByTitle(TestConfig.SEARCH_TITLE);
        jobPage.expectUrlContains("/home/posting/search");
    }

    @Test
    @Order(7)
    @Tag("smoke")
    @DisplayName("TC-E2E-007 Public job detail loads")
    void tc007_publicCanOpenJobDetail() {
        jobPage.openSeededDetail();
        jobPage.assertDetailLoaded();
        assertTrue(jobPage.hasPostingTitle(TestConfig.PUBLIC_POSTING_TITLE));
    }

    @Test
    @Order(8)
    @DisplayName("TC-E2E-008 Anonymous cannot open employer jobs")
    void tc008_anonymousCannotOpenEmployerJobList() {
        employerPage.openJobs();
        assertTrue(page.url().contains("/account/login"));
    }

    @Test
    @Order(9)
    @DisplayName("TC-E2E-009 Anonymous cannot open admin jobs")
    void tc009_anonymousCannotOpenAdminJobPage() {
        adminPage.openJobs();
        assertTrue(page.url().contains("/account/login"));
    }

    @Test
    @Order(10)
    @Tag("smoke")
    @DisplayName("TC-E2E-010 Minh seeker can login and access profile")
    void tc010_seekerCanLogin() {
        loginAsSeeker();
        seekerPage.openProfile();
        seekerPage.assertProfileLoaded();
        seekerPage.assertProfileInfo(TestConfig.SEEKER_EXPECTED_FULLNAME, TestConfig.SEEKER_EXPECTED_PHONE, TestConfig.SEEKER_EXPECTED_CV);
    }

    @Test
    @Order(11)
    @DisplayName("TC-E2E-011 Minh profile displays exact seeker data")
    void tc011_seekerProfileDisplaysExactData() {
        loginAsSeeker();
        seekerPage.openProfile();
        seekerPage.assertProfileLoaded();
        seekerPage.assertProfileInfo(TestConfig.SEEKER_EXPECTED_FULLNAME, TestConfig.SEEKER_EXPECTED_PHONE, TestConfig.SEEKER_EXPECTED_CV);
    }

    @Test
    @Order(12)
    @DisplayName("TC-E2E-012 Minh can upload valid CV")
    void tc012_seekerCanUploadValidCv() {
        loginAsSeeker();
        seekerPage.openProfile();
        seekerPage.uploadCv(TestConfig.VALID_CV_PATH);
        seekerPage.assertProfileLoaded();
        seekerPage.assertProfileInfo(TestConfig.SEEKER_EXPECTED_FULLNAME, TestConfig.SEEKER_EXPECTED_PHONE, TestConfig.VALID_CV_PATH.getFileName().toString());
    }

    @Test
    @Order(13)
    @DisplayName("TC-E2E-013 Minh can update seeker information")
    void tc013_seekerCanUpdateTextProfile() {
        loginAsSeeker();
        seekerPage.openProfile();
        seekerPage.updateProfileInfo(TestConfig.SEEKER_EXPECTED_FULLNAME, TestConfig.SEEKER_EXPECTED_PHONE);
        seekerPage.assertProfileLoaded();
        seekerPage.assertProfileInfo(TestConfig.SEEKER_EXPECTED_FULLNAME, TestConfig.SEEKER_EXPECTED_PHONE, TestConfig.VALID_CV_PATH.getFileName().toString());
    }

    @Test
    @Order(14)
    @DisplayName("TC-E2E-014 Minh can filter jobs by exact seeded criteria")
    void tc014_seekerCanFilterJobs() {
        loginAsSeeker();
        jobPage.openList();
        jobPage.searchByTitleAndCategory(TestConfig.SEARCH_TITLE, String.valueOf(TestConfig.CATEGORY_PARENT_ID));
        jobPage.expectUrlContains("/home/posting/search");
        jobPage.assertListContainsJob(TestConfig.PUBLIC_POSTING_TITLE);
    }

    @Test
    @Order(15)
    @Tag("smoke")
    @DisplayName("TC-E2E-015 Minh can open the exact job detail by title")
    void tc015_seekerCanOpenExactJobDetail() {
        loginAsSeeker();
        jobPage.openList();
        jobPage.searchByTitle(TestConfig.PUBLIC_POSTING_TITLE);
        jobPage.assertListContainsJob(TestConfig.PUBLIC_POSTING_TITLE);
        jobPage.openDetailByTitle(TestConfig.PUBLIC_POSTING_TITLE);
        jobPage.assertDetailLoaded();
        assertTrue(jobPage.hasPostingTitle(TestConfig.PUBLIC_POSTING_TITLE));
    }

    @Test
    @Order(16)
    @DisplayName("TC-E2E-016 Job detail shows the exact header and employer summary")
    void tc016_jobDetailShowsCorrectEmployerSummary() {
        loginAsSeeker();
        jobPage.openSeededDetail();
        jobPage.assertHeaderInfo(
                TestConfig.PUBLIC_POSTING_TITLE,
                TestConfig.JOB_EXPECTED_WAGE,
                TestConfig.JOB_EXPECTED_LOCATION,
                TestConfig.JOB_EXPECTED_EXPERIENCE);
        jobPage.assertEmployerSummary(
                TestConfig.INTEGRATION_EMPLOYER_NAME,
                TestConfig.JOB_EXPECTED_EMPLOYER_SCALE,
                TestConfig.JOB_EXPECTED_EMPLOYER_ADDRESS);
    }

    @Test
    @Order(17)
    @DisplayName("TC-E2E-017 Minh can open the correct employer page from job detail")
    void tc017_seekerCanOpenCorrectEmployerPage() {
        loginAsSeeker();
        jobPage.openSeededDetail();
        jobPage.openEmployerPageFromDetail();
        jobPage.assertEmployerPageLoaded(TestConfig.INTEGRATION_EMPLOYER_NAME, TestConfig.JOB_EXPECTED_EMPLOYER_ADDRESS);
    }

    @Test
    @Order(18)
    @DisplayName("TC-E2E-018 Job detail shows exact key information")
    void tc018_jobDetailShowsCorrectKeyInformation() {
        loginAsSeeker();
        jobPage.openSeededDetail();
        jobPage.assertDetailLoaded();
        jobPage.assertJobGeneralInfo(
                TestConfig.JOB_EXPECTED_LEVEL,
                TestConfig.JOB_EXPECTED_EXPERIENCE,
                TestConfig.JOB_EXPECTED_QUANTITY,
                TestConfig.JOB_EXPECTED_TYPE,
                TestConfig.JOB_EXPECTED_GENDER,
                TestConfig.JOB_EXPECTED_CATEGORY);
    }

    @Test
    @Order(19)
    @DisplayName("TC-E2E-019 Seeker can open applied history")
    void tc019_seekerCanOpenAppliedHistory() {
        loginAsSeeker();
        seekerPage.openAppliedHistory();
        seekerPage.assertAppliedHistoryLoaded();
    }

    @Test
    @Order(20)
    @DisplayName("TC-E2E-020 Seeker can open saved jobs")
    void tc020_seekerCanOpenSavedJobs() {
        loginAsSeeker();
        seekerPage.openSavedJobs();
        seekerPage.assertSavedJobsLoaded();
    }

    @Test
    @Order(21)
    @DisplayName("TC-E2E-021 Seeker cannot open admin category page")
    void tc021_seekerCannotOpenAdminPage() {
        loginAsSeeker();
        adminPage.openCategories();
        assertTrue(page.url().contains("/account/accessdenied"));
    }

    @Test
    @Order(22)
    @Tag("smoke")
    @DisplayName("TC-E2E-022 Employer can login")
    void tc022_employerCanLogin() {
        loginAsEmployer();
        assertTrue(page.url().contains("/employer"));
    }

    @Test
    @Order(23)
    @DisplayName("TC-E2E-023 Employer dashboard loads")
    void tc023_employerDashboardLoads() {
        loginAsEmployer();
        employerPage.openDashboard();
        employerPage.expectUrlContains("/employer/dashboard");
    }

    @Test
    @Order(24)
    @Tag("smoke")
    @DisplayName("TC-E2E-024 Employer job list loads")
    void tc024_employerJobListLoads() {
        loginAsEmployer();
        employerPage.openJobs();
        employerPage.assertJobListLoaded();
    }

    @Test
    @Order(25)
    @DisplayName("TC-E2E-025 Employer add job page loads")
    void tc025_employerAddJobPageLoads() {
        loginAsEmployer();
        employerPage.openAddJob();
        employerPage.expectUrlContains("/employer/job/add");
        employerPage.expectBodyContains("Recruitment Posts Details");
    }

    @Test
    @Order(26)
    @DisplayName("TC-E2E-026 Employer can create job with unique title")
    void tc026_employerCanCreateUniqueJob() {
        loginAsEmployer();
        employerPage.openAddJob();
        String title = uniqueJobTitle("create");
        employerPage.createJob(title);
        employerPage.expectUrlContains("/employer/job");
        assertTrue(employerPage.hasJobRow(title));
    }

    @Test
    @Order(27)
    @DisplayName("TC-E2E-027 Employer can open edit page from created job row")
    void tc027_employerCanOpenEditPageByJobTitle() {
        loginAsEmployer();
        employerPage.openAddJob();
        String title = uniqueJobTitle("edit-open");
        employerPage.createJob(title);
        employerPage.openJobs();
        employerPage.openEditJobByTitle(title);
        employerPage.expectUrlContains("/employer/job/update/");
        employerPage.expectBodyContains("Recruitment Posts Details");
    }

    @Test
    @Order(28)
    @DisplayName("TC-E2E-028 Employer can update created job title")
    void tc028_employerCanUpdateCreatedJobTitle() {
        loginAsEmployer();
        employerPage.openAddJob();
        String originalTitle = uniqueJobTitle("edit");
        String updatedTitle = originalTitle + "-updated";
        employerPage.createJob(originalTitle);
        employerPage.openJobs();
        employerPage.openEditJobByTitle(originalTitle);
        employerPage.updateCurrentJobTitle(updatedTitle);
        employerPage.expectUrlContains("/employer/job/update/");
        employerPage.expectAnyBodyContains("SUCCESS", updatedTitle, "Recruitment Posts Details");
    }

    @Test
    @Order(29)
    @DisplayName("TC-E2E-029 Employer applicants page loads for seeded posting")
    void tc029_employerApplicantListLoads() {
        loginAsEmployer();
        employerPage.openApplicants(TestConfig.INTEGRATION_POSTING_ID);
        employerPage.assertApplicantsPageLoaded();
    }

    @Test
    @Order(30)
    @DisplayName("TC-E2E-030 Employer company profile loads")
    void tc030_employerCompanyProfileLoads() {
        loginAsEmployer();
        employerPage.openCompanyProfile();
        employerPage.assertCompanyPageLoaded();
    }

    @Test
    @Order(31)
    @DisplayName("TC-E2E-031 Employer update company page loads")
    void tc031_employerUpdateCompanyPageLoads() {
        loginAsEmployer();
        employerPage.openUpdateCompany(TestConfig.INTEGRATION_EMPLOYER_ID);
        employerPage.expectUrlContains("/employer/company/update/");
        employerPage.expectAnyBodyContains("Company Details", "Update");
    }

    @Test
    @Order(32)
    @DisplayName("TC-E2E-032 Employer cannot open admin category page")
    void tc032_employerCannotOpenAdminPage() {
        loginAsEmployer();
        adminPage.openCategories();
        assertTrue(page.url().contains("/account/accessdenied"));
    }

    @Test
    @Order(33)
    @Tag("smoke")
    @DisplayName("TC-E2E-033 Admin can login")
    void tc033_adminCanLogin() {
        loginAsAdmin();
        assertTrue(page.url().contains("/admin"));
    }

    @Test
    @Order(34)
    @DisplayName("TC-E2E-034 Admin dashboard loads")
    void tc034_adminDashboardLoads() {
        loginAsAdmin();
        adminPage.openDashboard();
        adminPage.expectUrlContains("/admin/dashboard");
    }

    @Test
    @Order(35)
    @Tag("smoke")
    @DisplayName("TC-E2E-035 Admin account list loads")
    void tc035_adminAccountListLoads() {
        loginAsAdmin();
        adminPage.openAccounts();
        adminPage.assertAccountPageLoaded();
    }

    @Test
    @Order(36)
    @Tag("smoke")
    @DisplayName("TC-E2E-036 Admin company list loads")
    void tc036_adminCompanyListLoads() {
        loginAsAdmin();
        adminPage.openCompanies();
        adminPage.assertCompanyPageLoaded();
    }

    @Test
    @Order(37)
    @DisplayName("TC-E2E-037 Admin can open company detail from seeded company")
    void tc037_adminCanOpenCompanyDetailByName() {
        loginAsAdmin();
        adminPage.openCompanies();
        adminPage.openCompanyDetailByName(TestConfig.INTEGRATION_EMPLOYER_NAME);
        adminPage.expectUrlContains("/admin/company/detail/");
        adminPage.expectAnyBodyContains("Company Detail", TestConfig.INTEGRATION_EMPLOYER_NAME);
    }

    @Test
    @Order(38)
    @Tag("smoke")
    @DisplayName("TC-E2E-038 Admin job list loads")
    void tc038_adminJobListLoads() {
        loginAsAdmin();
        adminPage.openJobs();
        adminPage.assertJobPageLoaded();
    }

    @Test
    @Order(39)
    @DisplayName("TC-E2E-039 Admin can open seeded job detail by title")
    void tc039_adminCanOpenJobDetailByTitle() {
        loginAsAdmin();
        adminPage.openJobs();
        adminPage.openJobDetailByTitle(TestConfig.INTEGRATION_POSTING_TITLE);
        adminPage.expectUrlContains("/admin/job/detail/");
        adminPage.expectAnyBodyContains("Recruitment Posts Detail", TestConfig.INTEGRATION_POSTING_TITLE, "Job Information");
    }

    @Test
    @Order(40)
    @Tag("smoke")
    @DisplayName("TC-E2E-040 Admin category page loads")
    void tc040_adminCategoryPageLoads() {
        loginAsAdmin();
        adminPage.openCategories();
        adminPage.assertCategoryPageLoaded();
    }

    @Test
    @Order(41)
    @DisplayName("TC-E2E-041 Admin category children page loads")
    void tc041_adminCategoryChildrenPageLoads() {
        loginAsAdmin();
        adminPage.openCategoryChildren(TestConfig.CATEGORY_PARENT_ID);
        adminPage.expectUrlContains("/admin/category/");
    }

    @Test
    @Order(42)
    @Tag("integration")
    @DisplayName("TC-E2E-042 Admin can create unique parent category")
    void tc042_adminCanCreateUniqueParentCategory() {
        loginAsAdmin();
        adminPage.openCategories();
        String name = uniqueCategoryName("parent");
        adminPage.addParentCategory(name);
        assertTrue(adminPage.hasCategoryRow(name));
    }

    @Test
    @Order(43)
    @Tag("integration")
    @DisplayName("TC-E2E-043 Admin duplicate parent category shows warning flow")
    void tc043_adminDuplicateParentCategoryWarningFlowWorks() {
        loginAsAdmin();
        adminPage.openCategories();
        String name = uniqueCategoryName("duplicate");
        adminPage.addParentCategory(name);
        assertTrue(adminPage.hasCategoryRow(name));
        adminPage.addParentCategory(name);
        adminPage.expectAnyBodyContains("EXIST", "already exists", "Category List");
    }

    @Test
    @Order(44)
    @Tag("integration")
    @DisplayName("TC-E2E-044 Admin can update unique category by name")
    void tc044_adminCanUpdateCategoryByName() {
        loginAsAdmin();
        adminPage.openCategories();
        String originalName = uniqueCategoryName("update");
        String updatedName = originalName + "-new";
        adminPage.addParentCategory(originalName);
        assertTrue(adminPage.hasCategoryRow(originalName));
        adminPage.updateCategoryByName(originalName, updatedName);
        assertTrue(adminPage.hasCategoryRow(updatedName));
        assertFalse(adminPage.hasCategoryRow(originalName));
    }

    @Test
    @Order(45)
    @Tag("integration")
    @DisplayName("TC-E2E-045 Admin can delete unique category by name")
    void tc045_adminCanDeleteUniqueCategoryByName() {
        loginAsAdmin();
        adminPage.openCategories();
        String name = uniqueCategoryName("delete");
        adminPage.addParentCategory(name);
        assertTrue(adminPage.hasCategoryRow(name));
        adminPage.deleteCategoryByName(name);
        assertFalse(adminPage.hasCategoryRow(name));
    }

    @Test
    @Order(46)
    @DisplayName("TC-E2E-046 Admin existing category row is visible")
    void tc046_adminExistingCategoryRowIsVisible() {
        loginAsAdmin();
        adminPage.openCategories();
        assertTrue(adminPage.hasCategoryRow(TestConfig.EXISTING_CATEGORY_NAME));
    }

    @Test
    @Order(47)
    @Tag("integration")
    @DisplayName("TC-E2E-047 Cross-role seeded posting is visible to seeker detail")
    void tc047_crossRoleSeededPostingVisibleToSeeker() {
        loginAsSeeker();
        jobPage.openDetail(TestConfig.INTEGRATION_POSTING_ID);
        jobPage.assertDetailLoaded();
        assertTrue(jobPage.hasPostingTitle(TestConfig.INTEGRATION_POSTING_TITLE));
    }

    @Test
    @Order(48)
    @Tag("integration")
    @DisplayName("TC-E2E-048 Cross-role seeded posting is visible to employer applicants")
    void tc048_crossRoleSeededPostingVisibleToEmployerApplicants() {
        loginAsEmployer();
        employerPage.openApplicants(TestConfig.INTEGRATION_POSTING_ID);
        employerPage.assertApplicantsPageLoaded();
    }

    @Test
    @Order(49)
    @Tag("integration")
    @DisplayName("TC-E2E-049 Cross-role seeded employer is visible to admin company detail")
    void tc049_crossRoleSeededEmployerVisibleToAdmin() {
        loginAsAdmin();
        adminPage.openCompanyDetail(TestConfig.INTEGRATION_EMPLOYER_ID);
        adminPage.expectAnyBodyContains("Company Detail", TestConfig.INTEGRATION_EMPLOYER_NAME);
    }

    @Test
    @Order(50)
    @Tag("smoke")
    @Tag("integration")
    @DisplayName("TC-E2E-050 Main regression smoke across public, seeker, employer, admin routes")
    void tc050_mainRegressionSmokeAcrossMainFlows() {
        jobPage.openSeededDetail();
        jobPage.assertDetailLoaded();

        loginAsSeeker();
        seekerPage.openProfile();
        seekerPage.assertProfileLoaded();

        closeCurrentSession();
        initializeSession();

        loginAsEmployer();
        employerPage.openJobs();
        employerPage.assertJobListLoaded();

        closeCurrentSession();
        initializeSession();

        loginAsAdmin();
        adminPage.openJobs();
        adminPage.assertJobPageLoaded();
    }
}
