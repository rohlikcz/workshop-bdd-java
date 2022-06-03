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
        double totalProducts = cart
                .getLines()
                .stream()
                .mapToDouble(currentCartLine -> currentCartLine.getQuantity() * currentCartLine.getProduct().getPrice())
                .sum();
        double totalDiscounts = cart.getDiscounts().stream().mapToDouble(Discount::getAmount).sum();
        double totalPrice = totalProducts - totalDiscounts;

        Assertions.assertEquals(Math.round(totalPrice * 100f) / 100f, amount);
    }

    @Then("there should be {int} unit(s) of product {string} in my cart")
    public void thereShouldBeProductUnitsInMyCart(int quantity, String sku) {
        Cart cart = currentCart();
        CartLine cartLine = cart.getLines()
                .stream()
                .filter(currentCartLine -> currentCartLine.getProduct().getSku().equals(sku))
                .findFirst()
                .orElseThrow();
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

    @Then("there should be discount {long} in my cart")
    public void thereShouldBeDiscountInCart(long discountId) {
        Cart cart = currentCart();
        Discount discount = cart.getDiscounts()
                .stream()
                .filter(currentDiscount -> currentDiscount.getId() == discountId)
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(discount, String.format("Discount %s should be present", discountId));
    }

    @Then("there shouldn't be discounts in my cart")
    public void thereShouldNotBeDiscountsInCart() {
        Cart cart = currentCart();
        Assertions.assertEquals(0, cart.getDiscounts().size());
    }

    private Cart currentCart() {
        return cartRepository.findById(currentCartId).orElseThrow();
    }
}
