package com.demo.e2e.pages;

import com.microsoft.playwright.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmployerPage extends BasePage {

    public EmployerPage(Page page) {
        super(page);
    }

    public void openDashboard() {
        open("/employer/dashboard");
    }

    public void openJobs() {
        open("/employer/job");
    }

    public void openAddJob() {
        open("/employer/job/add");
    }

    public void openEditJob(int postingId) {
        open("/employer/job/update/" + postingId);
    }

    public void openApplicants(int postingId) {
        open("/employer/apply/" + postingId);
    }

    public void openCompanyProfile() {
        open("/employer/company");
    }

    public void openAddCompany() {
        open("/employer/company/add");
    }

    public void openUpdateCompany(int employerId) {
        open("/employer/company/update/" + employerId);
    }

    public void assertJobListLoaded() {
        expectBodyContains("Recruitment Posts List");
    }

    public void createJob(String title) {
        fill("input[placeholder='Enter title...']", title);
        fill("input[placeholder='EX: Male, Female']", "Any");
        fill("input[placeholder='Enter quantity...']", "2");
        fill("input.datetimepicker-input", LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        fill("textarea[placeholder='Enter a description...']", "E2E regression job description");
        click("button[type='submit']");
        page.waitForLoadState();
        captureStep("Create job " + title);
    }

    public boolean hasJobRow(String title) {
        filterCurrentDataTable(title);
        return hasRowText(title);
    }

    public void openEditJobByTitle(String title) {
        filterCurrentDataTable(title);
        locator("tbody tr:has-text('" + title + "')").first().locator("a.btn.btn-info").click();
        page.waitForLoadState();
        captureStep("Open edit job by title " + title);
    }

    public void updateCurrentJobTitle(String title) {
        fill("input[placeholder='Enter title...']", title);
        click("button[type='submit']");
        page.waitForLoadState();
        captureStep("Update current job title to " + title);
    }

    public void assertApplicantsPageLoaded() {
        expectUrlContains("/employer/apply/");
        expectAnyBodyContains("List", "Invite", "Reject", "Viewed", "Not seen");
    }

    public void assertCompanyPageLoaded() {
        expectAnyBodyContains("Company", "Company Details", "Add Company");
    }
}
