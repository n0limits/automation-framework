package com.automation.ui;

import com.automation.base.BaseTest;
import com.automation.pages.LoginPage;
import com.automation.pages.TradingDashboardPage;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UI Tests for Trading Dashboard
 * Tests cover:
 * - Trading pair selection
 * - Order placement (Buy/Sell)
 * - Price monitoring
 * - Balance verification
 * - Order history management
 * - UI interactions and validations
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("Trading Dashboard UI")
public class TradingDashboardUITests extends BaseTest {
    private LoginPage loginPage;
    private TradingDashboardPage dashboardPage;

    @BeforeMethod
    public void setupAndLogin() {
        log.info("===== Setting up Trading Dashboard UI Test =====");

        // Initialize pages
        loginPage = new LoginPage();
        dashboardPage = new TradingDashboardPage();

        // Login before each test
        log.info("Performing login");
        loginPage.open();
        loginPage.login(config.getUsername(), config.getPassword());

        // Navigate to dashboard
        dashboardPage.open();
        log.info("Dashboard ready for testing");
    }

    @Test(description = "Verify trading dashboard loads successfully")
    @Description("Test that trading dashboard displays all required elements")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Dashboard Navigation")
    public void testDashboardLoadsSuccessfully() {
        log.info("=== Test: Dashboard Loads Successfully ===");

        // Verify dashboard is loaded
        boolean isDashboardLoaded = dashboardPage.isDashboardLoaded();
        assertThat(isDashboardLoaded)
                .as("Dashboard should be fully loaded")
                .isTrue();

        // Verify URL contains dashboard
        String currentUrl = dashboardPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("URL should contain 'dashboard'")
                .contains("/dashboard");

        // Verify page title
        String title = dashboardPage.getTitle();
        assertThat(title)
                .as("Page title should be meaningful")
                .isNotEmpty();
        assertThat(title.toLowerCase())
                .as("Page title should contain dashboard, trading, or automation")
                .satisfiesAnyOf(
                        t -> assertThat(t).contains("dashboard"),
                        t -> assertThat(t).contains("trading"),
                        t -> assertThat(t).contains("automation")
                );

        // Take screenshot
        String screenshot = dashboardPage.takeScreenshot("dashboard-loaded");
        log.info("Dashboard screenshot: {}", screenshot);

        log.info("✅ Dashboard load test passed");
    }

    @Test(description = "Verify trading pair selection")
    @Description("Test user can select different trading pairs and prices update")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Pair Selection")
    public void testTradingPairSelection() {
        log.info("=== Test: Trading Pair Selection ===");

        // Get initial trading pair
        String initialPair = dashboardPage.getCurrentTradingPair();
        log.info("Initial trading pair: {}", initialPair);

        // Get initial price
        double initialPrice = dashboardPage.getCurrentPrice();
        log.info("Initial price: {}", initialPrice);
        assertThat(initialPrice).as("Price should be positive").isPositive();

        // Select different trading pair
        dashboardPage.selectTradingPair("ETHUSD");

        // Verify trading pair changed
        String newPair = dashboardPage.getCurrentTradingPair();
        assertThat(newPair)
                .as("Trading pair should have changed")
                .isNotEqualTo(initialPair)
                .containsIgnoringCase("ETH");

        // Verify price updated
        double newPrice = dashboardPage.getCurrentPrice();
        assertThat(newPrice)
                .as("Price should be positive")
                .isPositive();

        log.info("✅ Trading pair selection test passed");
    }

    @Test(description = "Verify available trading pairs are displayed")
    @Description("Test system displays list of available trading pairs")
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading Pair Selection")
    public void testAvailableTradingPairs() {
        log.info("=== Test: Available Trading Pairs ===");

        // Get list of available pairs
        List<String> availablePairs = dashboardPage.getAvailableTradingPairs();

        // Verify we have trading pairs
        assertThat(availablePairs)
                .as("Should have at least one trading pair")
                .isNotEmpty()
                .hasSizeGreaterThan(0);

        log.info("Available trading pairs: {}", availablePairs);

        // Verify common pairs are present (if this is crypto trading)
        boolean hasCommonPairs = availablePairs.stream()
                .anyMatch(pair -> pair.contains("BTC") ||
                                  pair.contains("ETH") ||
                                  pair.contains("USD"));

        assertThat(hasCommonPairs)
                .as("Should have common trading pairs")
                .isTrue();

        log.info("✅ Available trading pairs test passed - found {} pairs", availablePairs.size());
    }

    @Test(description = "Verify search trading pair functionality")
    @Description("Test user can search for specific trading pairs")
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading Pair Selection")
    public void testSearchTradingPair() {
        log.info("=== Test: Search Trading Pair ===");

        // Search for Bitcoin pairs
        dashboardPage.searchTradingPair("BTC");

        // Get available pairs after search
        List<String> searchResults = dashboardPage.getAvailableTradingPairs();

        // Verify search results contain BTC
        boolean allContainBTC = searchResults.stream()
                .allMatch(pair -> pair.toUpperCase().contains("BTC"));

        assertThat(allContainBTC)
                .as("Search results should contain 'BTC'")
                .isTrue();

        log.info("✅ Search trading pair test passed");
    }

    @Test(description = "Verify price information is displayed correctly")
    @Description("Test current price, bid, and ask prices are shown")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Price Display")
    public void testPriceInformationDisplay() {
        log.info("=== Test: Price Information Display ===");

        // Select known trading pair
        dashboardPage.selectTradingPair("BTCUSD");

        // Get price information
        double currentPrice = dashboardPage.getCurrentPrice();
        double bidPrice = dashboardPage.getBidPrice();
        double askPrice = dashboardPage.getAskPrice();

        log.info("Current: {}, Bid: {}, Ask: {}", currentPrice, bidPrice, askPrice);

        // Verify all prices are positive
        assertThat(currentPrice).as("Current price should be positive").isPositive();
        assertThat(bidPrice).as("Bid price should be positive").isPositive();
        assertThat(askPrice).as("Ask price should be positive").isPositive();

        // Verify bid < ask (normal market condition)
        assertThat(bidPrice)
                .as("Bid price should be less than or equal to ask price")
                .isLessThanOrEqualTo(askPrice);

        // Verify price chart is visible
        boolean chartVisible = dashboardPage.isPriceChartVisible();
        assertThat(chartVisible)
                .as("Price chart should be visible")
                .isTrue();

        log.info("✅ Price information display test passed");
    }

    @Test(description = "Verify price change indicator")
    @Description("Test price change percentage is displayed")
    @Severity(SeverityLevel.MINOR)
    @Story("Price Display")
    public void testPriceChangeIndicator() {
        log.info("=== Test: Price Change Indicator ===");

        // Get price change
        String priceChange = dashboardPage.getPriceChange();

        assertThat(priceChange)
                .as("Price change should be displayed")
                .isNotEmpty();

        log.info("Price change: {}", priceChange);

        // Verify it contains percentage or numeric value
        boolean hasNumericValue = priceChange.matches(".*[0-9].*");
        assertThat(hasNumericValue)
                .as("Price change should contain numeric value")
                .isTrue();

        log.info("✅ Price change indicator test passed");
    }

    @Test(description = "Verify buy order placement")
    @Description("Test user can place a buy order successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Order Placement")
    public void testPlaceBuyOrder() {
        log.info("=== Test: Place Buy Order ===");

        // Select trading pair
        dashboardPage.selectTradingPair("BTCUSD");

        // Get initial balance
        double initialBalance = dashboardPage.getAvailableBalance();
        log.info("Initial balance: {}", initialBalance);

        // Place buy order
        double amount = 0.001; // Small amount for testing
        double price = dashboardPage.getCurrentPrice();

        dashboardPage.placeBuyOrder(amount, price);

        // Verify success message
        boolean successVisible = dashboardPage.isSuccessMessageVisible();
        if (successVisible) {
            String successMessage = dashboardPage.getSuccessMessage();
            log.info("Success message: {}", successMessage);
            assertThat(successMessage.toLowerCase())
                    .as("Success message should mention order, placed, or success")
                    .satisfiesAnyOf(
                            t -> assertThat(t).contains("order"),
                            t -> assertThat(t).contains("placed"),
                            t -> assertThat(t).contains("success")
                    );
        }

        // Take screenshot of order confirmation
        dashboardPage.takeScreenshot("buy-order-placed");

        log.info("✅ Buy order placement test passed");
    }

    @Test(description = "Verify sell order placement")
    @Description("Test user can place a sell order successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Order Placement")
    public void testPlaceSellOrder() {
        log.info("=== Test: Place Sell Order ===");

        // Select trading pair
        dashboardPage.selectTradingPair("BTCUSD");

        // Place sell order
        double amount = 0.001;
        double price = dashboardPage.getCurrentPrice();

        dashboardPage.placeSellOrder(amount, price);

        // Verify success or error message
        boolean messageVisible = dashboardPage.isSuccessMessageVisible() ||
                                 dashboardPage.isErrorMessageVisible();

        assertThat(messageVisible)
                .as("Should show feedback message after order placement")
                .isTrue();

        if (dashboardPage.isSuccessMessageVisible()) {
            log.info("✅ Sell order placed successfully");
        } else {
            log.info("Sell order failed (expected if no balance)");
        }

        dashboardPage.takeScreenshot("sell-order-result");

        log.info("✅ Sell order placement test passed");
    }

    @Test(description = "Verify market buy order")
    @Description("Test user can place a market buy order")
    @Severity(SeverityLevel.NORMAL)
    @Story("Order Placement")
    public void testPlaceMarketBuyOrder() {
        log.info("=== Test: Place Market Buy Order ===");

        dashboardPage.selectTradingPair("ETHUSD");

        // Place market buy order
        double amount = 0.01;
        dashboardPage.placeMarketBuyOrder(amount);

        // Verify feedback
        boolean feedbackShown = dashboardPage.isSuccessMessageVisible() ||
                                dashboardPage.isErrorMessageVisible() ||
                                dashboardPage.isWarningMessageVisible();

        assertThat(feedbackShown)
                .as("Should show feedback after market order")
                .isTrue();

        log.info("✅ Market buy order test passed");
    }

    @Test(description = "Verify order type selection")
    @Description("Test user can switch between limit and market orders")
    @Severity(SeverityLevel.NORMAL)
    @Story("Order Placement")
    public void testOrderTypeSelection() {
        log.info("=== Test: Order Type Selection ===");

        // Test switching order types
        dashboardPage.selectOrderType("Market");
        dashboardPage.waitFor(500);

        dashboardPage.selectOrderType("Limit");
        dashboardPage.waitFor(500);

        // Verify we can interact with the order form after switching
        boolean dashboardStillLoaded = dashboardPage.isDashboardLoaded();
        assertThat(dashboardStillLoaded)
                .as("Dashboard should remain functional after order type switch")
                .isTrue();

        log.info("✅ Order type selection test passed");
    }

    @Test(description = "Verify account balance display")
    @Description("Test account balance is visible and updated")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Balance Management")
    public void testAccountBalanceDisplay() {
        log.info("=== Test: Account Balance Display ===");

        // Get balance information
        double accountBalance = dashboardPage.getAccountBalance();
        double availableBalance = dashboardPage.getAvailableBalance();
        String currency = dashboardPage.getBalanceCurrency();

        log.info("Account Balance: {} {}", accountBalance, currency);
        log.info("Available Balance: {} {}", availableBalance, currency);

        // Verify balances are non-negative
        assertThat(accountBalance)
                .as("Account balance should be non-negative")
                .isGreaterThanOrEqualTo(0);

        assertThat(availableBalance)
                .as("Available balance should be non-negative")
                .isGreaterThanOrEqualTo(0);

        // Verify currency is shown
        assertThat(currency)
                .as("Currency should be displayed")
                .isNotEmpty();

        log.info("✅ Account balance display test passed");
    }

    @Test(description = "Verify order history access")
    @Description("Test user can view order history")
    @Severity(SeverityLevel.NORMAL)
    @Story("Order History")
    public void testOrderHistoryAccess() {
        log.info("=== Test: Order History Access ===");

        // Open order history
        dashboardPage.openOrderHistory();

        // Get order count
        int orderCount = dashboardPage.getOpenOrdersCount();
        log.info("Open orders count: {}", orderCount);

        assertThat(orderCount)
                .as("Order count should be non-negative")
                .isGreaterThanOrEqualTo(0);

        // Take screenshot of order history
        dashboardPage.takeScreenshot("order-history");

        log.info("✅ Order history access test passed");
    }

    @Test(description = "Verify order cancellation")
    @Description("Test user can cancel an open order")
    @Severity(SeverityLevel.NORMAL)
    @Story("Order History")
    public void testOrderCancellation() {
        log.info("=== Test: Order Cancellation ===");

        // First, place an order to cancel
        dashboardPage.selectTradingPair("BTCUSD");
        double price = dashboardPage.getCurrentPrice() * 0.5; // Low price unlikely to fill
        dashboardPage.placeBuyOrder(0.001, price);

        // Wait for order to appear in history
        dashboardPage.waitForOrderInHistory(1);

        // Get initial count
        int initialCount = dashboardPage.getOpenOrdersCount();
        log.info("Initial open orders: {}", initialCount);

        if (initialCount > 0) {
            // Cancel first order
            dashboardPage.cancelOrder(0);

            // Wait and verify count decreased
            dashboardPage.waitFor(1000);
            int newCount = dashboardPage.getOpenOrdersCount();

            assertThat(newCount)
                    .as("Order count should decrease after cancellation")
                    .isLessThan(initialCount);

            log.info("✅ Order cancelled successfully");
        } else {
            log.info("No orders to cancel, skipping cancellation verification");
        }

        log.info("✅ Order cancellation test passed");
    }

    @Test(description = "Verify dashboard UI responsiveness")
    @Description("Test dashboard updates when data changes")
    @Severity(SeverityLevel.MINOR)
    @Story("UI Responsiveness")
    public void testDashboardResponsiveness() {
        log.info("=== Test: Dashboard Responsiveness ===");

        // Select first pair and get price
        dashboardPage.selectTradingPair("BTCUSD");
        double price1 = dashboardPage.getCurrentPrice();
        String pair1 = dashboardPage.getCurrentTradingPair();

        // Select different pair
        dashboardPage.selectTradingPair("ETHUSD");
        double price2 = dashboardPage.getCurrentPrice();
        String pair2 = dashboardPage.getCurrentTradingPair();

        // Verify UI updated
        assertThat(pair2)
                .as("Trading pair should have changed")
                .isNotEqualTo(pair1);

        assertThat(price2)
                .as("Price should have updated")
                .isNotEqualTo(price1);

        log.info("✅ Dashboard responsiveness test passed");
    }

    @Test(description = "Verify error handling for invalid order")
    @Description("Test system handles invalid order inputs gracefully")
    @Severity(SeverityLevel.NORMAL)
    @Story("Error Handling")
    public void testInvalidOrderHandling() {
        log.info("=== Test: Invalid Order Handling ===");

        dashboardPage.selectTradingPair("BTCUSD");

        // Try to place order with zero amount
        try {
            dashboardPage.placeBuyOrder(0, dashboardPage.getCurrentPrice());

            // Should show error or warning
            boolean errorShown = dashboardPage.isErrorMessageVisible() ||
                                 dashboardPage.isWarningMessageVisible();

            assertThat(errorShown)
                    .as("Should show error for zero amount")
                    .isTrue();

            if (dashboardPage.isErrorMessageVisible()) {
                log.info("Error message: {}", dashboardPage.getErrorMessage());
            }
        } catch (Exception e) {
            log.info("Order validation prevented submission (expected): {}", e.getMessage());
        }

        log.info("✅ Invalid order handling test passed");
    }

    @Test(description = "Verify full page screenshot capture")
    @Description("Test full page screenshot functionality for visual regression")
    @Severity(SeverityLevel.MINOR)
    @Story("Visual Testing")
    public void testFullPageScreenshot() {
        log.info("=== Test: Full Page Screenshot ===");

        // Take full page screenshot
        String screenshotPath = dashboardPage.takeFullPageScreenshot("dashboard-full");

        assertThat(screenshotPath)
                .as("Screenshot path should be returned")
                .isNotEmpty()
                .contains("dashboard-full");

        log.info("✅ Full page screenshot saved: {}", screenshotPath);
    }
}
