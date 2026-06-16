package com.demo.e2e.pages;

import com.demo.e2e.config.TestConfig;
import com.demo.e2e.evidence.EvidenceRecorder;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    public void open(String path) {
        String url = TestConfig.url(path);
        PlaywrightException lastError = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                page.navigate(url, new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(Math.max(TestConfig.TIMEOUT_MS, 45000)));
                captureStep("Open " + path);
                return;
            } catch (PlaywrightException ex) {
                lastError = ex;
                if (attempt == 2) {
                    throw ex;
                }
                page.waitForTimeout(1500);
            }
        }
        throw lastError;
    }

    protected Locator locator(String selector) {
        return page.locator(selector);
    }

    protected void click(String selector) {
        locator(selector).first().click();
        captureStep("Click " + selector);
    }

    protected void fill(String selector, String value) {
        locator(selector).first().fill(value);
        captureStep("Fill " + selector);
    }

    protected void select(String selector, String value) {
        locator(selector).first().selectOption(value);
        captureStep("Select " + selector + " = " + value);
    }

    protected void upload(String selector, Path path) {
        locator(selector).first().setInputFiles(path);
        captureStep("Upload " + path.getFileName() + " to " + selector);
    }

    protected boolean isVisible(String selector) {
        try {
            return locator(selector).first().isVisible();
        } catch (PlaywrightException ex) {
            return false;
        }
    }

    protected boolean bodyContains(String text) {
        String bodyText = page.locator("body").textContent();
        return bodyText != null && bodyText.contains(text);
    }

    protected String bodyText() {
        String bodyText = page.locator("body").textContent();
        return bodyText == null ? "" : bodyText;
    }

    public void expectBodyContains(String text) {
        assertTrue(bodyContains(text), "Expected page body to contain: " + text);
        captureStep("Assert body contains: " + text);
    }

    public void expectAnyBodyContains(String... values) {
        String bodyText = bodyText();
        assertTrue(Arrays.stream(values).anyMatch(bodyText::contains),
                "Expected page body to contain one of: " + Arrays.toString(values));
        captureStep("Assert body contains one of " + Arrays.toString(values));
    }

    public void expectUrlContains(String fragment) {
        assertTrue(page.url().contains(fragment), "Expected URL to contain: " + fragment + " but was " + page.url());
        captureStep("Assert URL contains: " + fragment);
    }

    public void expectVisible(String selector) {
        assertTrue(isVisible(selector), "Expected visible selector: " + selector);
        captureStep("Assert visible: " + selector);
    }

    public boolean hasRowText(String text) {
        try {
            return locator("tbody tr:has-text('" + text + "')").count() > 0;
        } catch (PlaywrightException ex) {
            return false;
        }
    }

    protected void filterCurrentDataTable(String term) {
        if (isVisible("input[type='search']")) {
            fill("input[type='search']", term);
            page.waitForTimeout(300);
            captureStep("Filter DataTable with term: " + term);
        }
    }

    public void waitForToast() {
        page.waitForTimeout(800);
        captureStep("Wait for toast");
    }

    protected void captureStep(String action) {
        EvidenceRecorder.captureStep(action, page);
    }
}
