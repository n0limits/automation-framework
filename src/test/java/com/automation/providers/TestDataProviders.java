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
        return new Object[][] {
            { "Dashboard", "dashboard" },
            { "Markets", "markets" },
            { "Trade", "trade" },
            { "Features", "features" },
            { "About Us", "about" },
            { "Support", "support" }
        };
    }

    @DataProvider(name = "tradingPairsProvider")
    public Object[][] tradingPairsProvider() {
        return new Object[][] {
            { "BTC/USDT" },
            { "ETH/USDT" },
            { "SOL/USDT" },
            { "XRP/USDT" }
        };
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
        return new Object[][] {
            { "Instant Buy" },
            { "Card/Wire Transfer" },
            { "24/7 Support" }
        };
    }
}
