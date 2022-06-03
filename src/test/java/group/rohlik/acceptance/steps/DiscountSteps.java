package group.rohlik.acceptance.steps;

import group.rohlik.entity.Discount;
import group.rohlik.entity.DiscountRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class DiscountSteps {
    private final DiscountRepository discountRepository;

    @Given("there is a cart discount {string} for {float} euros with a minimum total price of {float} euros")
    @Transactional
    public void thereIsACartDiscountWithAMinimumTotalPrice(String name, float amount, float minPrice) {
        Discount discount = new Discount();
        discount.setName(name);
        discount.setAmount(amount);
        discount.setMinPrice(minPrice);
        discountRepository.save(discount);
    }
}
