package group.rohlik.acceptance.steps;

import group.rohlik.entity.ProductRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class ProductSteps {

    private final ProductRepository productRepository;

    @Given("the following products exist:")
    @Transactional
    public void theFollowingProductsExists(DataTable table) {
        SetupSteps.notImplemented();
    }
}
