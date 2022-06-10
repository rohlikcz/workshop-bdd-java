Feature: Add product to cart
  As a Rohlik customer
  I want to add products to my cart
  So that I can buy them

  Background:
    Given the following products exist:
      | sku | name     | price |
      | 001 | potatoes | 2.5   |
      | 002 | water    | 0.95  |
    And I have a cart

  Scenario: Add product
    When I add 2 units of product "001" to my cart
    And I add 1 unit of product "002" to my cart
    Then there should be 2 units of product "001" in my cart
    And there should be 1 unit of product "002" in my cart
    And the cart's total cost should be 5.95 euros

  Scenario: Remove product
    Given I add 2 units of product "001" to my cart
    And I add 1 unit of product "002" to my cart
    When I remove product "001" of my cart
    Then there should be 1 unit of product "002" in my cart
    And the cart's total cost should be 0.95 euros
    But there shouldn't be product "001" in my cart