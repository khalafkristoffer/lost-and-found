package com.lostfound.backend.service;

import com.lostfound.backend.model.Item;
import com.lostfound.backend.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Handles business logic for items
@Service
public class ItemService {

    private final ItemRepository itemRepository;


    public ItemService(ItemRepository itemRepository, com.lostfound.backend.service.ImageProcessingService imageProcessingService) {
        this.itemRepository = itemRepository;
    }

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
    public Item addItem(Item item, byte[] bytes) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

}