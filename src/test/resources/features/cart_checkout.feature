Feature: Cart checkout
  As a Rohlik customer
  I want to check out my cart for a concrete date
  So that I can receive the goodies

  Background:
    Given the following products exist:
      | sku | name     | price |
      | 001 | potatoes | 2.5   |
      | 002 | water    | 0.95  |
    And I have a cart
    And I add 2 units of product "001" to my cart
    And I add 1 unit of product "002" to my cart

  Scenario: Cart checkout
    Given the warehouse expects to receive an order request to be delivered at "2022-06-20T14:00:00+02:00"
    When I proceed to checkout my cart to be delivered at "2022-06-20T14:00:00+02:00"
    Then there should be a status "CHECK_OUT" in my cart
    And there should be a delivery date "2022-06-20T14:00:00+02:00" in my cart
