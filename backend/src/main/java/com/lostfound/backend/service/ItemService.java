package com.lostfound.backend.service;

import com.lostfound.backend.model.Item;
import com.lostfound.backend.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Handles business logic for items

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // Get all items
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Get item by ID
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    // Search items by tags
    public List<Item> searchItemsByTags(String tags) {
        return itemRepository.findByTagsContaining(tags);
    }

    // Add a new item
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    // Delete an item by ID
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
