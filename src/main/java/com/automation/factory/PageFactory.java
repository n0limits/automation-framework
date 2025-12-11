package com.automation.factory;

import com.automation.pages.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PageFactory {

    public static <T extends BasePage> T createPage(Class<T> pageClass) {
        try {
            T page = pageClass.getDeclaredConstructor().newInstance();
            log.info("Created page instance: {}", pageClass.getSimpleName());
            return page;
        } catch (Exception e) {
            log.error("Failed to create page instance: {}", pageClass.getSimpleName(), e);
            throw new RuntimeException("Page instantiation failed", e);
        }
    }

    public static LoginPage getLoginPage() {
        return createPage(LoginPage.class);
    }

    public static DashboardPage getDashboardPage() {
        return createPage(DashboardPage.class);
    }

}
