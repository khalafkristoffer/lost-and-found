package com.lostfound.backend.controller;

import com.lostfound.backend.model.Item;
import com.lostfound.backend.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    @GetMapping("/debug")
    public ResponseEntity<String> debugDatabase() {
        List<Item> items = itemService.getAllItems();
        StringBuilder debug = new StringBuilder("Database contents:\n");
        for (Item item : items) {
            debug.append(String.format("ID: %d\nTitle: %s\nDescription: %s\nLocation: %s\nTags: %s\n\n",
                item.getId(), item.getTitle(), item.getDescription(), item.getLocation(), item.getTags()));
        }
        return ResponseEntity.ok(debug.toString());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Item>> getAllItems() {
        try {
            List<Item> items = itemService.getAllItems();
            logger.info("Found {} items in database", items.size());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error getting all items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String tags) {
        try {
            List<Item> items = itemService.searchItems(title, description, location, tags);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error searching items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/upload")
    public ResponseEntity<Item> addItem(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received upload request - Title: {}, Description: {}, Location: {}, File size: {}", 
                       title, description, location, file.getSize());
            
            Item item = new Item();
            item.setTitle(title);
            item.setDescription(description);
            item.setLocation(location);
            item.setFound(false);
            
            return ResponseEntity.ok(itemService.addItem(item, file.getBytes()));
        } catch (Exception e) {
            logger.error("Error uploading item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
