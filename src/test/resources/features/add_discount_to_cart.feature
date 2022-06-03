Feature: Add discount to cart
  As a Rohlik customer
  I want to apply discounts to my cart
  so that I can save some money

  Background:
    Given the following products exist:
      | sku | name     | price |
      | 001 | potatoes | 2.5   |
      | 002 | water    | 0.95  |
    And there is a cart discount "free shipping" for 1 euros with code "FREE-SHIPPING"
    And there is a cart discount "Amazing discount" for 1 euros with a minimum total price of 6 euros
    And I have a cart

  Scenario: Add discount
    Given I add 2 units of product "001" to my cart
    When I apply "FREE-SHIPPING" discount to my cart
    Then the cart's total cost should be 4.0 euros
    And there should be discount "free shipping" in my cart

  Scenario: Add discount only apply once
    Given I add 2 units of product "001" to my cart
    And I apply "FREE-SHIPPING" discount to my cart
    When I apply "FREE-SHIPPING" discount to my cart
    Then the cart's total cost should be 4.0 euros
    And there should be discount "free shipping" in my cart

  Scenario: Mix automatic and code discounts
    Given I add 3 units of product "001" to my cart
    When I apply "FREE-SHIPPING" discount to my cart
    Then the cart's total cost should be 5.5 euros
    And there should be discount "free shipping" in my cart
    And there should be discount "Amazing discount" in my cart

  Scenario: Code discounts are no recalculated
    Given I add 2 units of product "001" to my cart
    And I add 2 units of product "002" to my cart
    And I apply "FREE-SHIPPING" discount to my cart
    When I remove product "002" of my cart
    Then the cart's total cost should be 4.0 euros
    And there should be discount "free shipping" in my cart