
package com.lostfound.backend.repository;

import java.util.*;

import com.lostfound.backend.model.Item;

import org.springframework.data.jpa.repository.JpaRepository;
// for repo, dependencies needed

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByTagsContaining(String tag);
}

