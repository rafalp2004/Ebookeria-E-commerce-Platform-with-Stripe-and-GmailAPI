package com.ebookeria.ecommerce.service.cart;

import com.ebookeria.ecommerce.dto.cart.*;
import com.ebookeria.ecommerce.entity.Cart;
import com.ebookeria.ecommerce.entity.CartItem;
import com.ebookeria.ecommerce.entity.Ebook;
import com.ebookeria.ecommerce.entity.User;
import com.ebookeria.ecommerce.exception.ResourceNotFoundException;
import com.ebookeria.ecommerce.repository.CartRepository;
import com.ebookeria.ecommerce.repository.EbookRepository;
import com.ebookeria.ecommerce.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private final UserService userService;
    private final CartRepository cartRepository;
    private final EbookRepository ebookRepository;

    public CartServiceImpl(UserService userService, CartRepository cartRepository, EbookRepository ebookRepository) {
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.ebookRepository = ebookRepository;
    }

    @Override
    public void createCart() {
        Cart cart = new Cart();
        cart.setUser(userService.getCurrentUser());
        cart.setCreatedAt(LocalDateTime.now());
        cart.setLastUpdated(LocalDateTime.now());

        cartRepository.save(cart);


    }

    @Override
    public void addItemToCart(AddItemToCartDTO addItemToCartDTO) {
        int cartId = addItemToCartDTO.cartId();
        int ebookId = addItemToCartDTO.ebookId();
        int quantity = addItemToCartDTO.quantity();
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart with id" + cartId + " not found"));
        Ebook ebook = ebookRepository.findById(ebookId).orElseThrow(() -> new ResourceNotFoundException("Ebook with id" + ebookId + " not found"));

        Optional<CartItem> existingCartItem = cart.getCartItems().stream().filter(item -> item.getEbook().getId() == ebookId).findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {


            CartItem cartItem = new CartItem();
            cartItem.setEbook(ebook);
            cartItem.setQuantity(quantity);
            cartItem.setUnit_price(ebook.getPrice());
            cartItem.setCart(cart);
            cartItem.setAddedAt(LocalDateTime.now());

            cart.addCartItem(cartItem);
            cart.setLastUpdated(LocalDateTime.now());

            cartRepository.save(cart);
        }
    }

    @Override
    public void removeItemFromCart(RemoveItemFromCartDTO removeItemFromCartDTO) {
        int cartId = removeItemFromCartDTO.cartId();
        int cartItemId = removeItemFromCartDTO.cartItemId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart with id" + cartId + " not found"));

        CartItem cartItem = cart.getCartItems().stream().filter(item -> item.getId() == cartItemId).findFirst().orElseThrow(() -> new RuntimeException("CartItem not found"));
        cart.getCartItems().remove(cartItem);

        cart.setLastUpdated(LocalDateTime.now());

        cartRepository.save(cart);

    }


    //TODO Test this method
    @Override
    public CartDTO findById(int id) {
        User currentUser = userService.getCurrentUser();

        Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart with id" + id + " not found"));
        //TODO add possibility for admins
        if(cart.getUser().getId()==currentUser.getId()) {
            return mapCartToDTO(cart);
        }
        return null;
    }

    private CartDTO mapCartToDTO(Cart cart) {
        return new CartDTO(cart.getId(),
                cart.getCreatedAt(),
                cart.getLastUpdated(),
                cart.getCartItems().stream().map(this::mapCartItemToDTO).toList()
                );


    }


    private CartItemDTO mapCartItemToDTO(CartItem cartItem) {
        return new CartItemDTO(cartItem.getId(), cartItem.getEbook().getId(), cartItem.getQuantity(), cartItem.getUnit_price(), cartItem.getAddedAt());
    }

    @Override
    public void updateCartItemQuantity(UpdateCartItemDTO updateCartItemDTO) {
        int cartId = updateCartItemDTO.cartId();
        int cartItemId = updateCartItemDTO.cartItemId();
        int newQuantity = updateCartItemDTO.quantity();
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart with id" + cartId + " not found"));
        CartItem cartItem = cart.getCartItems().stream().filter(item -> item.getId() == cartItemId).findFirst().orElseThrow(() -> new RuntimeException("CartItem not found"));
        cartItem.setQuantity(newQuantity);

        cartRepository.save(cart);
    }
}