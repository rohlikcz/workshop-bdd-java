package group.rohlik.acceptance.steps;

import group.rohlik.entity.Discount;
import group.rohlik.entity.DiscountRepository;
import io.cucumber.java.en.Given;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class DiscountSteps {
    private final DiscountRepository discountRepository;

    @Given("there is a cart discount {string} for {double} euros with code {string}")
    @Transactional
    public void thereIsACartDiscountWithCode(String name, double value, String code) {
        Discount discount = Discount.create(name, code, value);
        discountRepository.save(discount);
    }
}
