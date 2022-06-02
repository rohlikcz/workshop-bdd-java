package group.rohlik.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "products")
@Data
@NoArgsConstructor
public class Product {
    @Id
    private String sku;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private float price;
}
