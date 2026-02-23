package com.automation.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Domain-specific API client for Trading operations
 * Provides high-level API for trading-related endpoints
 *
 * Features:
 * - Trading pairs endpoint
 * - Order management (create, get, cancel)
 * - Account balance
 * - Trade history
 * - Allure reporting integration
 *
 * Usage:
 * <pre>
 * TradingAPIClient client = new TradingAPIClient();
 * Response response = client.getTradingPairs();
 * List<TradingPair> pairs = response.jsonPath().getList(".", TradingPair.class);
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class TradingAPIClient {

    /**
     * Get all available trading pairs
     *
     * @return Response containing trading pairs
     */
    @Step("GET /trading-pairs - Get all trading pairs")
    public Response getTradingPairs() {
        log.info("Getting all trading pairs");
        return APIClient.getRequestSpec()
                .when()
                .get("/trading-pairs");
    }

    /**
     * Get specific trading pair by symbol
     *
     * @param symbol Trading pair symbol (e.g., "BTCUSD")
     * @return Response containing trading pair details
     */
    @Step("GET /trading-pairs/{symbol} - Get trading pair: {symbol}")
    public Response getTradingPair(String symbol) {
        log.info("Getting trading pair: {}", symbol);
        return APIClient.getRequestSpec()
                .pathParam("symbol", symbol)
                .when()
                .get("/trading-pairs/{symbol}");
    }

    /**
     * Create a new market order
     *
     * @param symbol   Trading pair symbol
     * @param side     Order side (BUY/SELL)
     * @param quantity Order quantity
     * @return Response containing order details
     */
    @Step("POST /orders - Create market order: {side} {quantity} {symbol}")
    public Response createMarketOrder(String symbol, String side, double quantity) {
        log.info("Creating market order: {} {} {}", side, quantity, symbol);

        Map<String, Object> requestBody = Map.of(
                "symbol", symbol,
                "side", side,
                "type", "MARKET",
                "quantity", quantity
        );

        return APIClient.getRequestSpec()
                .body(requestBody)
                .when()
                .post("/orders");
    }

    /**
     * Create a new limit order
     *
     * @param symbol   Trading pair symbol
     * @param side     Order side (BUY/SELL)
     * @param quantity Order quantity
     * @param price    Limit price
     * @return Response containing order details
     */
    @Step("POST /orders - Create limit order: {side} {quantity} {symbol} @ {price}")
    public Response createLimitOrder(String symbol, String side, double quantity, double price) {
        log.info("Creating limit order: {} {} {} @ {}", side, quantity, symbol, price);

        Map<String, Object> requestBody = Map.of(
                "symbol", symbol,
                "side", side,
                "type", "LIMIT",
                "quantity", quantity,
                "price", price
        );

        return APIClient.getRequestSpec()
                .body(requestBody)
                .when()
                .post("/orders");
    }

    /**
     * Get order by ID
     *
     * @param orderId Order ID
     * @return Response containing order details
     */
    @Step("GET /orders/{orderId} - Get order: {orderId}")
    public Response getOrder(String orderId) {
        log.info("Getting order: {}", orderId);
        return APIClient.getRequestSpec()
                .pathParam("orderId", orderId)
                .when()
                .get("/orders/{orderId}");
    }

    /**
     * Get all orders for authenticated user
     *
     * @param authToken Authentication token
     * @return Response containing list of orders
     */
    @Step("GET /orders - Get all orders")
    public Response getAllOrders(String authToken) {
        log.info("Getting all orders");
        return APIClient.withAuth(authToken)
                .when()
                .get("/orders");
    }

    /**
     * Get orders with filters
     *
     * @param authToken Authentication token
     * @param symbol    Trading pair symbol (optional)
     * @param status    Order status filter (optional)
     * @return Response containing filtered orders
     */
    @Step("GET /orders - Get orders (symbol: {symbol}, status: {status})")
    public Response getOrders(String authToken, String symbol, String status) {
        log.info("Getting orders with filters - symbol: {}, status: {}", symbol, status);

        var request = APIClient.withAuth(authToken);

        if (symbol != null && !symbol.isEmpty()) {
            request = request.queryParam("symbol", symbol);
        }

        if (status != null && !status.isEmpty()) {
            request = request.queryParam("status", status);
        }

        return request.when().get("/orders");
    }

    /**
     * Cancel an order
     *
     * @param authToken Authentication token
     * @param orderId   Order ID to cancel
     * @return Response containing cancellation result
     */
    @Step("DELETE /orders/{orderId} - Cancel order: {orderId}")
    public Response cancelOrder(String authToken, String orderId) {
        log.info("Cancelling order: {}", orderId);
        return APIClient.withAuth(authToken)
                .pathParam("orderId", orderId)
                .when()
                .delete("/orders/{orderId}");
    }

    /**
     * Get account balance
     *
     * @param authToken Authentication token
     * @return Response containing account balance
     */
    @Step("GET /account/balance - Get account balance")
    public Response getAccountBalance(String authToken) {
        log.info("Getting account balance");
        return APIClient.withAuth(authToken)
                .when()
                .get("/account/balance");
    }

    /**
     * Get trade history
     *
     * @param authToken Authentication token
     * @return Response containing trade history
     */
    @Step("GET /trades - Get trade history")
    public Response getTradeHistory(String authToken) {
        log.info("Getting trade history");
        return APIClient.withAuth(authToken)
                .when()
                .get("/trades");
    }

    /**
     * Get trade history with pagination
     *
     * @param authToken Authentication token
     * @param page      Page number
     * @param pageSize  Number of items per page
     * @return Response containing paginated trade history
     */
    @Step("GET /trades - Get trade history (page: {page}, pageSize: {pageSize})")
    public Response getTradeHistory(String authToken, int page, int pageSize) {
        log.info("Getting trade history - page: {}, pageSize: {}", page, pageSize);
        return APIClient.withAuth(authToken)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .when()
                .get("/trades");
    }

    /**
     * Get ticker price for a symbol
     *
     * @param symbol Trading pair symbol
     * @return Response containing current ticker price
     */
    @Step("GET /ticker/{symbol} - Get ticker price: {symbol}")
    public Response getTickerPrice(String symbol) {
        log.info("Getting ticker price for: {}", symbol);
        return APIClient.getRequestSpec()
                .pathParam("symbol", symbol)
                .when()
                .get("/ticker/{symbol}");
    }

    /**
     * Get 24-hour statistics for a symbol
     *
     * @param symbol Trading pair symbol
     * @return Response containing 24h statistics
     */
    @Step("GET /ticker/24hr/{symbol} - Get 24h statistics: {symbol}")
    public Response get24HourStats(String symbol) {
        log.info("Getting 24h statistics for: {}", symbol);
        return APIClient.getRequestSpec()
                .pathParam("symbol", symbol)
                .when()
                .get("/ticker/24hr/{symbol}");
    }

    /**
     * Get market depth (order book)
     *
     * @param symbol Trading pair symbol
     * @param limit  Number of price levels (default: 100)
     * @return Response containing order book
     */
    @Step("GET /depth/{symbol} - Get market depth: {symbol} (limit: {limit})")
    public Response getMarketDepth(String symbol, int limit) {
        log.info("Getting market depth for {} (limit: {})", symbol, limit);
        return APIClient.getRequestSpec()
                .pathParam("symbol", symbol)
                .queryParam("limit", limit)
                .when()
                .get("/depth/{symbol}");
    }
}
