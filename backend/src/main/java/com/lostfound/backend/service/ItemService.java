package com.lostfound.backend.service;

import com.lostfound.backend.model.Item;
import com.lostfound.backend.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> getItemsByCategory(String tags) {
        return itemRepository.findByTagsContaining(tags);
    }
}