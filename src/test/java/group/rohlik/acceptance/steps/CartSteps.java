package group.rohlik.acceptance.steps;

import group.rohlik.entity.Cart;
import group.rohlik.entity.CartLine;
import group.rohlik.entity.CartRepository;
import group.rohlik.entity.Discount;
import io.cucumber.gherkin.internal.com.eclipsesource.json.JsonObject;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.math.RoundingMode;


@RequiredArgsConstructor
public class CartSteps {

    private final CartRepository cartRepository;
    private final TestRestTemplate template;
    private Long currentCartId;

    @Before
    public void setUp() {
        currentCartId = null;
    }

    @Given("I have a cart")
    public void haveCart() {
        Cart cart = new Cart();
        cartRepository.save(cart);
        currentCartId = cart.getId();
    }

    @When("I add {int} unit(s) of product {string} to my cart")
    public void addProductUnitsToMyCart(int quantity, String sku) {
        JsonObject body = new JsonObject();
        body.add("sku", sku);
        body.add("quantity", quantity);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        template.exchange(
                String.format("/carts/%d/lines", currentCartId),
                HttpMethod.POST,
                new HttpEntity<>(body.toString(), headers),
                String.class
        );
    }

    @When("I remove product {string} of my cart")
    public void removeProductOfMyCart(String sku) {
        addProductUnitsToMyCart(0, sku);
    }

    @Then("the cart's total cost should be {double} euro(s)")
    public void cartTotalCost(double amount) {
        Cart cart = currentCart();
        double totalProducts = cart
                .getLines()
                .stream()
                .mapToDouble(currentCartLine -> currentCartLine.getQuantity() * currentCartLine.getProduct().getPrice())
                .sum();
        double totalDiscounts = cart.getDiscounts().stream().mapToDouble(Discount::getValue).sum();
        double totalPrice = totalProducts - totalDiscounts;

        Assertions.assertEquals(amount, BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.CEILING).doubleValue());

    }

    @Then("there should be {int} unit(s) of product {string} in my cart")
    public void thereShouldBeProductUnitsInMyCart(int quantity, String sku) {
        Cart cart = currentCart();
        CartLine cartLine = cart.getLines()
                .stream()
                .filter(currentCartLine -> currentCartLine.getProduct().getSku().equals(sku))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(cartLine, String.format("Product %s not found", sku));
        Assertions.assertEquals(quantity, cartLine.getQuantity());
    }

    @Then("there shouldn't be product {string} in my cart")
    public void thereShouldNotBeProductInCart(String sku) {
        Cart cart = currentCart();
        CartLine cartLine = cart.getLines()
                .stream()
                .filter(currentCartLine -> currentCartLine.getProduct().getSku().equals(sku))
                .findFirst()
                .orElse(null);

        Assertions.assertNull(cartLine, String.format("Product %s should not be present", sku));
    }

    private Cart currentCart() {
        return cartRepository.findById(currentCartId).orElseThrow();
    }
}
