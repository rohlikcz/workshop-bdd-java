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
    And there is a cart discount "special offer" for 95 % with code "SPECIAL-OFFER"
    And I have a cart

  Scenario: No discounts
    When I add 2 units of product "001" to my cart
    Then the cart's total cost should be 5.0 euros
    But there shouldn't be discounts in my cart

  Scenario: Add discount
    Given I add 2 units of product "001" to my cart
    When I apply "FREE-SHIPPING" discount to my cart
    Then the cart's total cost should be 4.50 euros
    And there should be discount "free shipping" in my cart

  Scenario: Add discount only apply once
    Given I add 2 units of product "001" to my cart
    And I apply "FREE-SHIPPING" discount to my cart
    When I apply "FREE-SHIPPING" discount to my cart
    Then the cart's total cost should be 4.50 euros
    And there should be discount "free shipping" in my cart

  Scenario: Cart total should be always positive
    Given I add 2 units of product "001" to my cart
    And I apply "FREE-SHIPPING" discount to my cart
    When I apply "SPECIAL-OFFER" discount to my cart
    Then the cart's total cost should be 0.0 euros
    And there should be discount "free shipping" in my cart
    And there should be discount "special offer" in my cart

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
      | 5.0           | 3                | 0          | 15.00      |