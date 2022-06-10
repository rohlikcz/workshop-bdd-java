package group.rohlik.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToMany(mappedBy = "cart", orphanRemoval = true, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<CartLine> lines = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "cart_discounts",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<Discount> discounts = new HashSet<>();

    public void addLine(Product product, int quantity) {
        Assert.isTrue(quantity >= 0, "Line quantity must be positive");

        CartLine cartLine = lines
                .stream()
                .filter(currentCartLine -> currentCartLine.getProduct().equals(product))
                .findFirst()
                .orElse(null);

        if (cartLine == null) {
            if (quantity > 0) {
                cartLine = new CartLine();
                cartLine.setProduct(product);
                cartLine.setQuantity(quantity);
                cartLine.setCart(this);
                lines.add(cartLine);
            }
        } else {
            if (quantity == 0) {
                lines.remove(cartLine);
            } else {
                cartLine.setQuantity(quantity);
            }
        }
    }

    private double totalLinesPrice() {
        return lines
                .stream()
                .mapToDouble(currentCartLine -> currentCartLine.getQuantity() * currentCartLine.getProduct().getPrice())
                .sum();
    }

    private double totalDiscountsPrice() {
        return discounts
                .stream()
                .mapToDouble(Discount::getValue)
                .sum();
    }

    public double totalPrice() {
        return BigDecimal
                .valueOf(totalLinesPrice() - totalDiscountsPrice())
                .setScale(2, RoundingMode.CEILING)
                .doubleValue();
    }

    public Integer quantityOfProduct(String sku) {
        return lines
                .stream()
                .filter(line -> line.getProduct().getSku().equals(sku))
                .findFirst()
                .map(CartLine::getQuantity)
                .orElse(0);
    }
}
