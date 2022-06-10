package group.rohlik.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    private String sku;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private double price;
}
