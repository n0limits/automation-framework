package com.automation.pages;

import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * Login Page Object
 */
@Slf4j
public class LoginPage extends BasePage {
    // Selectors
    private static final String USERNAME_INPUT = "input[name='username'], #username";
    private static final String PASSWORD_INPUT = "input[name='password'], #password";
    private static final String LOGIN_BUTTON = "button[type='submit'], button:has-text('Login')";
    private static final String ERROR_MESSAGE = ".error-message, .alert-danger";
    
    @Step("Navigate to login page")
    public LoginPage open() {
        navigateTo(config.getBaseUrl() + "/login");
        return this;
    }
    
    @Step("Login with username: {username}")
    public void login(String username, String password) {
        log.info("Logging in with username: {}", username);
        fill(USERNAME_INPUT, username);
        fill(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
        waitForPageLoad();
    }
    
    public boolean isErrorVisible() {
        return isVisible(ERROR_MESSAGE);
    }
    
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
