package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void test_create_user() throws Exception  {
        when(encoder.encode("testPassword")).thenReturn("HashedPassword");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("testUser");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("HashedPassword", u.getPassword());
        assertEquals("testUser", u.getUsername());
    }

    @Test
    public void test_findById(){
        long id = 123;
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword1");
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        final ResponseEntity<User> response = userController.findById(id);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(123, u.getId());
        assertEquals("testUser", u.getUsername());
        assertEquals("testPassword1", u.getPassword());
    }

    @Test
    public void test_findByUserName(){
        long id = 123;
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword2");
        user.setId(id);
        when(userRepository.findByUsername("test")).thenReturn(user);
        final ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(123, u.getId());
        assertEquals("testUser", u.getUsername());
        assertEquals("testPassword2", u.getPassword());
    }

   
}
