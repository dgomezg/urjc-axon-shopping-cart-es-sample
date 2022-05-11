package es.urjc.samples.eventsourcing.shoppingcart.rest;

import es.urjc.samples.eventsourcing.shoppingcart.persistence.Product;
import es.urjc.samples.eventsourcing.shoppingcart.persistence.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductRestController {

    private final ProductRepository repository;

    public ProductRestController(ProductRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public String addProduct(@RequestBody Product product) {
        Assert.state(product.getPrice().doubleValue() > 0, "Price of a product can't be less or equal to 0");

        if (product.getProductId() == null) {
            product.setProductId(UUID.randomUUID().toString());
        }

        repository.save(product);

        return product.getProductId();
    }

    @GetMapping
    public List<Product> listProducts() {
        return repository.findAll();
    }

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable String productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + productId));
    }


}
