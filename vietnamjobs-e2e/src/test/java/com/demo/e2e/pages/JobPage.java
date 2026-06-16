package com.demo.e2e.pages;

import com.demo.e2e.config.TestConfig;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JobPage extends BasePage {

    public JobPage(Page page) {
        super(page);
    }

    public void openList() {
        open("/home/posting");
    }

    public void openDetail(int postingId) {
        open("/home/posting/" + postingId);
    }

    public void searchByTitle(String title) {
        fill("input[name='title']", title);
        click("button[type='submit']");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Search public jobs by title " + title);
    }

    public void searchByTitleAndCategory(String title, String categoryId) {
        fill("input[name='title']", title);
        select("select[name='category']", categoryId);
        click("button[type='submit']");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Search public jobs by title " + title + " and category " + categoryId);
    }

    public void openSeededDetail() {
        openDetail(TestConfig.PUBLIC_POSTING_ID);
    }

    public void saveCurrentJob() {
        click("form[action*='/saveJob/'] button[type='submit']");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Save current job");
    }

    public void openApplyDialog() {
        click("button[data-bs-target='#applyCV']");
        captureStep("Open apply dialog");
    }

    public void submitCurrentApplication() {
        click("form[action*='/applyCV/'] button[type='submit']");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Submit current application");
    }

    public void openDetailByTitle(String title) {
        locator("a[href*='/home/posting/']").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText(title)).first().click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Open job detail by title " + title);
    }

    public void openEmployerPageFromDetail() {
        click("a:has-text('Employer page')");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Open employer page from detail");
    }

    public void assertListContainsJob(String title) {
        assertTrue(page.locator("a[href*='/home/posting/']").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText(title)).count() > 0,
                "Expected job listing to contain title: " + title);
        captureStep("Assert job list contains " + title);
    }

    public void assertEmployerSummary(String employerName, String employerAddress) {
        expectBodyContains(employerName);
        expectBodyContains(employerAddress);
        captureStep("Assert employer summary on job detail");
    }

    public void assertEmployerSummary(String employerName, String employerScale, String employerAddress) {
        expectBodyContains(employerName);
        expectBodyContains(employerScale);
        expectBodyContains(employerAddress);
        captureStep("Assert exact employer summary on job detail");
    }

    public void assertHeaderInfo(String title, String wage, String location, String experience) {
        expectBodyContains(title);
        expectBodyContains(wage);
        expectBodyContains(location);
        expectBodyContains(experience);
        captureStep("Assert exact job header information");
    }

    public void assertJobGeneralInfo(String level, String experience, String quantity, String type, String gender, String category) {
        expectBodyContains(level);
        expectBodyContains(experience);
        expectBodyContains(quantity);
        expectBodyContains(type);
        expectBodyContains(gender);
        expectBodyContains(category);
        captureStep("Assert exact general job info");
    }

    public void assertEmployerPageLoaded(String employerName, String employerAddress) {
        expectAnyBodyContains("Company Details", "Employer detail");
        expectBodyContains(employerName);
        expectBodyContains(employerAddress);
        captureStep("Assert employer public page");
    }

    public boolean hasPostingTitle(String title) {
        return bodyContains(title);
    }

    public boolean hasJobCards() {
        return page.locator("a[href*='/home/posting/']").count() > 0;
    }

    public void assertListLoaded() {
        expectBodyContains("Job Page");
        expectBodyContains("Search");
        assertTrue(hasJobCards(), "Expected at least one job card on the listing page");
    }

    public void assertDetailLoaded() {
        expectBodyContains("Job detail");
        expectBodyContains("Job information");
        expectAnyBodyContains("Apply now", "Save this job");
    }
}
