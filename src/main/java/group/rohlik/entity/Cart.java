package group.rohlik.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "carts")
@Data
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CartStatus status = CartStatus.NEW;
    @Column
    private ZonedDateTime deliveryAt;
    @OneToMany(mappedBy = "cart", orphanRemoval = true, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @OrderBy
    private Set<CartLine> lines = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "cart_discounts",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    @EqualsAndHashCode.Exclude
    @OrderBy
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

    private double totalDiscountsPercentage() {
        return discounts
                .stream()
                .mapToDouble(Discount::getPercentage)
                .sum();
    }

    public void recalculateAutomaticDiscounts(List<Discount> discounts) {
        List<Discount> toBeDeleted = this.discounts.stream().filter(Discount::isAutomatic).toList();
        toBeDeleted.forEach(this.discounts::remove);
        double price = totalLinesPrice();
        discounts
                .stream()
                .filter(Discount::isAutomatic)
                .filter(discount -> discount.getMinPrice() < price)
                .forEach(this.discounts::add);
    }

    public double totalPrice() {
        return Math.max(
                0,
                BigDecimal
                        .valueOf(totalLinesPrice() - (totalLinesPrice() * totalDiscountsPercentage() / 100))
                        .setScale(2, RoundingMode.CEILING)
                        .doubleValue()
        );
    }

    public boolean hasProduct(String sku) {
        return lines
                .stream()
                .anyMatch(line -> line.getProduct().getSku().equals(sku));
    }

    public boolean hasDiscount(long id) {
        return discounts
                .stream()
                .anyMatch(line -> line.getId() == id);
    }

    public boolean hasDiscount(String name) {
        return discounts
                .stream()
                .anyMatch(line -> line.getName().equals(name));
    }

    public Integer quantityOfProduct(String sku) {
        return lines
                .stream()
                .filter(line -> line.getProduct().getSku().equals(sku))
                .findFirst()
                .map(CartLine::getQuantity)
                .orElse(0);
    }

    public void applyDiscount(Discount discount) {
        Assert.isTrue(!discount.isAutomatic(), "Discount can not be of type automatic");
        Assert.isTrue(!discounts.contains(discount), "Discount already applied");

        discounts.add(discount);
    }

    public void checkout(ZonedDateTime deliveryAt) {
        if (!status.equals(CartStatus.NEW)) {
            throw new RuntimeException(String.format("Cart %d can´t execute checkout", id));
        }
        this.status = CartStatus.CHECK_OUT;
        this.deliveryAt = deliveryAt;
    }

    public enum CartStatus {
        NEW, CHECK_OUT
    }
}
