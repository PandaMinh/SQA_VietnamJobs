package com.demo.e2e.pages;

import com.microsoft.playwright.Page;

public class AdminPage extends BasePage {

    public AdminPage(Page page) {
        super(page);
    }

    public void openDashboard() {
        open("/admin/dashboard");
    }

    public void openJobs() {
        open("/admin/job");
    }

    public void openJobDetail(int postingId) {
        open("/admin/job/detail/" + postingId);
    }

    public void openCompanies() {
        open("/admin/company");
    }

    public void openCompanyDetail(int employerId) {
        open("/admin/company/detail/" + employerId);
    }

    public void openAccounts() {
        open("/admin/account");
    }

    public void openCategories() {
        open("/admin/category");
    }

    public void openCategoryChildren(int categoryId) {
        open("/admin/category/" + categoryId);
    }

    public void addParentCategory(String name) {
        click("button[data-target='#modalAdd']");
        fill("#modalAdd input[placeholder='Enter category name...']", name);
        click("#modalAdd button[type='submit']");
        page.waitForLoadState();
        captureStep("Add parent category " + name);
    }

    public void addChildCategory(String name, String parentId) {
        click("button[data-target='#modalAdd']");
        fill("#modalAdd input[placeholder='Enter category name...']", name);
        select("#modalAdd select", parentId);
        click("#modalAdd button[type='submit']");
        page.waitForLoadState();
        captureStep("Add child category " + name + " under " + parentId);
    }

    public void updateCategory(String name) {
        click(".btn-edit");
        fill("#updateName", name);
        click("#modalUpdate button[type='submit']");
        page.waitForLoadState();
        captureStep("Update category to " + name);
    }

    public void deleteFirstCategory() {
        click("a.btn.btn-danger");
        page.waitForLoadState();
        captureStep("Delete first category");
    }

    public void updateCategoryByName(String existingName, String newName) {
        filterCurrentDataTable(existingName);
        locator("tbody tr:has-text('" + existingName + "')").first().locator(".btn-edit").click();
        fill("#updateName", newName);
        click("#modalUpdate button[type='submit']");
        page.waitForLoadState();
        captureStep("Update category " + existingName + " to " + newName);
    }

    public void deleteCategoryByName(String name) {
        filterCurrentDataTable(name);
        locator("tbody tr:has-text('" + name + "')").first().locator("a.btn.btn-danger").click();
        page.waitForLoadState();
        captureStep("Delete category by name " + name);
    }

    public boolean hasCategoryRow(String name) {
        filterCurrentDataTable(name);
        return hasRowText(name);
    }

    public void openCompanyDetailByName(String name) {
        filterCurrentDataTable(name);
        locator("tbody tr:has-text('" + name + "')").first().locator("a.btn.btn-info").click();
        page.waitForLoadState();
        captureStep("Open company detail by name " + name);
    }

    public void openJobDetailByTitle(String title) {
        filterCurrentDataTable(title);
        locator("tbody tr:has-text('" + title + "')").first().locator("a.btn.btn-info").click();
        page.waitForLoadState();
        captureStep("Open admin job detail by title " + title);
    }

    public void assertCategoryPageLoaded() {
        expectBodyContains("Category List");
    }

    public void assertCompanyPageLoaded() {
        expectBodyContains("Company List");
    }

    public void assertAccountPageLoaded() {
        expectBodyContains("Account List");
    }

    public void assertJobPageLoaded() {
        expectBodyContains("Recruitment Posts List");
    }
}
