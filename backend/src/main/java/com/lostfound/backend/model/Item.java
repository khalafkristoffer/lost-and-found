package com.lostfound.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// johannes-metoden?

@Entity
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String tags;
    private String location;
    private boolean isFound;
    // byte handling
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    // Get and Set methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String category) {
        this.tags = category;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public boolean isFound() {
        return isFound;
    }
    public void setFound(boolean found) {
        isFound = found;
    }
}