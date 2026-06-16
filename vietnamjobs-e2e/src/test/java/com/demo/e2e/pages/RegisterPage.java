package com.demo.e2e.pages;

import com.microsoft.playwright.Page;

public class RegisterPage extends BasePage {

    public RegisterPage(Page page) {
        super(page);
    }

    public void open() {
        open("/account/register");
    }

    public void registerSeeker(String username, String email, String password) {
        click("input[type='radio'][value='ROLE_SEEKER']");
        fill("#name", username);
        fill("#email", email);
        fill("#newPassword", password);
        fill("#confirmPassword", password);
        click("#submitRegister");
        page.waitForLoadState();
        captureStep("Submit seeker registration for " + username);
    }

    public void registerEmployer(String username, String email, String password) {
        click("input[type='radio'][value='ROLE_EMPLOYER']");
        fill("#name", username);
        fill("#email", email);
        fill("#newPassword", password);
        fill("#confirmPassword", password);
        click("#submitRegister");
        page.waitForLoadState();
        captureStep("Submit employer registration for " + username);
    }

    public void assertLoaded() {
        expectBodyContains("Register");
        expectBodyContains("Login Now");
    }
}
