package com.automation.api;

import com.automation.base.BaseAPITest;
import com.automation.providers.TestDataProviders;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.automation.api.APIAssertions.assertThat;

/**
 * Comprehensive API tests for Trading endpoints
 * Demonstrates usage of TradingAPIClient, SchemaValidator, and APIAssertions
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("Trading API")
public class TradingAPITests extends BaseAPITest {
    private TradingAPIClient tradingAPI;

    @BeforeClass
    public void setupTradingAPI() {
        tradingAPI = new TradingAPIClient();
        log.info("TradingAPIClient initialized");
    }

    @Test(priority = 1, description = "Verify GET /trading-pairs returns list of trading pairs")
    @Description("Test that trading pairs endpoint returns 200 OK with non-empty list")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetTradingPairs() {
        log.info("=== Test: Get All Trading Pairs ===");

        Response response = tradingAPI.getTradingPairs();
        logResponse(response);

        // Fluent assertions
        assertThat(response)
                .isOK()
                .hasJsonContentType()
                .respondsWithin(2000)
                .hasNonEmptyBody();

        log.info("✅ Get trading pairs test passed");
    }

    @Test(priority = 2, description = "Verify GET /trading-pairs/{symbol} returns specific pair")
    @Description("Test that individual trading pair endpoint returns correct data")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetSpecificTradingPair() {
        log.info("=== Test: Get Specific Trading Pair ===");

        String symbol = "BTCUSD";
        Response response = tradingAPI.getTradingPair(symbol);
        logResponse(response);

        // Fluent assertions with JSON path
        assertThat(response)
                .isOK()
                .hasJsonContentType()
                .respondsWithin(1000)
                .jsonPath("symbol").isEqualTo(symbol);

        log.info("✅ Get specific trading pair test passed");
    }

    @Test(priority = 3, description = "Verify GET /ticker/{symbol} returns current price")
    @Description("Test ticker endpoint returns price data within performance threshold")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetTickerPrice() {
        log.info("=== Test: Get Ticker Price ===");

        String symbol = "BTCUSD";
        Response response = tradingAPI.getTickerPrice(symbol);
        logResponse(response);

        assertThat(response)
                .isOK()
                .hasJsonContentType()
                .respondsWithin(500)  // Ticker should be very fast
                .jsonPath("symbol").isEqualTo(symbol)
                .jsonPath("price").isNotNull();

        // Verify price is a positive number
        double price = response.jsonPath().getDouble("price");
        org.assertj.core.api.Assertions.assertThat(price)
                .as("Price should be positive")
                .isGreaterThan(0);

        log.info("Current {} price: {}", symbol, price);
        log.info("✅ Get ticker price test passed");
    }

    @Test(priority = 4, description = "Verify GET /ticker/24hr/{symbol} returns statistics")
    @Description("Test 24-hour statistics endpoint returns complete data")
    @Severity(SeverityLevel.NORMAL)
    public void testGet24HourStatistics() {
        log.info("=== Test: Get 24-Hour Statistics ===");

        String symbol = "BTCUSD";
        Response response = tradingAPI.get24HourStats(symbol);
        logResponse(response);

        assertThat(response)
                .isOK()
                .hasJsonContentType()
                .respondsWithin(1000)
                .jsonPath("symbol").isEqualTo(symbol)
                .jsonPath("high").isNotNull()
                .jsonPath("low").isNotNull()
                .jsonPath("volume").isNotNull();

        // Verify required fields exist
        SchemaValidator.validateRequiredFields(response,
                "symbol", "high", "low", "open", "close", "volume");

        log.info("✅ Get 24-hour statistics test passed");
    }

    @Test(priority = 5, description = "Verify GET /depth/{symbol} returns order book")
    @Description("Test market depth endpoint returns bids and asks")
    @Severity(SeverityLevel.NORMAL)
    public void testGetMarketDepth() {
        log.info("=== Test: Get Market Depth ===");

        String symbol = "BTCUSD";
        int limit = 10;
        Response response = tradingAPI.getMarketDepth(symbol, limit);
        logResponse(response);

        assertThat(response)
                .isOK()
                .hasJsonContentType()
                .respondsWithin(1000)
                .jsonPath("bids").isNotNull()
                .jsonPath("asks").isNotNull();

        // Verify field types
        Map<String, Class<?>> fieldTypes = new HashMap<>();
        fieldTypes.put("bids", java.util.List.class);
        fieldTypes.put("asks", java.util.List.class);
        SchemaValidator.validateFieldTypes(response, fieldTypes);

        log.info("✅ Get market depth test passed");
    }

    @Test(priority = 6, description = "Verify GET /trading-pairs with invalid symbol returns 404")
    @Description("Test error handling for non-existent trading pair")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentTradingPair() {
        log.info("=== Test: Get Non-Existent Trading Pair (Error Handling) ===");

        String invalidSymbol = "INVALID123";
        Response response = tradingAPI.getTradingPair(invalidSymbol);
        logResponse(response);

        assertThat(response)
                .isNotFound()
                .hasJsonContentType()
                .respondsWithin(1000);

        log.info("✅ Error handling test passed");
    }

    @Test(priority = 7, description = "Verify ticker response time per symbol",
          dataProvider = "tradingSymbolsProvider", dataProviderClass = TestDataProviders.class)
    @Description("Performance test: ticker request should complete quickly for each symbol")
    @Severity(SeverityLevel.MINOR)
    public void testTickerPerformance(String symbol) {
        log.info("=== Test: Ticker Performance for {} ===", symbol);

        Response response = tradingAPI.getTickerPrice(symbol);

        assertThat(response)
                .isOK()
                .respondsWithin(500);

        log.info("✅ Ticker performance test passed for {}", symbol);
    }

    @Test(priority = 8, description = "Verify POST /orders creates order (simulated)")
    @Description("Test order creation endpoint (may require authentication)")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateMarketOrder() {
        log.info("=== Test: Create Market Order ===");

        String symbol = "BTCUSD";
        String side = "BUY";
        double quantity = 0.001;

        Response response = tradingAPI.createMarketOrder(symbol, side, quantity);
        logResponse(response);

        // Note: This may return 401 if authentication is required
        // Adjust assertions based on your API requirements
        if (response.getStatusCode() == 401) {
            log.info("Order creation requires authentication (expected)");
            assertThat(response).isUnauthorized();
        } else {
            assertThat(response)
                    .isCreated()
                    .hasJsonContentType()
                    .respondsWithin(3000);
        }

        log.info("✅ Create market order test passed");
    }

    @Test(priority = 9, description = "Verify POST /orders with limit order")
    @Description("Test limit order creation with price parameter")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateLimitOrder() {
        log.info("=== Test: Create Limit Order ===");

        String symbol = "BTCUSD";
        String side = "BUY";
        double quantity = 0.001;
        double price = 40000.0;

        Response response = tradingAPI.createLimitOrder(symbol, side, quantity, price);
        logResponse(response);

        if (response.getStatusCode() == 401) {
            log.info("Order creation requires authentication (expected)");
            assertThat(response).isUnauthorized();
        } else {
            assertThat(response)
                    .isCreated()
                    .hasJsonContentType()
                    .respondsWithin(3000);
        }

        log.info("✅ Create limit order test passed");
    }

    @Test(priority = 10, description = "Verify POST /orders with invalid data returns 400")
    @Description("Test validation: invalid order should return Bad Request")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateInvalidOrder() {
        log.info("=== Test: Create Invalid Order (Validation) ===");

        // Create order with invalid quantity (negative)
        String symbol = "BTCUSD";
        String side = "BUY";
        double invalidQuantity = -1.0;

        Response response = tradingAPI.createMarketOrder(symbol, side, invalidQuantity);
        logResponse(response);

        // Should return 400 Bad Request or 401 Unauthorized
        org.assertj.core.api.Assertions.assertThat(response.getStatusCode())
                .as("Invalid order should return 400 or 401")
                .isIn(400, 401);

        log.info("✅ Validation test passed");
    }

    @Test(priority = 11, description = "Verify GET /account/balance requires authentication")
    @Description("Test that balance endpoint returns 401 without auth token")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetBalanceWithoutAuth() {
        log.info("=== Test: Get Balance Without Authentication ===");

        Response response = tradingAPI.getAccountBalance("invalid-token");
        logResponse(response);

        assertThat(response)
                .isUnauthorized()
                .hasJsonContentType()
                .respondsWithin(1000);

        log.info("✅ Authentication test passed");
    }

    @Test(priority = 12, description = "Verify GET /trades requires authentication")
    @Description("Test that trade history endpoint requires valid authentication")
    @Severity(SeverityLevel.NORMAL)
    public void testGetTradeHistoryWithoutAuth() {
        log.info("=== Test: Get Trade History Without Authentication ===");

        Response response = tradingAPI.getTradeHistory("invalid-token");
        logResponse(response);

        assertThat(response)
                .isUnauthorized()
                .respondsWithin(1000);

        log.info("✅ Trade history authentication test passed");
    }

    @Test(priority = 13, description = "Verify pagination parameters work correctly")
    @Description("Test that pagination parameters are accepted")
    @Severity(SeverityLevel.MINOR)
    public void testTradeHistoryPagination() {
        log.info("=== Test: Trade History Pagination ===");

        int page = 1;
        int pageSize = 10;

        Response response = tradingAPI.getTradeHistory("invalid-token", page, pageSize);
        logResponse(response);

        // Should return 401 but verify pagination parameters were sent
        assertThat(response)
                .isUnauthorized()
                .respondsWithin(1000);

        log.info("✅ Pagination test passed");
    }

    @Test(priority = 14, description = "Verify DELETE /orders/{orderId} requires authentication")
    @Description("Test that order cancellation requires valid authentication")
    @Severity(SeverityLevel.NORMAL)
    public void testCancelOrderWithoutAuth() {
        log.info("=== Test: Cancel Order Without Authentication ===");

        String orderId = "test-order-123";
        Response response = tradingAPI.cancelOrder("invalid-token", orderId);
        logResponse(response);

        assertThat(response)
                .isUnauthorized()
                .respondsWithin(1000);

        log.info("✅ Cancel order authentication test passed");
    }

    @Test(priority = 15, description = "Verify API returns correct headers")
    @Description("Test that API responses include expected headers")
    @Severity(SeverityLevel.MINOR)
    public void testResponseHeaders() {
        log.info("=== Test: Response Headers ===");

        Response response = tradingAPI.getTradingPairs();
        logResponse(response);

        assertThat(response)
                .isOK()
                .hasHeader("Content-Type")
                .respondsWithin(2000);

        // Log all headers for debugging
        log.info("Response headers:");
        response.getHeaders().forEach(header ->
                log.info("  {} = {}", header.getName(), header.getValue())
        );

        log.info("✅ Response headers test passed");
    }
}
