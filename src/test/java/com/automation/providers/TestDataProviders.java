package com.automation.providers;

import com.automation.testdata.TestDataFactory;
import org.testng.annotations.DataProvider;

public class TestDataProviders {

    @DataProvider(name = "navigationItemsProvider")
    public Object[][] navigationItemsProvider() {
        return new Object[][] {
            { "Dashboard", "/" },
            { "Markets", "/" }
        };
    }

    @DataProvider(name = "allNavigationItemsProvider")
    public Object[][] allNavigationItemsProvider() {
        return new Object[][] {
            { "Dashboard", "trade.multibank.io" },
            { "Markets",   "trade.multibank.io" },
            { "Trade",     "/trade" },
            { "Features",  "/features" },
            { "About Us",  "/about" },
            { "Support",   "/support" }
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

    @DataProvider(name = "tradingTabsProvider")
    public Object[][] tradingTabsProvider() {
        return new Object[][] {
            { "All Pairs" },
            { "Favorites" }
        };
    }

    @DataProvider(name = "expectedColumnsProvider")
    public Object[][] expectedColumnsProvider() {
        return new Object[][] {
            { "Pair" },
            { "Max Leverage" },
            { "Change 24h" }
        };
    }

    @DataProvider(name = "authenticatedEndpointsProvider")
    public Object[][] authenticatedEndpointsProvider() {
        return new Object[][] {
            { "balance" },
            { "trade-history" },
            { "cancel-order" }
        };
    }

    @DataProvider(name = "userDataProvider")
    public Object[][] userDataProvider() {
        TestDataFactory factory = new TestDataFactory();
        return new Object[][] {
            { factory.username(), factory.email() },
            { factory.username(), factory.email() },
            { factory.username(), factory.email() }
        };
    }
}
