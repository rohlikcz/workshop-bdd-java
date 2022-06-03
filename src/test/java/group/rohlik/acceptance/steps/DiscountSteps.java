package group.rohlik.acceptance.steps;

import group.rohlik.entity.Discount;
import group.rohlik.entity.DiscountRepository;
import io.cucumber.java.en.Given;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class DiscountSteps {

    private final DiscountRepository discountRepository;

    @Given("there is a cart discount {string} for {float} euros with a minimum total price of {float} euros")
    @Transactional
    public void thereIsACartDiscountWithAMinimumTotalPrice(String name, float amount, float minPrice) {
        Discount discount = Discount.minimumPriceDiscount(name, amount, minPrice);
        discountRepository.save(discount);
    }

    @Given("there is a cart discount {string} for {float} euros with code {string}")
    @Transactional
    public void thereIsACartDiscountWithCode(String name, float amount, String code) {
        Discount discount = Discount.codeDiscount(name, amount, code);
        discountRepository.save(discount);
    }
}
