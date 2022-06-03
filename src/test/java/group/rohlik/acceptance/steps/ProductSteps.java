package group.rohlik.acceptance.steps;

import group.rohlik.entity.Product;
import group.rohlik.entity.ProductRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@AllArgsConstructor
public class ProductSteps {
    private final ProductRepository productRepository;

    @Given("the following products exist:")
    @Transactional
    public void theFollowingProductsExists(DataTable table) {
        table
                .asMaps(String.class, String.class)
                .forEach((Map<String, String> row) -> {
                    Product product = new Product();
                    product.setSku(row.get("sku"));
                    product.setName(row.get("name"));
                    product.setPrice(Float.parseFloat(row.get("price")));
                    productRepository.save(product);
                });
    }
}
