package group.rohlik.application;

import group.rohlik.entity.Cart;
import group.rohlik.entity.CartRepository;
import group.rohlik.entity.Discount;
import group.rohlik.entity.DiscountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class CartDiscountAdder {

    private final CartRepository cartRepository;
    private final DiscountRepository discountRepository;

    public void add(long cartId, String code) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        Discount discount = discountRepository.findByCode(code).orElseThrow();
        cart.applyDiscount(discount);

        cartRepository.save(cart);
    }
}
