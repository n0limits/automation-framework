package com.automation.bdd.steps;

import com.automation.api.APIAssertions;
import com.automation.api.SchemaValidator;
import com.automation.api.TradingAPIClient;
import com.automation.bdd.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TradingAPISteps {

    private final ScenarioContext scenarioContext;
    private TradingAPIClient tradingAPI;

    public TradingAPISteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    private TradingAPIClient getClient() {
        if (tradingAPI == null) {
            tradingAPI = new TradingAPIClient();
        }
        return tradingAPI;
    }

    private Response getLastResponse() {
        return scenarioContext.get("lastResponse", Response.class);
    }

    // ===== Given =====

    @Given("the trading API is available")
    public void theTradingApiIsAvailable() {
        getClient();
        log.info("Trading API client ready");
    }

    // ===== When =====

    @When("I request all trading pairs")
    public void iRequestAllTradingPairs() {
        Response response = getClient().getTradingPairs();
        scenarioContext.set("lastResponse", response);
        log.info("GET /trading-pairs - Status: {}", response.getStatusCode());
    }

    @When("I request the trading pair {string}")
    public void iRequestTheTradingPair(String symbol) {
        Response response = getClient().getTradingPair(symbol);
        scenarioContext.set("lastResponse", response);
        log.info("GET /trading-pairs/{} - Status: {}", symbol, response.getStatusCode());
    }

    @When("I request the ticker price for {string}")
    public void iRequestTheTickerPriceFor(String symbol) {
        Response response = getClient().getTickerPrice(symbol);
        scenarioContext.set("lastResponse", response);
        log.info("GET /ticker/{} - Status: {}", symbol, response.getStatusCode());
    }

    @When("I request the 24h statistics for {string}")
    public void iRequestThe24hStatisticsFor(String symbol) {
        Response response = getClient().get24HourStats(symbol);
        scenarioContext.set("lastResponse", response);
        log.info("GET /ticker/24hr/{} - Status: {}", symbol, response.getStatusCode());
    }

    @When("I request the market depth for {string} with limit {int}")
    public void iRequestTheMarketDepthForWithLimit(String symbol, int limit) {
        Response response = getClient().getMarketDepth(symbol, limit);
        scenarioContext.set("lastResponse", response);
        log.info("GET /depth/{} - Status: {}", symbol, response.getStatusCode());
    }

    @When("I create a market order for {string} side {string} quantity {double}")
    public void iCreateAMarketOrder(String symbol, String side, double quantity) {
        Response response = getClient().createMarketOrder(symbol, side, quantity);
        scenarioContext.set("lastResponse", response);
        log.info("POST /orders (market) - Status: {}", response.getStatusCode());
    }

    @When("I create a limit order for {string} side {string} quantity {double} price {double}")
    public void iCreateALimitOrder(String symbol, String side, double quantity, double price) {
        Response response = getClient().createLimitOrder(symbol, side, quantity, price);
        scenarioContext.set("lastResponse", response);
        log.info("POST /orders (limit) - Status: {}", response.getStatusCode());
    }

    @When("I request account balance with token {string}")
    public void iRequestAccountBalanceWithToken(String token) {
        Response response = getClient().getAccountBalance(token);
        scenarioContext.set("lastResponse", response);
        log.info("GET /account/balance - Status: {}", response.getStatusCode());
    }

    @When("I request trade history with token {string}")
    public void iRequestTradeHistoryWithToken(String token) {
        Response response = getClient().getTradeHistory(token);
        scenarioContext.set("lastResponse", response);
        log.info("GET /trades - Status: {}", response.getStatusCode());
    }

    @When("I cancel order {string} with token {string}")
    public void iCancelOrderWithToken(String orderId, String token) {
        Response response = getClient().cancelOrder(token, orderId);
        scenarioContext.set("lastResponse", response);
        log.info("DELETE /orders/{} - Status: {}", orderId, response.getStatusCode());
    }

    @When("I request the authenticated {string} endpoint with token {string}")
    public void iRequestTheAuthenticatedEndpointWithToken(String endpoint, String token) {
        Response response = switch (endpoint) {
            case "balance" -> getClient().getAccountBalance(token);
            case "trade-history" -> getClient().getTradeHistory(token);
            case "cancel-order" -> getClient().cancelOrder(token, "test-order-123");
            default -> throw new IllegalArgumentException("Unknown authenticated endpoint: " + endpoint);
        };
        scenarioContext.set("lastResponse", response);
        log.info("Authenticated endpoint '{}' - Status: {}", endpoint, response.getStatusCode());
    }

    // ===== Then =====

    @Then("the response status code is {int}")
    public void theResponseStatusCodeIs(int expectedCode) {
        APIAssertions.assertThat(getLastResponse()).hasStatusCode(expectedCode);
    }

    @Then("the response is OK")
    public void theResponseIsOK() {
        APIAssertions.assertThat(getLastResponse()).isOK();
    }

    @Then("the response is unauthorized")
    public void theResponseIsUnauthorized() {
        APIAssertions.assertThat(getLastResponse()).isUnauthorized();
    }

    @Then("the response is not found")
    public void theResponseIsNotFound() {
        APIAssertions.assertThat(getLastResponse()).isNotFound();
    }

    @Then("the response has JSON content type")
    public void theResponseHasJsonContentType() {
        APIAssertions.assertThat(getLastResponse()).hasJsonContentType();
    }

    @Then("the response body is not empty")
    public void theResponseBodyIsNotEmpty() {
        APIAssertions.assertThat(getLastResponse()).hasNonEmptyBody();
    }

    @Then("the response time is within {long} ms")
    public void theResponseTimeIsWithinMs(long maxTime) {
        APIAssertions.assertThat(getLastResponse()).respondsWithin(maxTime);
    }

    @Then("the response contains field {string} with value {string}")
    public void theResponseContainsFieldWithValue(String field, String value) {
        APIAssertions.assertThat(getLastResponse()).jsonPath(field).isEqualTo(value);
    }

    @Then("the response contains field {string}")
    public void theResponseContainsField(String field) {
        APIAssertions.assertThat(getLastResponse()).jsonPath(field).isNotNull();
    }

    @Then("the response contains required fields:")
    public void theResponseContainsRequiredFields(List<String> fields) {
        SchemaValidator.validateRequiredFields(getLastResponse(), fields.toArray(new String[0]));
    }

    @Then("the response contains bids and asks arrays")
    public void theResponseContainsBidsAndAsksArrays() {
        Response response = getLastResponse();
        APIAssertions.assertThat(response).jsonPath("bids").isNotNull();
        APIAssertions.assertThat(response).jsonPath("asks").isNotNull();

        Map<String, Class<?>> fieldTypes = new HashMap<>();
        fieldTypes.put("bids", List.class);
        fieldTypes.put("asks", List.class);
        SchemaValidator.validateFieldTypes(response, fieldTypes);
    }

    @Then("the price field is a positive number")
    public void thePriceFieldIsAPositiveNumber() {
        double price = getLastResponse().jsonPath().getDouble("price");
        assertThat(price)
                .as("Price should be positive")
                .isGreaterThan(0);
    }

    @Then("the response has Content-Type header")
    public void theResponseHasContentTypeHeader() {
        APIAssertions.assertThat(getLastResponse()).hasHeader("Content-Type");
    }

    @Then("the response status code is {int} or {int}")
    public void theResponseStatusCodeIsOr(int code1, int code2) {
        int actual = getLastResponse().getStatusCode();
        assertThat(actual)
                .as("Status code should be %d or %d", code1, code2)
                .isIn(code1, code2);
    }
}
