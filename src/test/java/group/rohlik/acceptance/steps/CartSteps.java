package group.rohlik.acceptance.steps;

import group.rohlik.acceptance.config.MockedHttpRequests;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CartSteps {

    private final CartRepository cartRepository;
    private final TestRestTemplate template;
    private final MockedHttpRequests mockRequests;
    private Long currentCartId;

    @Autowired
    public CartSteps(
            CartRepository cartRepository,
            TestRestTemplate template,
            MockedHttpRequests mockRequests
    ) {
        this.cartRepository = cartRepository;
        this.template = template;
        this.mockRequests = mockRequests;
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

    @Then("there should be a status {string} in my cart")
    public void thereShouldBeStatusInCart(String status) {
        Cart cart = currentCart();
        Assertions.assertEquals(Cart.CartStatus.valueOf(status), cart.getStatus());
    }

    @Then("there should be a delivery date {string} in my cart")
    public void thereShouldBeDeliveryDateInCart(String deliveryDate) {
        Cart cart = currentCart();
        ZonedDateTime deliveryAt = ZonedDateTime.parse(deliveryDate);
        Assertions.assertEquals(deliveryAt.toInstant(), cart.getDeliveryAt().toInstant());
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

    @When("I proceed to checkout my cart to be delivered at {string}")
    public void iProceedToCheckoutCartToBeDelivered(String deliveredAt) {
        mockRequests.addResponse(HttpStatus.CREATED);

        JsonObject body = new JsonObject();
        body.add("delivery_at", deliveredAt);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        template.exchange(
                String.format("/carts/%d/checkout", currentCartId),
                HttpMethod.POST,
                new HttpEntity<>(body.toString(), headers),
                String.class
        );
    }

    @Then("the warehouse has received my order request")
    public void warehouseReceivedOrderRequest() throws IOException {
        Cart cart = currentCart();
        CartPayload payload = new CartPayload(
                cart.getId(),
                cart.getDeliveryAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                cart.getLines()
                        .stream()
                        .map(cartLine -> new ProductPayload(cartLine.getProduct().getSku(), cartLine.getQuantity()))
                        .toList()
        );
        mockRequests.assertMethod(0, HttpMethod.POST);
        mockRequests.assertUrl(0, "https://internal-warehouse-microservice.rohlik/orders");
        mockRequests.assertHeader(0, "Content-Type", MediaType.APPLICATION_JSON.toString());
        mockRequests.assertBody(0, payload);
    }

    private Cart currentCart() {
        return cartRepository.findById(currentCartId).orElseThrow();
    }

    private record CartPayload(Long cartId, String deliveryAt, List<ProductPayload> products) {}

    private record ProductPayload(String sku, int quantity) {}
}
