package es.urjc.samples.eventsourcing.shoppingcart.rest;

import es.urjc.samples.eventsourcing.shoppingcart.persistence.Customer;
import es.urjc.samples.eventsourcing.shoppingcart.persistence.CustomerRepository;
import es.urjc.samples.eventsourcing.shoppingcart.persistence.ShoppingCart;
import es.urjc.samples.eventsourcing.shoppingcart.persistence.ShoppingCartRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerRestController {

    private final CustomerRepository repository;

    private final ShoppingCartRepository shoppingCartRepository;

    public CustomerRestController(CustomerRepository repository, ShoppingCartRepository shoppingCartRepository) {
        this.repository = repository;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @PostMapping
    public String createCustomer(@RequestBody Customer customer) {
        if (customer.getCustomerId() == null) {
            customer.setCustomerId(UUID.randomUUID().toString());
        }

        repository.save(customer);

        return customer.getCustomerId();
    }

    @GetMapping
    public List<Customer> listCustomers() {
        return repository.findAll();
    }

    @GetMapping("/{customerId}")
    public Customer getCustomer(@PathVariable String customerId) {
        return repository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer " + customerId));
    }

    @PostMapping("/{customerId}/cart")
    public String createCart(@PathVariable String customerId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCartId(UUID.randomUUID().toString());
        shoppingCart.setCustomerId(customerId);
        shoppingCartRepository.save(shoppingCart);

        return shoppingCart.getCartId();
    }


}
