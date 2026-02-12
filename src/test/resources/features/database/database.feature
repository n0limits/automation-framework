@db @database
Feature: Database Operations
  As a QA engineer
  I want to verify database CRUD operations and data integrity
  So that I can ensure the data layer works correctly

  Background:
    Given the database connection is established

  # ===== Schema Validation =====

  Scenario: Users table exists
    Then the "users" table exists

  Scenario: Users table has required columns
    Then the "users" table has columns:
      | id       |
      | username |
      | email    |
      | status   |

  # ===== User CRUD =====

  Scenario: Create a new user
    When I create a user with username "bdd_test_user" and email "bdd@test.com"
    Then the user ID is generated
    And the user exists in the database

  Scenario: Retrieve a user by ID
    Given a user exists with username "retrieve_bdd" and email "retrieve_bdd@test.com"
    When I query the user by ID
    Then the queried user has username "retrieve_bdd"
    And the queried user has email "retrieve_bdd@test.com"

  Scenario: Update user status
    Given a user exists with username "update_bdd" and email "update_bdd@test.com"
    When I update the user status to "suspended"
    Then 1 row was affected
    And the user status in the database is "suspended"

  Scenario: Delete a user
    Given a user exists with username "delete_bdd" and email "delete_bdd@test.com"
    When I delete the user by ID
    Then 1 row was deleted
    And the user does not exist in the database

  # ===== Referential Integrity =====

  Scenario: User-order relationship
    Given a user exists with username "order_bdd_user" and email "orderbdd@test.com"
    When I create an order for the user with order number "BDD-ORD-001" and amount 150.50
    Then the user has 1 order
    And the order references the user

  Scenario: One-to-many relationship with multiple orders
    Given a user exists with username "multi_order_bdd" and email "multiorder_bdd@test.com"
    When I create 3 orders for the user with amounts:
      | order_number | amount | status    |
      | BDD-M-001    | 100.0  | completed |
      | BDD-M-002    | 200.0  | completed |
      | BDD-M-003    | 300.0  | completed |
    Then the user has 3 orders
    And the total order amount for the user is 600.0
    And the average order amount for the user is 200.0

  # ===== Data Consistency =====

  Scenario: User status update preserves order relationships
    Given a user exists with username "consistency_bdd" and email "consistency_bdd@test.com"
    When I create an order for the user with order number "CON-BDD-001" and amount 100.0
    And I update the user status to "suspended"
    Then the user status in the database is "suspended"
    And the user has 1 order

  # ===== Constraints =====

  Scenario: Unique username constraint
    Given a user exists with username "unique_bdd_user" and email "unique_bdd@test.com"
    Then a duplicate username "unique_bdd_user" is rejected

  # ===== Count by Status =====

  Scenario: Count users by status
    Given a user exists with username "active_bdd_1" email "a_bdd1@test.com" and status "active"
    And a user exists with username "active_bdd_2" email "a_bdd2@test.com" and status "active"
    And a user exists with username "pending_bdd_1" email "p_bdd1@test.com" and status "pending"
    Then there are at least 2 users with status "active"
    And there are at least 1 users with status "pending"
