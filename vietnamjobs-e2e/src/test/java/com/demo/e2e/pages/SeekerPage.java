package com.demo.e2e.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeekerPage extends BasePage {

    public SeekerPage(Page page) {
        super(page);
    }

    public void openProfile() {
        open("/seeker/profile/infor");
    }

    public void openAppliedHistory() {
        open("/seeker/profile/postingapplied");
    }

    public void openSavedJobs() {
        open("/seeker/profile/postingsaved");
    }

    public void uploadCv(Path file) {
        upload("#inputField", file);
        click("form[action='/seeker/profile/uploadCV'] button[type='submit']");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Upload CV " + file.getFileName());
    }

    public void updateAvatarAndProfile(Path file, String fullName, String phone) {
        upload("#account-upload", file);
        fill("input[name='fullname']", fullName);
        fill("input[name='phone']", phone);
        click("form[action='/seeker/profile/uploadAvatar'] button[type='submit']");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Update seeker avatar and profile");
    }

    public void updateProfileInfo(String fullName, String phone) {
        fill("input[name='fullname']", fullName);
        fill("input[name='phone']", phone);
        click("form[action='/seeker/profile/uploadAvatar'] button[type='submit']");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        captureStep("Update seeker profile text fields");
    }

    public void assertProfileInfo(String fullName, String phone, String cvName) {
        assertEquals(fullName, locator("input[name='fullname']").first().inputValue());
        assertEquals(phone, locator("input[name='phone']").first().inputValue());
        assertTrue(bodyContains(cvName), "Expected profile page to contain CV file name: " + cvName);
        captureStep("Assert seeker profile info");
    }

    public void assertProfileLoaded() {
        expectBodyContains("Profile Page");
        expectBodyContains("Update profile");
    }

    public void assertAppliedHistoryLoaded() {
        expectUrlContains("/seeker/profile/postingapplied");
    }

    public void assertSavedJobsLoaded() {
        expectUrlContains("/seeker/profile/postingsaved");
    }
}
