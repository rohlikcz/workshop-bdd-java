package group.rohlik.acceptance.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CartSteps {
    @Given("I have a cart")
    public void haveCart() {
        SetupSteps.notImplemented();
    }

    @When("I add {int} unit(s) of product {string} to my cart")
    public void addProductUnitsToMyCart(int quantity, String sku) {
        SetupSteps.notImplemented();
    }

    @When("I remove product {string} of my cart")
    public void removeProductOfMyCart(String sku) {
        SetupSteps.notImplemented();
    }

    @Then("the cart's total cost should be {float} euro(s)")
    public void cartTotalCost(float amount) {
        SetupSteps.notImplemented();
    }

    @Then("there should be {int} unit(s) of product {string} in my cart")
    public void thereShouldBeProductUnitsInMyCart(int quantity, String sku) {
        SetupSteps.notImplemented();
    }

    @Then("there shouldn't be product {string} in my cart")
    public void thereShouldNotBeProductInCart(String sku) {
        SetupSteps.notImplemented();
    }

    @Then("there should be discount {long} in my cart")
    public void thereShouldBeDiscountInCart(long discountId) {
        SetupSteps.notImplemented();
    }

    @Then("there shouldn't be discounts in my cart")
    public void thereShouldNotBeDiscountsInCart() {
        SetupSteps.notImplemented();
    }
}
