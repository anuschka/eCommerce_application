package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    public static Item createItem(long id){
        Item item = new Item();
        item.setId(id);
        item.setPrice(BigDecimal.valueOf(id * 2000));
        item.setName("TestItem " + item.getId());
        item.setDescription("Some test Item Description ");
        return item;
    }

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
        Item item1 = createItem(123);
        Item item2 = createItem(234);
        when(itemRepository.findById((long) 123)).thenReturn(Optional.of(item1));
        when(itemRepository.findByName(item1.getName())).thenReturn(Lists.list(item1, item2));
        when(itemRepository.findAll()).thenReturn(Lists.list(item1, item2));
    }


    @Test
    public void test_get_item_by_id() {
        ResponseEntity<Item> response = itemController.getItemById((long) 123);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item i = response.getBody();
        assertNotNull(i);
    }


    @Test
    public void test_get_all_items() {
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    public void test_not_found() {
        ResponseEntity<Item> response = itemController.getItemById((long) 345);
        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void get_items_by_name() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("TestItem " + 123);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    public void test_get_by_name_not_found() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("");
        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

}