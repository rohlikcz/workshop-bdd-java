package group.rohlik.application;

import group.rohlik.entity.Cart;
import group.rohlik.entity.CartRepository;
import group.rohlik.entity.Discount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class CartFetcher {

    private final CartRepository cartRepository;

    public CartDetail fetch(long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();

        return new CartDetail(
                cart.getId(),
                cart.totalPrice(),
                cart
                        .getLines()
                        .stream()
                        .map(line -> new Line(line.getProduct().getSku(), line.getProduct().getName(), line.getQuantity()))
                        .sorted(Comparator.comparing(line -> line.sku))
                        .toList(),
                cart
                        .getDiscounts()
                        .stream()
                        .map(Discount::getName)
                        .sorted()
                        .toList()
        );
    }

    private record CartDetail(
            long id,
            double total,
            List<Line> lines,
            List<String> discounts
    ) { }

    private record Line(
            String sku,
            String name,
            int quantity
    ) { }
}
