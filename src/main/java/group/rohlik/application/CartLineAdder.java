package group.rohlik.application;

import group.rohlik.entity.Cart;
import group.rohlik.entity.CartRepository;
import group.rohlik.entity.DiscountRepository;
import group.rohlik.entity.Product;
import group.rohlik.entity.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class CartLineAdder {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public void add(long cartId, String sku, int quantity) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        Product product = productRepository.findById(sku).orElseThrow();
        cart.addLine(product, quantity);
        cart.recalculateDiscounts(discountRepository.findAll());

        cartRepository.save(cart);
    }
}
