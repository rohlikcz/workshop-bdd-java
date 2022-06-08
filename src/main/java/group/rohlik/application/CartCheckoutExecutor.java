package group.rohlik.application;

import group.rohlik.entity.Cart;
import group.rohlik.entity.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class CartCheckoutExecutor {

    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;
    private final String warehouseApiUrl;

    @Autowired
    public CartCheckoutExecutor(
            CartRepository cartRepository,
            RestTemplate restTemplate,
            @Value("${group.rohlik.warehouse.url}") String warehouseApiUrl
    ) {
        this.cartRepository = cartRepository;
        this.restTemplate = restTemplate;
        this.warehouseApiUrl = warehouseApiUrl;
    }

    public void checkout(long cartId, ZonedDateTime deliveryAt) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        cart.checkout(deliveryAt);
        cartRepository.save(cart);
        sendToWarehouse(cart);
    }

    private void sendToWarehouse(Cart cart) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<WarehouseCartRequest> request = new RequestEntity<>(
                new WarehouseCartRequest(
                        cart.getId(),
                        cart.getDeliveryAt(),
                        cart
                                .getLines()
                                .stream()
                                .map(cartLine -> new WarehouseProductRequest(cartLine.getProduct().getSku(), cartLine.getQuantity()))
                                .toList()
                ),
                headers,
                HttpMethod.POST,
                URI.create(warehouseApiUrl)
        );
        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new RuntimeException("Warehouse petition failed");
        }
    }

    private record WarehouseCartRequest(Long cartId, ZonedDateTime deliveryAt, List<WarehouseProductRequest> products) {}

    private record WarehouseProductRequest(String sku, int quantity){}
}
