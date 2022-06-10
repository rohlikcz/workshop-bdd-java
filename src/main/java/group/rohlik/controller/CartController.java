package group.rohlik.controller;

import com.fasterxml.jackson.databind.JsonNode;
import group.rohlik.application.CartDiscountAdder;
import group.rohlik.application.CartLineAdder;
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

    private final CartLineAdder cartLineAdder;
    private final CartDiscountAdder cartDiscountAdder;

    @PostMapping(path = "/carts/{id}/lines", consumes = "application/json", produces = "application/json")
    public ResponseEntity addLine(@PathVariable long id, @RequestBody JsonNode payload) {
        String sku = payload.get("sku").textValue();
        int quantity = payload.get("quantity").intValue();

        cartLineAdder.add(id, sku, quantity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/carts/{id}/discounts", consumes = "application/json", produces = "application/json")
    public ResponseEntity addDiscount(@PathVariable long id, @RequestBody JsonNode payload) {
        String code = payload.get("code").textValue();

        cartDiscountAdder.add(id, code);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}