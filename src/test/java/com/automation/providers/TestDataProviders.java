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

    @DataProvider(name = "allNavigationItemsProvider")
    public Object[][] allNavigationItemsProvider() {
        return new Object[][] {
            { "Dashboard", "/" },
            { "Markets", "/" },
            { "Trade", "/trade" },
            { "Features", "/features" },
            { "About Us", "/about" },
            { "Support", "/support" }
        };
    }

    @DataProvider(name = "tradingTabsVisibilityProvider")
    public Object[][] tradingTabsVisibilityProvider() {
        return new Object[][] {
            { "Favorites", "favoritesTabAvailable" },
            { "All Pairs", "allPairsTabAvailable" }
        };
    }

    @DataProvider(name = "marketIndicatorsProvider")
    public Object[][] marketIndicatorsProvider() {
        return new Object[][] {
            { "Fear Index", "fearIndexVisible" },
            { "Top Gainers", "topGainersVisible" },
            { "Top Losers", "topLosersVisible" }
        };
    }

    @DataProvider(name = "investmentSectionsProvider")
    public Object[][] investmentSectionsProvider() {
        // Real World Assets is a marketing banner, not a separate investment section
        return new Object[][] {
            { "MBG Token" }
        };
    }

    @DataProvider(name = "downloadLinksVisibilityProvider")
    public Object[][] downloadLinksVisibilityProvider() {
        return new Object[][] {
            { "App Store" },
            { "Google Play" }
        };
    }

    @DataProvider(name = "specialUsernamesProvider")
    public Object[][] specialUsernamesProvider() {
        return new Object[][] {
            { "user@email.com" },
            { "user+test@domain.com" },
            { "user.name@test.com" },
            { "user_123" },
            { "user-name" }
        };
    }

    @DataProvider(name = "transactionTypesProvider")
    public Object[][] transactionTypesProvider() {
        return new Object[][] {
            { "Deposit" },
            { "Withdrawal" }
        };
    }

    @DataProvider(name = "tradingSymbolsProvider")
    public Object[][] tradingSymbolsProvider() {
        return new Object[][] {
            { "BTCUSD" },
            { "ETHUSD" },
            { "XRPUSD" }
        };
    }
}
