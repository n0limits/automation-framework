@api @trading-api
Feature: Trading API Endpoints
  As an API consumer
  I want to interact with the trading API
  So that I can retrieve market data and manage orders

  Background:
    Given the trading API is available

  @smoke
  Scenario: GET all trading pairs returns 200 with data
    When I request all trading pairs
    Then the response is OK
    And the response has JSON content type
    And the response body is not empty
    And the response time is within 2000 ms

  Scenario: GET specific trading pair returns correct data
    When I request the trading pair "BTCUSD"
    Then the response is OK
    And the response has JSON content type
    And the response time is within 1000 ms
    And the response contains field "symbol" with value "BTCUSD"

  Scenario: GET ticker price returns current price
    When I request the ticker price for "BTCUSD"
    Then the response is OK
    And the response has JSON content type
    And the response time is within 500 ms
    And the response contains field "symbol" with value "BTCUSD"
    And the response contains field "price"
    And the price field is a positive number

  Scenario: GET 24h statistics returns complete data
    When I request the 24h statistics for "BTCUSD"
    Then the response is OK
    And the response has JSON content type
    And the response time is within 1000 ms
    And the response contains field "symbol" with value "BTCUSD"
    And the response contains required fields:
      | symbol |
      | high   |
      | low    |
      | open   |
      | close  |
      | volume |

  Scenario: GET market depth returns bids and asks
    When I request the market depth for "BTCUSD" with limit 10
    Then the response is OK
    And the response has JSON content type
    And the response time is within 1000 ms
    And the response contains bids and asks arrays

  Scenario: GET non-existent trading pair returns 404
    When I request the trading pair "INVALID123"
    Then the response is not found
    And the response has JSON content type
    And the response time is within 1000 ms

  Scenario: POST market order creation
    When I create a market order for "BTCUSD" side "BUY" quantity 0.001
    Then the response status code is 201 or 401

  Scenario: POST limit order creation
    When I create a limit order for "BTCUSD" side "BUY" quantity 0.001 price 40000.0
    Then the response status code is 201 or 401

  Scenario: POST invalid order returns error
    When I create a market order for "BTCUSD" side "BUY" quantity -1.0
    Then the response status code is 400 or 401

  Scenario: GET account balance requires authentication
    When I request account balance with token "invalid-token"
    Then the response is unauthorized
    And the response has JSON content type
    And the response time is within 1000 ms

  Scenario: GET trade history requires authentication
    When I request trade history with token "invalid-token"
    Then the response is unauthorized
    And the response time is within 1000 ms

  Scenario: DELETE order requires authentication
    When I cancel order "test-order-123" with token "invalid-token"
    Then the response is unauthorized
    And the response time is within 1000 ms

  Scenario: API responses include correct headers
    When I request all trading pairs
    Then the response is OK
    And the response has Content-Type header
    And the response time is within 2000 ms
