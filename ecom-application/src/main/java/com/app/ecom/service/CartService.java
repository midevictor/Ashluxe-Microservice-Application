package com.app.ecom.service;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepsitory;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ProductRepository productRepository;
    private final CartItemRepsitory cartItemRepsitory;
    private final UserRepository userRepository;
    public boolean addToCart(String userId, CartItemRequest request) {
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty())
            return false;
        Product product = productOpt.get();
        if (product.getStockQuantity() < request.getQuantity())
            return false;
        Optional<User> userOpt =  userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty())
            return false;

        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepsitory.findByUserAndProduct(user, product);
        if (existingCartItem != null) {
            // Update quantity if item already exists in cart
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepsitory.save(existingCartItem);

        }else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepsitory.save(cartItem);

        }
        return true;
    }

    public boolean deleteItemFromCart(String userId, Long productId) {
        Optional<User> userOpt =  userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty())
            return false;
        User user = userOpt.get();
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty())
            return false;
        Product product = productOpt.get();
        CartItem existingCartItem = cartItemRepsitory.findByUserAndProduct(user, product);
        if (existingCartItem != null) {
            cartItemRepsitory.delete(existingCartItem);
            return true;
        }

        return false;
    }

    public List<CartItem> getCart(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepsitory::findByUser)
                .orElseGet(List::of);
    }
}
