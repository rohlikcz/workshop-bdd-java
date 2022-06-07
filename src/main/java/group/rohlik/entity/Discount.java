package group.rohlik.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "discounts")
@Getter
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private float minPrice;
    @Column(nullable = false)
    private float percentage;
    @Column
    private String code;
    @Column
    @Enumerated(EnumType.STRING)
    private DiscountType type;

    public enum DiscountType {
        CODE, MIN_PRICE
    }

    public static Discount minimumPriceDiscount(String name, float percentage, float minPrice) {
        Discount discount = new Discount();
        discount.name = name;
        discount.percentage = percentage;
        discount.minPrice = minPrice;
        discount.code = null;
        discount.type = DiscountType.MIN_PRICE;

        return discount;
    }

    public static Discount codeDiscount(String name, float percentage, String code) {
        Discount discount = new Discount();
        discount.name = name;
        discount.percentage = percentage;
        discount.minPrice = 0;
        discount.code = code;
        discount.type = DiscountType.CODE;

        return discount;
    }

    public boolean isAutomatic() {
        return !type.equals(DiscountType.CODE);
    }
}