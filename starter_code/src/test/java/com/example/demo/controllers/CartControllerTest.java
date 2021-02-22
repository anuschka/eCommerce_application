package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    private CartController cartController;

    private ItemRepository itemRepository = mock(ItemRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    public static User createUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setCart(createCart(user));
        return user;
    }

    public static Item createItem(long id) {
        Item item = new Item();
        item.setId(id);
        item.setPrice(BigDecimal.valueOf(id * 1000));
        item.setName("TestItem " + item.getId());
        item.setDescription("Some test Item Description ");
        return item;
    }

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(createItem(1));
        return cart;
    }

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        when(userRepository.findByUsername("testUser")).thenReturn(createUser());
        when(itemRepository.findById((long) 1)).thenReturn(Optional.of(createItem(1)));
    }

    @Test
    public void test_addTocard() {
        BigDecimal expected = new BigDecimal(6000);
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setQuantity(5);
        request.setUsername("testUser");
        final ResponseEntity<Cart> response = cartController.addTocart(request);
        Cart cart = response.getBody();
        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        assertNotNull(cart);
        assertEquals(cart.getUser().getUsername(), "testUser");
        assertEquals(cart.getItems().size(), 6);
        assertEquals(cart.getTotal(), expected);

    }

    @Test
    public void test_addToCart_userName_not_found(){
        BigDecimal expected = new BigDecimal(984000);
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(3);
        request.setItemId(123);
        request.setUsername("xxx");
        final ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void test_removeFromcard(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setQuantity(5);
        request.setUsername("testUser");
        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        Cart cart = response.getBody();
        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        assertNotNull(cart);
        assertEquals(cart.getUser().getUsername(), "testUser");
        assertEquals(cart.getItems().size(), 0);

    }
}