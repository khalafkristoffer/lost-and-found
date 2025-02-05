package com.lostfound.backend.controller;

import com.lostfound.backend.model.Item;
import com.lostfound.backend.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Optional<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<Item> searchItemsByTags(@RequestParam String tags) {
        return itemService.searchItemsByTags(tags);
    }

    @PostMapping("/upload")
    public Item addItem(@RequestParam("title") String title,
                        @RequestParam("file") MultipartFile file) throws IOException {
        Item item = new Item();
        item.setTitle(title);
        return itemService.addItem(item, file.getBytes());
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
