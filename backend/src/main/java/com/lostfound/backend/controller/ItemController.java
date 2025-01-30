package com.lostfound.backend.controller;

import com.lostfound.backend.model.Item;
import com.lostfound.backend.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Handles API requests for items

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // Get all items
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    // Get item by ID
    @GetMapping("/{id}")
    public Optional<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    // Search items by tags
    @GetMapping("/search")
    public List<Item> searchItemsByTags(@RequestParam String tags) {
        return itemService.searchItemsByTags(tags);
    }

    // Add a new item
    @PostMapping
    public Item addItem(@RequestBody Item item) {
        return itemService.addItem(item);
    }

    // Delete an item by ID
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
