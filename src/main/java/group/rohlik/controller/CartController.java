package group.rohlik.controller;

import com.fasterxml.jackson.databind.JsonNode;
import group.rohlik.application.CartDiscountAdder;
import group.rohlik.entity.Cart;
import group.rohlik.entity.CartLine;
import group.rohlik.entity.CartRepository;
import group.rohlik.entity.Product;
import group.rohlik.entity.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartDiscountAdder cartDiscountAdder;

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

        cartRepository.save(cart);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/carts/{id}/discounts", consumes = "application/json", produces = "application/json")
    public ResponseEntity addDiscount(@PathVariable long id, @RequestBody JsonNode payload) {
        String code = payload.get("code").textValue();

        cartDiscountAdder.add(id, code);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}