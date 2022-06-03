package group.rohlik.acceptance.steps;

import group.rohlik.entity.Cart;
import group.rohlik.entity.CartRepository;
import io.cucumber.gherkin.internal.com.eclipsesource.json.JsonObject;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class CartSteps {

    private final CartRepository cartRepository;
    private final TestRestTemplate template;
    private Long currentCartId;

    @Autowired
    public CartSteps(CartRepository cartRepository, TestRestTemplate template) {
        this.cartRepository = cartRepository;
        this.template = template;
    }

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

    @Then("the cart's total cost should be {float} euro(s)")
    public void cartTotalCost(float amount) {
        Cart cart = currentCart();
        double totalPrice = cart.totalPrice();

        Assertions.assertEquals(amount, (float) totalPrice);
    }

    @Then("there should be {int} unit(s) of product {string} in my cart")
    public void thereShouldBeProductUnitsInMyCart(int quantity, String sku) {
        Cart cart = currentCart();

        Assertions.assertEquals(quantity, cart.quantityOfProduct(sku));
    }

    @Then("there shouldn't be product {string} in my cart")
    public void thereShouldNotBeProductInCart(String sku) {
        Cart cart = currentCart();

        Assertions.assertFalse(cart.hasProduct(sku), String.format("Product %s should not be present", sku));
    }

    @Then("there should be discount {long} in my cart")
    public void thereShouldBeDiscountInCart(long discountId) {
        Cart cart = currentCart();

        Assertions.assertTrue(cart.hasDiscount(discountId), String.format("Discount %s should be present", discountId));
    }

    @Then("there should be discount {string} in my cart")
    public void thereShouldBeDiscountByNameInCart(String name) {
        Cart cart = currentCart();

        Assertions.assertTrue(cart.hasDiscount(name), String.format("Discount %s should be present", name));
    }

    @Then("there shouldn't be discounts in my cart")
    public void thereShouldNotBeDiscountsInCart() {
        Cart cart = currentCart();

        Assertions.assertEquals(0, cart.getDiscounts().size());
    }

    @When("I apply {string} discount to my cart")
    public void iApplyDiscountToMyCart(String code) {
        JsonObject body = new JsonObject();
        body.add("code", code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        template.exchange(
                String.format("/carts/%d/discounts", currentCartId),
                HttpMethod.POST,
                new HttpEntity<>(body.toString(), headers),
                String.class
        );
    }

    private Cart currentCart() {
        return cartRepository.findById(currentCartId).orElseThrow();
    }
}
