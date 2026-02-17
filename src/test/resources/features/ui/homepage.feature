@ui
Feature: MultiBank Homepage Functional Tests

  Background:
    Given I am on the homepage
    And I wait for the page to load

  # --- Smoke ---
  @smoke
  Scenario: Homepage loads with interactive content
    Then the page URL contains "trade.multibank.io"
    And the page title contains "MultiBank"
    And the navigation menu is displayed
    And the trading pairs table is displayed

  @smoke
  Scenario: Trading table responds to tab clicks
    When I click the "All Pairs" tab
    Then the trading pairs count is greater than 0
    When I click the "Favorites" tab
    Then the trading pairs table is still displayed after tab switch

  # --- Navigation ---
  @navigation
  Scenario: Click Markets and verify navigation
    When I click the "Markets" navigation item
    And I wait for the page to load
    Then the navigation menu is displayed

  @navigation
  Scenario: Browser back returns to homepage
    When I store the current URL
    And I click the "Markets" navigation item
    And I wait for the page to load
    And I go back in the browser
    Then the current URL matches the stored URL

  # --- Trading Table ---
  @trading
  Scenario: Read trading pair data from All Pairs tab
    When I click the "All Pairs" tab
    And I get the trading pairs list
    Then the trading pairs list is not empty
    When I get the trading pair data for the first pair
    Then the trading pair data is not empty

  @trading
  Scenario: Tab switching preserves data consistency
    When I click the "All Pairs" tab
    And I store the trading pairs count
    And I click the "Favorites" tab
    And I click the "All Pairs" tab
    Then the trading pairs count matches the stored count

  @trading
  Scenario: Table columns are present
    When I get the table columns
    Then the table contains the following columns:
      | Pair         |
      | Max Leverage |
      | Change 24h   |

  # --- Page Sections ---
  @sections
  Scenario: Scroll to footer and verify download links
    When I scroll to the bottom of the page
    Then the footer is displayed
    And the App Store link is visible
    And the Google Play link is visible
    And the App Store URL contains "apple.com"
    And the Google Play URL contains "play.google.com"

  @sections
  Scenario: Scroll through page and back to top
    When I scroll to the bottom of the page
    Then the footer is displayed
    When I scroll to the top of the page
    Then the navigation menu is displayed

  # --- Cross-Page ---
  @crosspage
  Scenario: Navigate to About Us and back
    When I store the current URL
    And I navigate to the About Us page
    And I wait for the page to load
    And I go back in the browser
    Then the current URL matches the stored URL
