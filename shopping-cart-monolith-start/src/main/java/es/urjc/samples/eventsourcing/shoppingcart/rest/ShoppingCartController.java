package es.urjc.samples.eventsourcing.shoppingcart.rest;

import es.urjc.samples.eventsourcing.shoppingcart.persistence.CartItem;
import es.urjc.samples.eventsourcing.shoppingcart.persistence.ShoppingCart;
import es.urjc.samples.eventsourcing.shoppingcart.persistence.ShoppingCartRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carts")
public class ShoppingCartController {

    private final ShoppingCartRepository repository;

    public ShoppingCartController(ShoppingCartRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ShoppingCart> listAllCarts(){
        return repository.findAll();
    }

    @GetMapping("/{cartId}")
    public ShoppingCart getCart(@PathVariable String cartId) {
        return repository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping Cart " + cartId));
    }

    @PostMapping("/{cartId}/product/{productId}")
    public String addItem(@PathVariable String cartId, @PathVariable String productId, @RequestParam int quantity) {
        ShoppingCart shoppingCart = getShoppingCart(cartId);
        Optional<CartItem> cartItem = getCartItem(shoppingCart, productId);

        cartItem.ifPresentOrElse(
                    item -> item.setQuantity(item.getQuantity() + quantity),
                    () -> shoppingCart.addItem(new CartItem(productId, quantity))
                );

        repository.save(shoppingCart);

        return shoppingCart.getCartId();
    }


    @DeleteMapping("/{cartId}/product/{productId}")
    public String removeItem(@PathVariable String cartId, @PathVariable String productId, @RequestParam int quantity) {
        ShoppingCart shoppingCart = getShoppingCart(cartId);
        CartItem cartItem = getCartItem(shoppingCart, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + productId + " in cart " + cartId));

        if (quantity == 0 || quantity >= cartItem.getQuantity()) { //remove the product from cart
            shoppingCart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() - quantity);
        }

        return shoppingCart.getCartId();
    }

    private ShoppingCart getShoppingCart(String cartId) {
        ShoppingCart shoppingCart = repository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart " + cartId));
        return shoppingCart;
    }

    private Optional<CartItem> getCartItem(ShoppingCart shoppingCart, String productId) {
        Optional<CartItem> cartItem = shoppingCart.getItems()
                .stream()
                .filter(item -> item.getProductId().equalsIgnoreCase(productId))
                .findFirst();
        return cartItem;
    }


}
