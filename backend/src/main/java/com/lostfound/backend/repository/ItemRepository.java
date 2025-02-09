package com.lostfound.backend.repository;

import java.util.*;

import com.lostfound.backend.model.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// for repo, dependencies needed

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByTagsContaining(String tag);

    @Query(value = "SELECT i FROM Item i WHERE " +
           "(:title IS NULL OR i.title LIKE %:title%) OR " +
           "(:description IS NULL OR i.description LIKE %:description%) OR " +
           "(:location IS NULL OR i.location LIKE %:location%) OR " +
           "(:tags IS NULL OR i.tags LIKE %:tags%)")
    List<Item> searchItems(
        @Param("title") String title,
        @Param("description") String description,
        @Param("location") String location,
        @Param("tags") String tags
    );
}

