Feature: Get cart detail

  Background:
    Given the following products exist:
      | sku | name     | price |
      | 001 | potatoes | 2.5   |
      | 002 | water    | 0.95  |
    And there is a cart discount "free shipping" for 10 % with code "FREE-SHIPPING"
    And I have a cart
    And I add 2 units of product "001" to my cart
    And I add 1 units of product "002" to my cart
    And I apply "FREE-SHIPPING" discount to my cart

  Scenario: Get cart details
    When I send a "GET" request to "/carts/1"
    Then the response status should be 200 with body:
    """
    {
      "id": 1,
      "total": 5.36,
      "lines": [
        {
          "sku": "001",
          "name": "potatoes",
          "quantity": 2
        },
        {
          "sku": "002",
          "name": "water",
          "quantity": 1
        }
      ],
      "discounts": [
        "free shipping"
      ]
    }
    """
