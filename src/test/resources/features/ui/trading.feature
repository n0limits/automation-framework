@ui @trading
Feature: Trading Page Functionality
  As a user of the MultiBank trading platform
  I want to view trading pairs, market indicators, and investment sections
  So that I can make informed trading decisions

  Background:
    Given I am on the homepage
    And the trading page is loaded

  @smoke
  Scenario: Spot trading section is displayed
    Then the spot trading section is displayed

  @smoke
  Scenario: Trading pairs table is displayed
    Then the trading pairs table is displayed

  Scenario: Trading pairs table contains expected columns
    When I get the table columns
    Then the table contains the following columns:
      | Pair         |
      | Max Leverage |
      | Short        |
      | Long         |
      | Charts       |
      | Change 24h   |

  Scenario: Trading pairs are displayed in the table
    Then the trading pairs list is not empty

  Scenario: Trading pairs count is greater than zero
    Then the trading pairs count is greater than 0

  Scenario Outline: Trading pair <pair> is visible
    Then the trading pair "<pair>" is visible

    Examples:
      | pair    |
      | BTC/USD |
      | ETH/USD |

  Scenario: Trading pair data structure is correct
    Then the trading pair data for "BTC/USD" is not empty

  Scenario Outline: Trading tab <tab> is visible
    Then the "<tab>" tab is visible

    Examples:
      | tab        |
      | Favorites  |
      | All Pairs  |

  Scenario Outline: Clicking <tab> tab keeps table displayed
    When I click the "<tab>" tab
    Then the trading pairs table is still displayed after tab switch

    Examples:
      | tab        |
      | Favorites  |
      | All Pairs  |

  Scenario Outline: Market indicator <indicator> is visible
    Then the "<indicator>" market indicator is visible

    Examples:
      | indicator   |
      | Fear Index  |
      | Top Gainers |
      | Top Losers  |

  Scenario Outline: Investment section <section> is visible
    Then the "<section>" investment section is visible

    Examples:
      | section            |
      | MBG Token          |
      | Real World Assets  |

  Scenario: Quick access tools are visible
    Then at least one quick access tool is visible
