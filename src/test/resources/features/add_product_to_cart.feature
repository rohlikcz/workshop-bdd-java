Feature: Add product to cart
  As a Rohlik customer
  I want to add products to my cart
  So that I can buy them

  Background:
    Given the following products exist:
      | sku | name     | price |
      | 001 | potatoes | 2.5   |
      | 002 | water    | 0.95  |
    And there is a cart discount "Amazing discount" for 10 % with a minimum total price of 6 euros
    And I have a cart

  Scenario: Add product
    When I add 2 units of product "001" to my cart
    And I add 1 unit of product "002" to my cart
    Then there should be 2 units of product "001" in my cart
    And there should be 1 unit of product "002" in my cart
    And the cart's total cost should be 5.95 euros
    But there shouldn't be discounts in my cart

  Scenario: Remove product
    Given I add 2 units of product "001" to my cart
    And I add 1 unit of product "002" to my cart
    When I remove product "001" of my cart
    Then there should be 1 unit of product "002" in my cart
    And the cart's total cost should be 0.95 euros
    But there shouldn't be product "001" in my cart
    * there shouldn't be discounts in my cart

  Scenario: Add product and apply discounts
    Given I add 2 units of product "001" to my cart
    And I add 2 units of product "002" to my cart
    Then there should be 2 units of product "001" in my cart
    And there should be 2 units of product "002" in my cart
    And there should be discount 1 in my cart
    And the cart's total cost should be 6.21 euros

  Scenario: Recalculate discount when remove products
    Given I add 2 units of product "001" to my cart
    And I add 2 units of product "002" to my cart
    When I remove product "002" of my cart
    And there should be 2 units of product "001" in my cart
    And the cart's total cost should be 5.00 euros
    But there shouldn't be product "002" in my cart
    * there shouldn't be discounts in my cart
