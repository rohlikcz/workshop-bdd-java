package group.rohlik.controller;

import com.fasterxml.jackson.databind.JsonNode;
import group.rohlik.entity.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@RestController
@AllArgsConstructor
public class CartController {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    @PostMapping(path = "/carts/{id}/lines", consumes = "application/json", produces = "application/json")
    public ResponseEntity addLine(@PathVariable long id, @RequestBody JsonNode payload) {
        String sku = payload.get("sku").textValue();
        int quantity = payload.get("quantity").intValue();

        if (quantity < 0) {
            throw new IllegalArgumentException("Negative quantity not allowed");
        }

        Cart cart = cartRepository.findById(id).orElseThrow();
        Product product = productRepository.findById(sku).orElseThrow();
        CartLine cartLine = cart.getLines()
                .stream()
                .filter(currentCartLine -> currentCartLine.getProduct().equals(product))
                .findFirst()
                .orElse(null);

        if (cartLine == null) {
            if (quantity > 0) {
                cartLine = new CartLine();
                cartLine.setProduct(product);
                cartLine.setQuantity(quantity);
                cartLine.setCart(cart);
                cart.getLines().add(cartLine);
            }
        } else {
            if (quantity == 0) {
                cart.getLines().remove(cartLine);
            } else {
                cartLine.setQuantity(quantity);
            }
        }

        double totalPrice = cart
                .getLines()
                .stream()
                .mapToDouble(currentCartLine -> currentCartLine.getQuantity() * currentCartLine.getProduct().getPrice())
                .sum();
        List<Discount> discounts = discountRepository.findAll();
        cart.getDiscounts().clear();
        discounts.forEach(discount -> {
            if (totalPrice >= discount.getMinPrice()) {
                cart.getDiscounts().add(discount);
            }
        });
        cartRepository.save(cart);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}