Feature: Add discount to cart
  As a Rohlik customer
  I want to apply discounts to my cart
  so that I can save some money

  Background:
    Given the following products exist:
      | sku | name     | price |
      | 001 | potatoes | 2.5   |
      | 002 | water    | 0.95  |
    And there is a cart discount "free shipping" for 10 % with code "FREE-SHIPPING"
    And I have a cart

  Scenario: Add discount
    Given I add 2 units of product "001" to my cart
    When I apply "FREE-SHIPPING" discount to my cart
    Then the cart's total cost should be 4.50 euros
    And there should be discount "free shipping" in my cart

  Scenario: Add discount only apply once
    Given I add 2 units of product "001" to my cart
    And I apply "FREE-SHIPPING" discount to my cart
    When I apply "FREE-SHIPPING" discount to my cart
    Then the cart's total cost should be 4.5 euros
    And there should be discount "free shipping" in my cart

  Scenario: Mix automatic and code discounts
    Given there is a cart discount "Amazing discount" for 10 % with a minimum total price of 6 euros
    And I add 3 units of product "001" to my cart
    When I apply "FREE-SHIPPING" discount to my cart
    Then the cart's total cost should be 6.0 euros
    And there should be discount "free shipping" in my cart
    And there should be discount "Amazing discount" in my cart

  Scenario: Code discounts are no recalculated
    Given there is a cart discount "Amazing discount" for 10 % with a minimum total price of 6 euros
    And I add 2 units of product "001" to my cart
    And I add 2 units of product "002" to my cart
    And I apply "FREE-SHIPPING" discount to my cart
    When I remove product "002" of my cart
    Then the cart's total cost should be 4.5 euros
    And there should be discount "free shipping" in my cart

  Scenario Outline: Add discount examples
    Given the following products exist:
      | sku | name         | price           |
      | 003 | orange juice | <product_price> |
    And there is a cart discount "super discount" for <percentage> % with code "SUPER-DISCOUNT"
    Given I add <product_quantity> units of product "003" to my cart
    When I apply "SUPER-DISCOUNT" discount to my cart
    Then the cart's total cost should be <cart_total> euros
    And there should be discount "super discount" in my cart
    Examples:
      | product_price | product_quantity | percentage | cart_total |
      | 21.0          | 1                | 10         | 18.90      |
      | 20.0          | 2                | 15         | 34.0       |
      | 5.0           | 3                | 3          | 14.55      |

  Scenario: Cart value is always positive
    Given there is a cart discount "super discount" for 50 % with code "SUPER-DISCOUNT"
    And there is a cart discount "another super discount" for 55 % with code "ANOTHER-SUPER-DISCOUNT"
    And I add 5 units of product "001" to my cart
    When I apply "SUPER-DISCOUNT" discount to my cart
    And I apply "ANOTHER-SUPER-DISCOUNT" discount to my cart
    Then the cart's total cost should be 0 euros
    And there should be discount "super discount" in my cart
    And there should be discount "another super discount" in my cart