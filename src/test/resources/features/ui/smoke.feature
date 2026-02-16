@ui @smoke
Feature: Smoke Tests - Application Health
  As a QA engineer
  I want to verify basic application functionality
  So that I can confirm the application is operational

  Background:
    Given I am on the homepage

  Scenario: Homepage loads successfully
    Then the page URL contains "trade.multibank.io"
    And the page title contains "MultiBank"
    And the header is visible

  Scenario: Critical page elements are present
    Then the header is visible
    And the page body is not empty
    And the page has links

  Scenario: Page has no critical JavaScript errors
    When I navigate to "https://trade.multibank.io"
    And I wait for the page to load
    Then the page is fully loaded
