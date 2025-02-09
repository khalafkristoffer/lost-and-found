package com.lostfound.backend.service;

import com.lostfound.backend.model.Item;
import com.lostfound.backend.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.util.ArrayList;

// Handles business logic for items
@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;


    public ItemService(ItemRepository itemRepository) {
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
    public Item addItem(Item item, byte[] imageData) throws IOException {
        ImageProcessingService imageProcessingService = new ImageProcessingService();
        List<String> generatedTags = imageProcessingService.generateTags(imageData);
        item.setTags(String.join(",", generatedTags));
        item.setImage(imageData);
        return itemRepository.save(item);
    }


    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> searchItems(String title, String description, String location, String tags) {
        // If all parameters are null or empty, return empty list
        if ((title == null || title.trim().isEmpty()) &&
            (description == null || description.trim().isEmpty()) &&
            (location == null || location.trim().isEmpty()) &&
            (tags == null || tags.trim().isEmpty())) {
            logger.info("Empty search parameters, returning no results");
            return new ArrayList<>();
        }

        logger.info("Searching with parameters:");
        logger.info("Title: {}", title);
        logger.info("Description: {}", description);
        logger.info("Location: {}", location);
        logger.info("Tags: {}", tags);

        // Convert empty strings to null to match the repository query
        title = (title != null && title.trim().isEmpty()) ? null : title;
        description = (description != null && description.trim().isEmpty()) ? null : description;
        location = (location != null && location.trim().isEmpty()) ? null : location;
        tags = (tags != null && tags.trim().isEmpty()) ? null : tags;

        List<Item> results = itemRepository.searchItems(title, description, location, tags);
        logger.info("Found {} results", results.size());
        return results;
    }

}