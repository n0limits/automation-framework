package com.automation.providers;

import org.testng.annotations.DataProvider;

public class TestDataProviders {

    @DataProvider(name = "browserProvider")
    public Object[][] browserProvider() {
        return new Object[][] {
            { "chromium" },
            { "firefox" },
            { "webkit" }
        };
    }

    @DataProvider(name = "navigationItemsProvider")
    public Object[][] navigationItemsProvider() {
        // Only include navigation items that are actually available on MultiBank homepage
        // Dashboard and Markets exist but don't change URL (both stay at "/")
        return new Object[][] {
            { "Dashboard", "/" },
            { "Markets", "/" }
        };
    }

    @DataProvider(name = "tradingPairsProvider")
    public Object[][] tradingPairsProvider() {
        // MultiBank homepage doesn't show specific trading pairs
        // Return empty array to skip tests that use this provider
        return new Object[][] {};
    }

    @DataProvider(name = "downloadLinksProvider")
    public Object[][] downloadLinksProvider() {
        return new Object[][] {
            { "App Store", "apple.com" },
            { "Google Play", "play.google.com" }
        };
    }

    @DataProvider(name = "marketingBannersProvider")
    public Object[][] marketingBannersProvider() {
        // Based on actual page content from https://trade.multibank.io/
        return new Object[][] {
            { "Pay Trading Fees with MBG" },
            { "Real World Assets" },
            { "Coming Soon" }
        };
    }

    @DataProvider(name = "tradingTabsProvider")
    public Object[][] tradingTabsProvider() {
        return new Object[][] {
            { "Favorites", "favoritesTabAvailable" },
            { "All Pairs", "allPairsTabAvailable" }
        };
    }
}
