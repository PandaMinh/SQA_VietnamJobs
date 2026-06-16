package com.demo.e2e.pages;

import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {

    public LoginPage(Page page) {
        super(page);
    }

    public void open() {
        open("/account/login");
    }

    public void login(String username, String password) {
        fill("#username", username);
        fill("#password", password);
        locator("input[type='submit'][value='Đăng nhập']").first()
                .click(new com.microsoft.playwright.Locator.ClickOptions().setNoWaitAfter(true));
        captureStep("Click login submit");
        page.waitForTimeout(1500);
        captureStep("Login with username " + username);
    }

    public void assertLoaded() {
        expectBodyContains("Login");
        expectBodyContains("Register");
    }
}
