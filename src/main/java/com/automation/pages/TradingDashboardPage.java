package com.automation.pages;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Trading Dashboard Page Object
 * Handles all trading operations including:
 * - Trading pair selection
 * - Order placement (Buy/Sell)
 * - Price monitoring
 * - Balance viewing
 * - Order history access
 */
@Slf4j
public class TradingDashboardPage extends BasePage {
    // Selectors - Trading Pairs
    private static final String TRADING_PAIR_SELECTOR = ".trading-pair-selector, #tradingPair";
    private static final String TRADING_PAIR_OPTION = ".trading-pair-option";
    private static final String SEARCH_PAIR_INPUT = "input[placeholder*='Search'], #searchPair";
    private static final String CURRENT_PAIR_DISPLAY = ".current-pair, .selected-pair";

    // Selectors - Price Display
    private static final String CURRENT_PRICE = ".current-price, #currentPrice";
    private static final String BID_PRICE = ".bid-price, #bidPrice";
    private static final String ASK_PRICE = ".ask-price, #askPrice";
    private static final String PRICE_CHART = ".price-chart, #priceChart";
    private static final String PRICE_CHANGE = ".price-change, .price-delta";

    // Selectors - Order Entry
    private static final String BUY_TAB = "button:has-text('Buy'), .buy-tab";
    private static final String SELL_TAB = "button:has-text('Sell'), .sell-tab";
    private static final String ORDER_TYPE_SELECT = "select[name='orderType'], #orderType";
    private static final String AMOUNT_INPUT = "input[name='amount'], #amount";
    private static final String PRICE_INPUT = "input[name='price'], #price";
    private static final String TOTAL_DISPLAY = ".total-amount, #totalAmount";
    private static final String BUY_BUTTON = "button:has-text('Buy'), button.buy-button";
    private static final String SELL_BUTTON = "button:has-text('Sell'), button.sell-button";

    // Selectors - Balance
    private static final String ACCOUNT_BALANCE = ".account-balance, #balance";
    private static final String AVAILABLE_BALANCE = ".available-balance, #availableBalance";
    private static final String BALANCE_CURRENCY = ".balance-currency";

    // Selectors - Order History
    private static final String ORDER_HISTORY_TAB = "button:has-text('Orders'), .orders-tab";
    private static final String OPEN_ORDERS_TABLE = ".open-orders-table, #openOrders";
    private static final String ORDER_ROW = ".order-row, tr.order";
    private static final String CANCEL_ORDER_BUTTON = "button:has-text('Cancel'), .cancel-order";

    // Selectors - Notifications
    private static final String SUCCESS_MESSAGE = ".success-message, .alert-success";
    private static final String ERROR_MESSAGE = ".error-message, .alert-danger";
    private static final String WARNING_MESSAGE = ".warning-message, .alert-warning";

    @Step("Navigate to trading dashboard")
    public TradingDashboardPage open() {
        navigateTo(config.getBaseUrl() + "/dashboard");
        waitForDashboardLoad();
        return this;
    }

    @Step("Wait for dashboard to load")
    private void waitForDashboardLoad() {
        log.info("Waiting for trading dashboard to load");
        waitForPageLoad();
        waitForVisible(TRADING_PAIR_SELECTOR);
        waitForVisible(CURRENT_PRICE);
        log.info("Trading dashboard loaded successfully");
    }

    // ========== Trading Pair Selection ==========

    @Step("Select trading pair: {symbol}")
    public TradingDashboardPage selectTradingPair(String symbol) {
        log.info("Selecting trading pair: {}", symbol);
        click(TRADING_PAIR_SELECTOR);
        waitForVisible(TRADING_PAIR_OPTION);
        clickByText(symbol);
        waitForPriceUpdate();
        return this;
    }

    @Step("Search for trading pair: {searchTerm}")
    public TradingDashboardPage searchTradingPair(String searchTerm) {
        log.info("Searching for trading pair: {}", searchTerm);
        click(TRADING_PAIR_SELECTOR);
        fill(SEARCH_PAIR_INPUT, searchTerm);
        return this;
    }

    @Step("Get current trading pair")
    public String getCurrentTradingPair() {
        String pair = getText(CURRENT_PAIR_DISPLAY);
        log.info("Current trading pair: {}", pair);
        return pair;
    }

    @Step("Get available trading pairs")
    public List<String> getAvailableTradingPairs() {
        log.info("Getting available trading pairs");
        click(TRADING_PAIR_SELECTOR);
        waitForVisible(TRADING_PAIR_OPTION);

        List<String> pairs = page.locator(TRADING_PAIR_OPTION)
                .allTextContents()
                .stream()
                .filter(text -> !text.trim().isEmpty())
                .collect(Collectors.toList());

        log.info("Found {} trading pairs", pairs.size());
        pressKey("Escape"); // Close dropdown
        return pairs;
    }

    // ========== Price Information ==========

    @Step("Get current price")
    public double getCurrentPrice() {
        String priceText = getText(CURRENT_PRICE).replaceAll("[^0-9.]", "");
        double price = Double.parseDouble(priceText);
        log.info("Current price: {}", price);
        return price;
    }

    @Step("Get bid price")
    public double getBidPrice() {
        String priceText = getText(BID_PRICE).replaceAll("[^0-9.]", "");
        double price = Double.parseDouble(priceText);
        log.info("Bid price: {}", price);
        return price;
    }

    @Step("Get ask price")
    public double getAskPrice() {
        String priceText = getText(ASK_PRICE).replaceAll("[^0-9.]", "");
        double price = Double.parseDouble(priceText);
        log.info("Ask price: {}", price);
        return price;
    }

    @Step("Get price change percentage")
    public String getPriceChange() {
        String change = getText(PRICE_CHANGE);
        log.info("Price change: {}", change);
        return change;
    }

    @Step("Wait for price update")
    private void waitForPriceUpdate() {
        log.debug("Waiting for price update");
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    @Step("Check if price chart is visible")
    public boolean isPriceChartVisible() {
        return isVisible(PRICE_CHART);
    }

    // ========== Order Placement ==========

    @Step("Place buy order: {amount} at {price}")
    public TradingDashboardPage placeBuyOrder(double amount, double price) {
        log.info("Placing buy order: {} at {}", amount, price);

        // Switch to buy tab
        click(BUY_TAB);

        // Fill order details
        fill(AMOUNT_INPUT, String.valueOf(amount));
        fill(PRICE_INPUT, String.valueOf(price));

        // Verify total
        double total = getTotalAmount();
        log.info("Order total: {}", total);

        // Submit order
        click(BUY_BUTTON);
        waitForOrderConfirmation();

        return this;
    }

    @Step("Place sell order: {amount} at {price}")
    public TradingDashboardPage placeSellOrder(double amount, double price) {
        log.info("Placing sell order: {} at {}", amount, price);

        // Switch to sell tab
        click(SELL_TAB);

        // Fill order details
        fill(AMOUNT_INPUT, String.valueOf(amount));
        fill(PRICE_INPUT, String.valueOf(price));

        // Verify total
        double total = getTotalAmount();
        log.info("Order total: {}", total);

        // Submit order
        click(SELL_BUTTON);
        waitForOrderConfirmation();

        return this;
    }

    @Step("Place market buy order: {amount}")
    public TradingDashboardPage placeMarketBuyOrder(double amount) {
        log.info("Placing market buy order: {}", amount);

        click(BUY_TAB);
        selectOrderType("Market");
        fill(AMOUNT_INPUT, String.valueOf(amount));
        click(BUY_BUTTON);
        waitForOrderConfirmation();

        return this;
    }

    @Step("Place market sell order: {amount}")
    public TradingDashboardPage placeMarketSellOrder(double amount) {
        log.info("Placing market sell order: {}", amount);

        click(SELL_TAB);
        selectOrderType("Market");
        fill(AMOUNT_INPUT, String.valueOf(amount));
        click(SELL_BUTTON);
        waitForOrderConfirmation();

        return this;
    }

    @Step("Select order type: {orderType}")
    public TradingDashboardPage selectOrderType(String orderType) {
        log.info("Selecting order type: {}", orderType);
        selectByText(ORDER_TYPE_SELECT, orderType);
        return this;
    }

    @Step("Get total amount")
    public double getTotalAmount() {
        String totalText = getText(TOTAL_DISPLAY).replaceAll("[^0-9.]", "");
        double total = Double.parseDouble(totalText);
        log.debug("Total amount: {}", total);
        return total;
    }

    @Step("Wait for order confirmation")
    private void waitForOrderConfirmation() {
        log.info("Waiting for order confirmation");
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);

        // Check for success or error message
        if (isVisible(SUCCESS_MESSAGE)) {
            log.info("Order placed successfully");
        } else if (isVisible(ERROR_MESSAGE)) {
            String error = getText(ERROR_MESSAGE);
            log.error("Order failed: {}", error);
        }
    }

    // ========== Balance Information ==========

    @Step("Get account balance")
    public double getAccountBalance() {
        String balanceText = getText(ACCOUNT_BALANCE).replaceAll("[^0-9.]", "");
        double balance = Double.parseDouble(balanceText);
        log.info("Account balance: {}", balance);
        return balance;
    }

    @Step("Get available balance")
    public double getAvailableBalance() {
        String balanceText = getText(AVAILABLE_BALANCE).replaceAll("[^0-9.]", "");
        double balance = Double.parseDouble(balanceText);
        log.info("Available balance: {}", balance);
        return balance;
    }

    @Step("Get balance currency")
    public String getBalanceCurrency() {
        String currency = getText(BALANCE_CURRENCY);
        log.info("Balance currency: {}", currency);
        return currency;
    }

    // ========== Order History ==========

    @Step("Open order history")
    public TradingDashboardPage openOrderHistory() {
        log.info("Opening order history");
        click(ORDER_HISTORY_TAB);
        waitForVisible(OPEN_ORDERS_TABLE);
        return this;
    }

    @Step("Get open orders count")
    public int getOpenOrdersCount() {
        openOrderHistory();
        int count = getElementCount(ORDER_ROW);
        log.info("Open orders count: {}", count);
        return count;
    }

    @Step("Get order status at index: {index}")
    public String getOrderStatus(int index) {
        openOrderHistory();
        String status = page.locator(ORDER_ROW).nth(index)
                .locator(".order-status, td:nth-child(5)")
                .textContent();
        log.info("Order {} status: {}", index, status);
        return status;
    }

    @Step("Cancel order at index: {index}")
    public TradingDashboardPage cancelOrder(int index) {
        log.info("Cancelling order at index: {}", index);
        openOrderHistory();
        page.locator(ORDER_ROW).nth(index)
                .locator(CANCEL_ORDER_BUTTON)
                .click();
        waitForOrderConfirmation();
        return this;
    }

    @Step("Cancel all open orders")
    public TradingDashboardPage cancelAllOrders() {
        log.info("Cancelling all open orders");
        openOrderHistory();
        int orderCount = getElementCount(ORDER_ROW);

        for (int i = 0; i < orderCount; i++) {
            cancelOrder(0); // Always cancel first order since list updates after each cancellation
        }

        log.info("[PASS] All orders cancelled");
        return this;
    }

    // ========== Validation & Assertions ==========

    @Step("Check if success message is visible")
    public boolean isSuccessMessageVisible() {
        return isVisible(SUCCESS_MESSAGE);
    }

    @Step("Check if error message is visible")
    public boolean isErrorMessageVisible() {
        return isVisible(ERROR_MESSAGE);
    }

    @Step("Check if warning message is visible")
    public boolean isWarningMessageVisible() {
        return isVisible(WARNING_MESSAGE);
    }

    @Step("Get success message")
    public String getSuccessMessage() {
        String message = getText(SUCCESS_MESSAGE);
        log.info("Success message: {}", message);
        return message;
    }

    @Step("Get error message")
    public String getErrorMessage() {
        String message = getText(ERROR_MESSAGE);
        log.error("Error message: {}", message);
        return message;
    }

    @Step("Get warning message")
    public String getWarningMessage() {
        String message = getText(WARNING_MESSAGE);
        log.warn("Warning message: {}", message);
        return message;
    }

    @Step("Verify dashboard is loaded")
    public boolean isDashboardLoaded() {
        return isVisible(TRADING_PAIR_SELECTOR)
                && isVisible(CURRENT_PRICE)
                && isVisible(BUY_BUTTON);
    }

    @Step("Wait for order to appear in history")
    public TradingDashboardPage waitForOrderInHistory(int expectedCount) {
        log.info("Waiting for {} orders in history", expectedCount);
        openOrderHistory();
        waitForElementCount(ORDER_ROW, expectedCount);
        return this;
    }
}
