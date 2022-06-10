package group.rohlik.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    private String sku;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private double price;

    public static Product create(String sku, String name, double price) {
        Product product = new Product();
        product.sku = sku;
        product.name = name;
        product.price = price;

        return product;
    }
}
