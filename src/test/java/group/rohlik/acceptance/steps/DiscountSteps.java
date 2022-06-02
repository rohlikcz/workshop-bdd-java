package group.rohlik.acceptance.steps;

import io.cucumber.java.en.Given;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiscountSteps {
    @Given("there is a cart discount {string} for {float} euros with a minimum total price of {float} euros")
    public void thereIsACartDiscountWithAMinimumTotalPrice(String name, float amount, float minPrice) {
        SetupSteps.notImplemented();
    }
}
