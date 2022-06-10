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
    And there is a cart discount "special offer" for 10 euros with code "SPECIAL-OFFER"
    And I have a cart

  Scenario: No discounts
    When I add 2 units of product "001" to my cart
    Then the cart's total cost should be 5.0 euros
    But there shouldn't be discounts in my cart

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

  Scenario: Cart total should be always positive
    Given I add 2 units of product "001" to my cart
    And I apply "FREE-SHIPPING" discount to my cart
    When I apply "SPECIAL-OFFER" discount to my cart
    Then the cart's total cost should be 0.0 euros
    And there should be discount "free shipping" in my cart
    And there should be discount "special offer" in my cart
